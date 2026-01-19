package com.example.controller;

import com.example.dto.TxnRequestDTO;
import com.example.dto.TxnStatusDTO;
import com.example.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/transaction-service")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private static Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    @PostMapping("/txn")
    public ResponseEntity<String> initTransaction(@RequestBody @Valid TxnRequestDTO txnRequestDTO) throws ExecutionException, InterruptedException {
        log.info("Starting transaction : {}", txnRequestDTO);
        String txnId = transactionService.initTransaction(txnRequestDTO);
        return ResponseEntity.accepted().body(txnId);
    }

    @GetMapping("status/{txnId}")
    public ResponseEntity<TxnStatusDTO> getTxnStatus(@PathVariable String txnId){
        return ResponseEntity.ok(transactionService.getStatus(txnId));
    }
}
