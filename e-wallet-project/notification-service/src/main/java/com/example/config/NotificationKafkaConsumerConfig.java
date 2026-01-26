package com.example.config;


import com.example.dto.UserCreatedPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class NotificationKafkaConsumerConfig {
    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final JavaMailSender javaMailSender;

    @KafkaListener(topics = "${user.created.topic}", groupId = "email")
    public void consumeUserCreatedTopic(ConsumerRecord payload){
        UserCreatedPayload userCreatedPayload = OBJECT_MAPPER.readValue(payload.value().toString(), UserCreatedPayload.class);
        MDC.put("requestId", userCreatedPayload.getRequestId());
        log.info("Read from kafka : {}", userCreatedPayload);

//        emzq mhdj dioc kkio

        // Send Email
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("mishraaditya6001@gmail.com");
        simpleMailMessage.setSubject("Welcome " + userCreatedPayload.getUserName());
        simpleMailMessage.setText("Hi "+userCreatedPayload.getUserName()+", Thank you for registering with us. We hope you enjoy our services.");
        simpleMailMessage.setCc("vijay.jbdl@yopmail.com");
        simpleMailMessage.setTo(userCreatedPayload.getUserEmail());
        javaMailSender.send(simpleMailMessage);

        MDC.clear();
    }
}
