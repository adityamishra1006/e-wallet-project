package com.example.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserCreatedConsumer {

    @KafkaListener(topics = "USER-CREATED", groupId = "user-group")
    public void listen(String message) {
        System.out.println("Received USER-CREATED event: " + message);
    }
}
