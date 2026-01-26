package com.example.service;

import com.example.dto.TransactionRequestDTO;
import com.example.dto.TransactionStatusDTO;
import com.example.dto.TxnInitPayload;
import com.example.entity.Transaction;
import com.example.entity.TxnStatus;
import com.example.repo.TransactionRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
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

    private final TransactionRepo transactionRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${txn.init.topic}")
    private String txnInitTopic;

    @Transactional
    public String initTransaction(TransactionRequestDTO requestDTO) throws ExecutionException, InterruptedException {
        Transaction transaction = new Transaction();
        transaction.setFromUserId(requestDTO.getFromUserId());
        transaction.setToUserId(requestDTO.getToUserId());
        transaction.setAmount(requestDTO.getAmount());
        transaction.setComment(requestDTO.getComment());
        transaction.setTxnId(UUID.randomUUID().toString());
        transaction.setStatus(TxnStatus.PENDING);
        transaction = transactionRepo.save(transaction);

        //publish to kafka

        TxnInitPayload txnInitPayload = new TxnInitPayload();
        txnInitPayload.setId(transaction.getId());
        txnInitPayload.setFromUserId(transaction.getFromUserId());
        txnInitPayload.setToUserId(transaction.getToUserId());
        txnInitPayload.setAmount(transaction.getAmount());
        txnInitPayload.setRequestId(MDC.get("requestId"));
        Future<SendResult<String,Object>> future  = kafkaTemplate.send(txnInitTopic,transaction.getFromUserId().toString(),txnInitPayload);
        log.info("Pushed txnInitPayload to kafka: {}",future.get());

        return transaction.getTxnId();
    }

    public TransactionStatusDTO getStatus(String txnId){
        Transaction transaction = transactionRepo.findByTxnId(txnId);
        TransactionStatusDTO txnStatusDto = new TransactionStatusDTO();
        if(transaction != null){
            txnStatusDto.setReason(transaction.getReason());
            txnStatusDto.setStatus(transaction.getStatus());
        }
        return txnStatusDto;
    }
}
