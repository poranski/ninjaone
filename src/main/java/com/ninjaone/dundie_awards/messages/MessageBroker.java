package com.ninjaone.dundie_awards.messages;

import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class MessageBroker {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBroker.class);
    private final RabbitTemplate rabbitTemplate;
    private final ConcurrentLinkedQueue<Activity> messagesQueue;

    public MessageBroker(RabbitTemplate rabbitTemplate) {
        this.messagesQueue = new ConcurrentLinkedQueue<>();
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message) {
        LOGGER.info("Sending message to rabbitMQ [Message: {}]", message);
        Activity activity = new Activity(LocalDateTime.now(), message);
        messagesQueue.add(activity);
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, message);
    }

    public void receiveMessage(String message) {
        LOGGER.info("Received message from rabbitMQ [Message: {}]", message);
    }

    public Queue<Activity> getMessages(){
        return messagesQueue;
    }
}
