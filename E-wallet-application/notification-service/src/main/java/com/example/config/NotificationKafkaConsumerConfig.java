package com.example.config;

import com.example.dto.UserCreatedPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class NotificationKafkaConsumerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationKafkaConsumerConfig.class);
    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @KafkaListener(topics = "${user.created.topic}", groupId = "email")
    public void consumerUserCreateTopic(String payload){
        UserCreatedPayload userCreatedPayload = OBJECT_MAPPER.readValue(payload.value().toString(), UserCreatedPayload.class);
        MDC.put("requestId", userCreatedPayload.getRequestId());
        LOGGER.info("Read from kafka : {}", userCreatedPayload);

        //Sending email



        MDC.clear();
    }
}
