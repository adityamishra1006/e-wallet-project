package com.example.controller;

import com.example.dto.TxnRequestDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction-service")
public class TransactionController {

    private static Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

    @PostMapping("/txn")
    public ResponseEntity<String> initTransaction(@RequestBody @Valid TxnRequestDTO txnRequestDTO){

    }
}
