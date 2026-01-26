package com.example.config;


import com.example.dto.UserCreatedPayload;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Configuration
public class NotificationKafkaConsumerConfig {
    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @KafkaListener(topics = "${user.created.topic}", groupId = "email")
    public void consumeUserCreatedTopic(ConsumerRecord payload){
        UserCreatedPayload userCreatedPayload = OBJECT_MAPPER.readValue(payload.value().toString(), UserCreatedPayload.class);
        MDC.put("requestId", userCreatedPayload.getRequestId());
        log.info("Read from kafka : {}", userCreatedPayload);

        // Send Email
        JavaMailSender

        MDC.clear();
    }
}
