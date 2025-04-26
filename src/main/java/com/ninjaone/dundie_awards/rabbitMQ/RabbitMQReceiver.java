package com.ninjaone.dundie_awards.rabbitMQ;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQReceiver.class);

    public void receiveMessage(String message) {
        LOGGER.info("Received message from rabbitMQ [Message: {}]", message);
    }
}

