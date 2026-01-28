package com.example.service;

import com.example.dto.TxnCompletedPayload;
import com.example.dto.TxnInitPayload;
import com.example.dto.WalletBalanceDTO;
import com.example.dto.WalletUpdatePayload;
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

    @Value("${wallet.updated.topic}")
    private String walletUpdatedTopic;

    public WalletBalanceDTO walletBalance(Long userId){
        Wallet wallet = walletRepo.findByUserId(userId);
        WalletBalanceDTO walletBalanceDTO = new WalletBalanceDTO();
        walletBalanceDTO.setBalance(wallet.getBalance());
        return walletBalanceDTO;
    }

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

            WalletUpdatePayload walletUpdatedPayload1 = new WalletUpdatePayload(
                    fromWallet.getUserEmail(),
                    fromWallet.getBalance(),
                    txnInitPayload.getRequestId()
            );

            WalletUpdatePayload walletUpdatedPayload2 = new WalletUpdatePayload(
                    toWallet.getUserEmail(),
                    toWallet.getBalance(),
                    txnInitPayload.getRequestId()
            );

            Future<SendResult<String,Object>> walletUpdatedFuture1  = kafkaTemplate.send(walletUpdatedTopic,walletUpdatedPayload1.getUserEmail(),walletUpdatedPayload1);
            log.info("Pushed WalletUpdated to kafka: {}",walletUpdatedFuture1.get());

            Future<SendResult<String,Object>> walletUpdatedFuture2  = kafkaTemplate.send(walletUpdatedTopic,walletUpdatedPayload2.getUserEmail(),walletUpdatedPayload2);
            log.info("Pushed WalletUpdated to kafka: {}",walletUpdatedFuture2.get());
        }
        Future<SendResult<String,Object>> future  = kafkaTemplate.send(txnCompletedTopic,txnInitPayload.getFromUserId().toString(),txnCompletedPayload);
        log.info("Pushed TxnCompleted to kafka: {}",future.get());
    }

}
