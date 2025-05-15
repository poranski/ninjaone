package com.ninjaone.dundie_awards.messages;

import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.config.RabbitMQConfig;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.service.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MessageBroker {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBroker.class);
    private final RabbitTemplate rabbitTemplate;
    private final ActivityService activityService;
    private final ConcurrentLinkedQueue<Activity> messagesQueue;
    private final ConcurrentLinkedQueue<HashMap<Integer, Integer>> incompletBatches = new ConcurrentLinkedQueue<>();
    private final AtomicInteger batchNumber = new AtomicInteger(0);

    public MessageBroker(RabbitTemplate rabbitTemplate, ActivityService activityService) {
        this.messagesQueue = new ConcurrentLinkedQueue<>();
        this.rabbitTemplate = rabbitTemplate;
        this.activityService = activityService;
    }

    @CacheEvict(value = "activities", allEntries = true)
    public void sendMultipleTransactionalMessages(List<Employee> employees) {
        int currentBatchNumber = this.batchNumber.incrementAndGet();
        incompletBatches.add(new HashMap<>(currentBatchNumber, employees.size()));

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
}
