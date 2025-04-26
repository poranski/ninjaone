package com.ninjaone.dundie_awards;

import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.rabbitMQ.RabbitMQSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class MessageBroker {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBroker.class);
    private final RabbitMQSender rabbitMQSender;

    public MessageBroker(RabbitMQSender rabbitMQSender) {
        this.rabbitMQSender = rabbitMQSender;
    }

    private List<Activity> messages = new LinkedList<>();

    public void sendMessage(Activity message) {
        LOGGER.info("Sending message to rabbitMQ [Message: {}]", message);
        rabbitMQSender.send("getting all employees");
    }

    // needs to be Activity
    public void receiveMessage(String message) {
        LOGGER.info("Received message from rabbitMQ [Message: {}]", message);
    }

    public List<Activity> getMessages(){
        return messages;
    }
}
