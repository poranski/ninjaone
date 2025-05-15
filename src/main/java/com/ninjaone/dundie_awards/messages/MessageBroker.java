package com.ninjaone.dundie_awards.messages;

import com.ninjaone.dundie_awards.cache.AwardsCache;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.config.RabbitMQConfig;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.service.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
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
    private final ConcurrentLinkedQueue<Activity> messagesQueue;
    private final ConcurrentHashMap<Integer, Integer> incompleteBatches = new ConcurrentHashMap<>();
    private final AtomicInteger batchNumber = new AtomicInteger(0);
    private static final int WAIT_TIME_IN_MINUTES = 1;

    public MessageBroker(RabbitTemplate rabbitTemplate, ActivityService activityService, EmployeeRepository employeeRepository,
                         AwardsCache awardsCache) {
        this.messagesQueue = new ConcurrentLinkedQueue<>();
        this.rabbitTemplate = rabbitTemplate;
        this.activityService = activityService;
        this.employeeRepository = employeeRepository;
        this.awardsCache = awardsCache;
    }

    @CacheEvict(value = "activities", allEntries = true)
    public void sendMultipleTransactionalMessages(List<Employee> employees) {
        int currentBatchNumber = this.batchNumber.incrementAndGet();
        incompleteBatches.put(currentBatchNumber, employees.size());

        LOGGER.info("Sending multiple messages to rabbitMQ [Batch Number: {}] [Batch Size: {}]",
            currentBatchNumber, employees.size() );

        for(Employee employee : employees) {
            String message = String.format("%s %s got an Award", employee.getFirstName(), employee.getLastName());
            Activity activity = new Activity(new Date(), message, currentBatchNumber, employee.getId());
            messagesQueue.add(activity);
            sendMessage(activity);
        }
    }

    public void sendMessage(Activity activity) {
        LOGGER.info("Sending message to rabbitMQ [Activity: {}]", activity);
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, activity);
    }

    public void receiveMessage(Activity activity) {
        LOGGER.info("Received message from rabbitMQ [Message: {}]", activity);
        activityService.saveActivity(activity);
        messagesQueue.add(activity);
    }

    public Queue<Activity> getMessages(){
        return messagesQueue;
    }



    @Scheduled(cron = "0 * * * * *")
    private void checkForIncompleteBatches() {
        incompleteBatches.forEach((batchNumber, employeeCount) -> {

            if(isBatchComplete(batchNumber, employeeCount)) {
                LOGGER.info("All messages for batch {} have been processed", batchNumber);
                incompleteBatches.remove(batchNumber);
            }

            List<Activity> incompleteActivities = getActivitiesByBatchNumber(batchNumber);
            Date date = getLatestTimestamp(incompleteActivities);

            if (isOlderThanWaitTime(date)) {
                LOGGER.info("There are messages for batch {} have not been processed", batchNumber);
                rollBack(incompleteActivities);
                incompleteBatches.remove(batchNumber);
            }
        });
    }

    private void rollBack(List<Activity> activities) {
        for (Activity activity : activities) {
            LOGGER.info("Rolling back activity [Activity: {}]", activity);
            employeeRepository.findById(activity.getEmployeeId());
            messagesQueue.remove(activity);
            awardsCache.removeOneAward();
        }
    }

    private boolean isBatchComplete(int batchNumber, int employeeCount) {
        for (Activity activity : messagesQueue) {
            if (activity.getBatchNumber() == batchNumber) {
                employeeCount--;
                if (employeeCount == 0) {
                    LOGGER.info("Batch {} is complete", batchNumber);
                    return true;
                }
           }
        }

        return false;
    }

    private boolean isOlderThanWaitTime(Date date) {
        long now = System.currentTimeMillis();
        long threshold = now - WAIT_TIME_IN_MINUTES * 60 * 1000;
        return date.getTime() < threshold;
    }

    private List<Activity> getActivitiesByBatchNumber(int batchNumber) {
        return messagesQueue.stream()
            .filter(activity -> activity.getBatchNumber() == batchNumber)
            .toList();
    }

    private Date getLatestTimestamp(List<Activity> activity) {
        return activity.stream()
            .map(Activity::getOccuredAt)
            .max(Date::compareTo)
            .orElse(null);
    }

}
