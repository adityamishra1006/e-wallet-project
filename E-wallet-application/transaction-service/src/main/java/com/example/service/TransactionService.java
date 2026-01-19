package com.example.service;

import com.example.dto.TxnRequestDTO;
import com.example.entity.Transaction;
import com.example.entity.TxnStatusEnum;
import com.example.repo.TransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepo transactionRepo;

    public String initTransaction(TxnRequestDTO txnRequestDTO){
        Transaction transaction = new Transaction();
        transaction.setFromUserId(txnRequestDTO.getFromUserId());
        transaction.setToUserId(txnRequestDTO.getToUserId());
        transaction.setAmount(txnRequestDTO.getAmount());
        transaction.setComment(txnRequestDTO.getComment());
        transaction.setTxnId(UUID.randomUUID().toString());
        transaction.setStatus(TxnStatusEnum.PENDING);
        transactionRepo.save(transaction);
    }
}

//2:18
