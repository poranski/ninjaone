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
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

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

            //for testing: if(employee.getId() != 2) {
                sendMessage(activity);
            //}
        }
    }

    public void sendMessage(Activity activity) {
        LOGGER.info("Sending message to rabbitMQ [Activity: {}]", activity);
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, activity);
    }

    public void receiveMessage(Activity activity) {
        LOGGER.info("Received message from rabbitMQ [Message: {}]", activity);

        markActivityCompleted(activity);
        activityService.saveActivity(activity);
    }

    public Queue<Activity> getMessages(){
        return messagesQueue;
    }

    @Scheduled(cron = "0 * * * * *")
    public void checkForIncompleteBatches() {
        incompleteBatches.forEach((currentBatchNumber, employeeCount) -> {

            List<Activity> incompleteActivities = getActivitiesByBatchNumber(currentBatchNumber);

            if(batchCompete(incompleteActivities)) {
                LOGGER.info("All messages for batch {} have been processed", batchNumber);
                incompleteBatches.remove(currentBatchNumber);

            } else {
                Date date = getLatestTimestamp(incompleteActivities);

                if (isOlderThanWaitTime(date)) {
                    LOGGER.info("There are messages for batch {} have not been processed", currentBatchNumber);
                    rollBack(incompleteActivities);
                    incompleteBatches.remove(currentBatchNumber);
                }
            }
        });
    }

    private boolean batchCompete(List<Activity> incompleteActivities) {
        for (Activity activity : incompleteActivities) {
            if (!activity.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    private void rollBack(List<Activity> activities) {
        for (Activity activity : activities) {
            LOGGER.info("Rolling back activity [Activity: {}]", activity);
            Optional<Employee> employee = employeeRepository.findById(activity.getEmployeeId());

            if (employee.isPresent()) {
                Employee emp = employee.get();
                emp.setDundieAwards(emp.getDundieAwards() - 1);
                employeeRepository.save(emp);
                LOGGER.info("Removed an award [Employee: {} {}]", emp.getFirstName(), emp.getLastName());
            }

            messagesQueue.remove(activity);
            awardsCache.removeOneAward();
        }
    }

    private boolean isOlderThanWaitTime(Date date) {
        long now = System.currentTimeMillis();
        long threshold = now - WAIT_TIME_IN_MINUTES * 60 * 1000;
        // for testing: long threshold = now - 10 * 1000;
        return date.getTime() < threshold;
    }

    private List<Activity> getActivitiesByBatchNumber(int batchNumber) {
        return messagesQueue.stream()
            .filter(activity -> activity.getBatchNumber() == batchNumber)
            .toList();
    }

    private Date getLatestTimestamp(List<Activity> activity) {
        return activity.stream()
            .map(Activity::getOccurredAt)
            .max(Date::compareTo)
            .orElse(null);
    }

    private void markActivityCompleted(Activity activity) {
        for(Activity existingActivity : messagesQueue) {
            if(existingActivity.equals(activity)) {
                existingActivity.setCompleted(true);
                break;
            }
        }
    }
}
