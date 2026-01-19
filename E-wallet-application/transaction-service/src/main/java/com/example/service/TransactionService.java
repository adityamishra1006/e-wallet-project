package com.example.service;

import com.example.dto.TxnInitPayload;
import com.example.dto.TxnRequestDTO;
import com.example.dto.TxnStatusDTO;
import com.example.entity.Transaction;
import com.example.entity.TxnStatusEnum;
import com.example.repo.TransactionRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepo txnRepo;

    private final TransactionRepo transactionRepo;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${txn.init.topic}")
    private String txnInitTopic;

    @Transactional
    public String initTransaction(TxnRequestDTO txnRequestDTO) throws ExecutionException, InterruptedException {
        Transaction transaction = new Transaction();
        transaction.setFromUserId(txnRequestDTO.getFromUserId());
        transaction.setToUserId(txnRequestDTO.getToUserId());
        transaction.setAmount(txnRequestDTO.getAmount());
        transaction.setComment(txnRequestDTO.getComment());
        transaction.setTxnId(UUID.randomUUID().toString());
        transaction.setStatus(TxnStatusEnum.PENDING);
        transactionRepo.save(transaction);

        // Kafka

        TxnInitPayload txnInitPayload = new TxnInitPayload();
        txnInitPayload.setId(transaction.getId());
        txnInitPayload.setFromUserId(transaction.getFromUserId());
        txnInitPayload.setToUserId(transaction.getToUserId());
        txnInitPayload.setAmount(transaction.getAmount());
        txnInitPayload.setRequestId(MDC.get("requestId"));

        Future<SendResult<String, Object>> future = kafkaTemplate.send(txnInitTopic, transaction.getFromUserId());
        log.info("Pushed txnInitPayload to kafka: {}", future.get());

        return transaction.getTxnId();
    }

    public TxnStatusDTO getStatus(String txnId){
        Transaction transaction = txnRepo.findByTxnId(txnId);
        TxnStatusDTO txnStatusDto = new TxnStatusDTO();
        if(transaction != null){
            txnStatusDto.setReason(transaction.getReason());
            txnStatusDto.setStatus(transaction.getStatus().toString());
        }
        return txnStatusDto;
    }
}

//2:18
