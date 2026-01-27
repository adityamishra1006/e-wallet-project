package com.example.config;

import com.example.dto.TxnCompletedPayload;
import com.example.dto.TxnInitPayload;
import com.example.entity.Transaction;
import com.example.entity.TxnStatus;
import com.example.repo.TransactionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.ExecutionException;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class TransactionKafkaConsumerConfig {

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final TransactionRepo transactionRepo;


    @KafkaListener(topics = "${txn.complete.topic}", groupId = "transaction")
    public void consumeTxnInitTopic(ConsumerRecord payload) throws ExecutionException, InterruptedException {
        TxnCompletedPayload txnCompletedPayload = OBJECT_MAPPER.readValue(payload.value().toString(), TxnCompletedPayload.class);
        MDC.put("requestId", txnCompletedPayload.getRequestId());
        log.info("Read from kafka  : {}", txnCompletedPayload);
        Transaction transaction = transactionRepo.findById(txnCompletedPayload.getId()).get();
        if(!txnCompletedPayload.getSuccess()){
            transaction.setStatus(TxnStatus.FAILED);
            transaction.setReason(txnCompletedPayload.getReason());
        } else{
            transaction.setStatus(TxnStatus.SUCCESS);
        }
        transactionRepo.save(transaction);
        MDC.clear();
    }
}
