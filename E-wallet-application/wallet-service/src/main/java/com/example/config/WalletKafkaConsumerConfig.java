package com.example.config;

import com.example.dto.UserCreatedPayload;
import com.example.entity.Wallet;
import com.example.repo.WalletRepo;
import com.example.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import tools.jackson.databind.ObjectMapper;

@Configuration
@RequiredArgsConstructor
public class WalletKafkaConsumerConfig {
    private static Logger LOGGER = LoggerFactory.getLogger(WalletKafkaConsumerConfig.class);

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final WalletRepo walletRepo;

    private final WalletService walletService;

    @KafkaListener(topics = "${user.created.topic}", groupId = "wallet")
    public void consumerUserCreateTopic(ConsumerRecord payload){
        UserCreatedPayload userCreatedPayload = OBJECT_MAPPER.readValue(payload.value().toString(), UserCreatedPayload.class);
        MDC.put("requestId", userCreatedPayload.getRequestId());
        LOGGER.info("Read from kafka : {}", userCreatedPayload);
        Wallet wallet = new Wallet();
        wallet.setBalance(100.00);
        wallet.setUserId(userCreatedPayload.getUserId());
        wallet.setUserEmail(userCreatedPayload.getUserEmail());

        walletRepo.save(wallet);

        MDC.clear();
    }
}
