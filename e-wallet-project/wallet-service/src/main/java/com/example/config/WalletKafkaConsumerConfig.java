package com.example.config;

import com.example.dto.UserCreatedPayload;
import com.example.entity.Wallet;
import com.example.repo.WalletRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import tools.jackson.databind.ObjectMapper;

@Configuration
@Slf4j
public class WalletKafkaConsumerConfig {
    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private WalletRepo walletRepo;

//    @Autowired
//    private WalletService walletService;

    @KafkaListener(topics = "${user.created.topic}", groupId = "wallet")
    public void consumeUserCreatedTopic(ConsumerRecord payload){
        UserCreatedPayload userCreatedPayload = OBJECT_MAPPER.readValue(payload.value().toString(), UserCreatedPayload.class);
        MDC.put("requestId", userCreatedPayload.getRequestId());
        log.info("Read from kafka : {}", userCreatedPayload);

        Wallet wallet = new Wallet();
        wallet.setBalance(100.00);
        wallet.setUserId(userCreatedPayload.getUserId());
        wallet.setUserEmail(userCreatedPayload.getUserEmail());

        walletRepo.save(wallet);

        MDC.clear();
    }
}
