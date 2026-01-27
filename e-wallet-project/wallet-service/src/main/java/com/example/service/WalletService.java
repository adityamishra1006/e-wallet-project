package com.example.service;

import com.example.dto.TxnCompletedPayload;
import com.example.dto.TxnInitPayload;
import com.example.entity.Wallet;
import com.example.repo.WalletRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepo walletRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${txn.completed.topic}")
    private String txnCompletedTopic;

    @Transactional
    public void walletTxn(TxnInitPayload txnInitPayload) throws ExecutionException, InterruptedException {
        Wallet fromWallet = walletRepo.findByUserId(txnInitPayload.getFromUserId());
        TxnCompletedPayload txnCompletedPayload = new TxnCompletedPayload();
        txnCompletedPayload.setRequestId(txnInitPayload.getRequestId());
        txnCompletedPayload.setId(txnInitPayload.getId());

        if(fromWallet.getBalance() < txnInitPayload.getAmount()){
            txnCompletedPayload.setSuccess(false);
            txnCompletedPayload.setReason("Insufficient Balance");
        } else{
            Wallet toWallet = walletRepo.findByUserId(txnInitPayload.getToUserId());

            fromWallet.setBalance(fromWallet.getBalance() - txnInitPayload.getAmount());
            toWallet.setBalance(toWallet.getBalance() + txnInitPayload.getAmount());

            walletRepo.save(fromWallet);
            walletRepo.save(toWallet);

            txnCompletedPayload.setSuccess(true);
        }
        Future<SendResult<String,Object>> future  = kafkaTemplate.send(txnCompletedTopic,txnInitPayload.getFromUserId().toString(),txnCompletedPayload);
        log.info("Pushed TxnCompleted to kafka: {}",future.get());
    }

}
