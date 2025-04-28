package com.ninjaone.dundie_awards.messages;

import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.rabbitmq.RabbitMQSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class MessageBroker {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBroker.class);
    private final RabbitMQSender rabbitMQSender;
    private final ConcurrentLinkedQueue<Activity> messagesQueue;

    public MessageBroker(RabbitMQSender rabbitMQSender) {
        this.messagesQueue = new ConcurrentLinkedQueue<>();
        this.rabbitMQSender = rabbitMQSender;
    }

    public void sendMessage(String message) {
        LOGGER.info("Sending message to rabbitMQ [Message: {}]", message);
        Activity activity = new Activity(LocalDateTime.now(), message);
        messagesQueue.add(activity);
        rabbitMQSender.send("message");
    }

    public void receiveMessage(String message) {
        LOGGER.info("Received message from rabbitMQ [Message: {}]", message);
    }

    public Queue<Activity> getMessages(){
        return messagesQueue;
    }
}
