package com.example.config;

import com.example.dto.UserCreatedPayload;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import tools.jackson.databind.ObjectMapper;

@Configuration
@RequiredArgsConstructor
public class NotificationKafkaConsumerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationKafkaConsumerConfig.class);
    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final JavaMailSender javaMailSender;

    @KafkaListener(topics = "${user.created.topic}", groupId = "email")
    public void consumerUserCreateTopic(ConsumerRecord payload){
        UserCreatedPayload userCreatedPayload = OBJECT_MAPPER.readValue(payload.value().toString(), UserCreatedPayload.class);
        MDC.put("requestId", userCreatedPayload.getRequestId());
        LOGGER.info("Read from kafka : {}", userCreatedPayload);

        //Sending email

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("mishraaditya6001@gmail.com");
        simpleMailMessage.setSubject("Welcome " + userCreatedPayload.getUserName());
        simpleMailMessage.setText("Hi " + userCreatedPayload.getUserName()+ ", Thank you for registering with us and welcome to our community");
        simpleMailMessage.setCc("aditya.jbdl@yopmail.com");
        simpleMailMessage.setTo(userCreatedPayload.getUserEmail());
        javaMailSender.send(simpleMailMessage);

        MDC.clear();
    }
}
