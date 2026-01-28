package com.example.service;


import com.example.client.WalletServiceClient;
import com.example.dto.UserCreatedPayload;
import com.example.dto.UserDTO;
import com.example.dto.UserProfileDTO;
import com.example.dto.WalletBalanceDTO;
import com.example.entity.User;
import com.example.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final WalletServiceClient walletServiceClient;

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

        // Kafka Push

        UserCreatedPayload userCreatedPayload = new UserCreatedPayload();
        userCreatedPayload.setUserEmail(user.getEmail());
        userCreatedPayload.setUserId(user.getId());
        userCreatedPayload.setUserName(user.getName());
        userCreatedPayload.setRequestId(MDC.get("requestId"));

        Future<SendResult<String, Object>> future = kafkaTemplate.
                send(userCreatedTopic, userCreatedPayload.getUserEmail(), userCreatedPayload);

        log.info("Pushed userCreatedPayload to Kafka : {}", future.get());

        return user.getId();
    }

    public UserProfileDTO getUserProfile(Long userId){
        User user = userRepo.findById(userId).get();
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setPhone(user.getPhone());
        userDTO.setKycNumber(user.getKycNumber());
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setUserDetails(userDTO);
        //Call API of Wallet Service
        WalletBalanceDTO walletBalanceDTO = walletServiceClient.getBalance(userId).getBody();
        userProfileDTO.setWalletBalance(walletBalanceDTO.getBalance());
        return userProfileDTO;
    }
}