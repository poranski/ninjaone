package com.ninjaone.dundie_awards.messages;

import com.ninjaone.dundie_awards.cache.AwardsCache;
import com.ninjaone.dundie_awards.dto.ActivityDTO;
import com.ninjaone.dundie_awards.config.RabbitMQConfig;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.service.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MessageBroker {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBroker.class);
    private final RabbitTemplate rabbitTemplate;
    private final ActivityService activityService;
    private final AwardsCache awardsCache;
    private final EmployeeRepository employeeRepository;
    private final ConcurrentLinkedQueue<ActivityDTO> messagesQueue;
    private final ConcurrentHashMap<Integer, Integer> incompleteBatches = new ConcurrentHashMap<>();
    private final AtomicInteger batchNumber = new AtomicInteger(0);

    @Value("${messageBroker.wait-time-in-minutes:1}")
    private int waitTimeInMinutes;

    public MessageBroker(RabbitTemplate rabbitTemplate, ActivityService activityService, EmployeeRepository employeeRepository,
                         AwardsCache awardsCache) {
        this.messagesQueue = new ConcurrentLinkedQueue<>();
        this.rabbitTemplate = rabbitTemplate;
        this.activityService = activityService;
        this.employeeRepository = employeeRepository;
        this.awardsCache = awardsCache;
    }

    public void sendMultipleTransactionalMessages(List<Employee> employees) {
        int currentBatchNumber = batchNumber.incrementAndGet();
        int batchSize = employees.size();
        incompleteBatches.put(currentBatchNumber, batchSize);

        LOGGER.info("Sending multiple messages to RabbitMQ [Batch Number: {}] [Batch Size: {}]",
            currentBatchNumber, batchSize);

        employees.forEach(employee -> {
            ActivityDTO activity = createActivityDTO(employee, currentBatchNumber);
            messagesQueue.add(activity);
            sendMessage(activity);
        });
    }

    private ActivityDTO createActivityDTO(Employee employee, int batchNumber) {
        String message = String.format("%s %s got an Award", employee.getFirstName(), employee.getLastName());
        return new ActivityDTO(new Date(), message, batchNumber, employee.getId());
    }


    public void sendMessage(ActivityDTO activityDTO) {
        LOGGER.info("Sending message to rabbitMQ [Activity: {}]", activityDTO);
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, activityDTO);
    }

    public void receiveMessage(ActivityDTO activityDTO) {
        LOGGER.info("Received message from rabbitMQ [Message: {}]", activityDTO);
        markActivityCompleted(activityDTO);
        activityService.saveActivity(activityDTO);
    }

    public Queue<ActivityDTO> getMessages(){
        return messagesQueue;
    }

    //  This will run every minute looking for messages that have not been delivered in the wait time
    //  which messageBroker.wait-time-in-minutes is in the application yaml file defaulting to 1 minute
    @Scheduled(cron = "0 * * * * *")
    public void checkForIncompleteBatches() {
        incompleteBatches.forEach((currentBatchNumber, employeeCount) -> {

            List<ActivityDTO> incompleteActivities = getActivitiesByBatchNumber(currentBatchNumber);

            if(batchCompete(incompleteActivities)) {
                LOGGER.info("All messages for batch {} have been processed", batchNumber);
                incompleteBatches.remove(currentBatchNumber);

            } else {
                Date date = getMostRecentOccurredAt(incompleteActivities);

                if (isOlderThanWaitTime(date)) {
                    LOGGER.info("There are messages for batch {} have not been processed", currentBatchNumber);
                    rollBack(incompleteActivities);
                    incompleteBatches.remove(currentBatchNumber);
                }
            }
        });
    }

    private boolean batchCompete(List<ActivityDTO> incompleteActivities) {
        for (ActivityDTO activity : incompleteActivities) {
            if (!activity.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    private void rollBack(List<ActivityDTO> activities) {
        for (ActivityDTO activity : activities) {
            LOGGER.info("Rolling back activity [Activity: {}]", activity);

            employeeRepository.findById(activity.getEmployeeId()).ifPresent(employee -> {
                employee.setDundieAwards(employee.getDundieAwards() - 1);
                employeeRepository.save(employee);
                LOGGER.info("Removed an award [Employee: {} {}]", employee.getFirstName(), employee.getLastName());
            });

            messagesQueue.remove(activity);
            awardsCache.removeOneAward();
        }
    }


    private boolean isOlderThanWaitTime(Date date) {
        long now = System.currentTimeMillis();
        long threshold = now - waitTimeInMinutes * 60 * 1000;
        return date.getTime() < threshold;
    }

    private List<ActivityDTO> getActivitiesByBatchNumber(int batchNumber) {
        return messagesQueue.stream()
            .filter(activity -> activity.getBatchNumber() == batchNumber)
            .toList();
    }

    public Date getMostRecentOccurredAt(List<ActivityDTO> activities) {
        return activities.stream()
            .map(ActivityDTO::getOccurredAt)
            .filter(Objects::nonNull)
            .max(Date::compareTo)
            .orElse(null);
    }

    private void markActivityCompleted(ActivityDTO activity) {
        messagesQueue.stream()
            .filter(activity::equals)
            .findFirst()
            .ifPresent(a -> a.setCompleted(true));
    }

}
