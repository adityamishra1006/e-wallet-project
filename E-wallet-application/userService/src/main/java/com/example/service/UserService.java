package com.example.service;

import com.example.dto.UserCreatedPayload;
import com.example.dto.UserDTO;
import com.example.entity.User;
import com.example.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class UserService {

    private static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepo userRepo;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${user.created.topic}")
    private String userCreatedTopic;

    @Transactional
    public Long createUser(UserDTO userDTO) throws ExecutionException, InterruptedException {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setKycNumber(userDTO.getKycNumber());

        user = userRepo.save(user);

        UserCreatedPayload userCreatedPayload = new UserCreatedPayload();
        userCreatedPayload.setUserEmail(user.getEmail());
        userCreatedPayload.setUserId(user.getId());
        userCreatedPayload.setUserName(user.getName());
        userCreatedPayload.setRequestId(MDC.get("requestId"));

        String json = objectMapper.writeValueAsString(userCreatedPayload);

        Future<SendResult<String, Object>> future = kafkaTemplate.
                send(userCreatedTopic, userCreatedPayload.getUserEmail(), json);

        LOGGER.info("Pushed userCreatedPayload to kafka topic: {}", future.get());

        return user.getId();
    }
}
