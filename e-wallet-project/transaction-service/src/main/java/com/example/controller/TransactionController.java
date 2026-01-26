package com.example.controller;

import com.example.dto.TransactionRequestDTO;
import com.example.dto.TransactionStatusDTO;
import com.example.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/transaction-service")
@Slf4j
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/txn")
    public ResponseEntity<String> initTransaction(@RequestBody @Valid TransactionRequestDTO requestDTO) throws ExecutionException, InterruptedException {
        log.info("Starting transaction : {}", requestDTO);

        String txnId = transactionService.initTransaction(requestDTO);
        return ResponseEntity.accepted().body(txnId);
    }

    @GetMapping("/status/{txnId}")
    public ResponseEntity<TransactionStatusDTO> getTxnStatus(@PathVariable String txnId){
        return ResponseEntity.ok(transactionService.getStatus(txnId));
    }

}
