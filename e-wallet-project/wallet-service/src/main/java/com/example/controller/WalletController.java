package com.example.controller;

import com.example.dto.AddMoneyRequest;
import com.example.dto.AddMoneyResponse;
import com.example.dto.WalletBalanceDTO;
import com.example.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/wallet-service")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final RestTemplate restTemplate;

    @GetMapping("/balance/{userId}")
    public ResponseEntity<WalletBalanceDTO> getBalance(@PathVariable Long userId){
        WalletBalanceDTO walletBalanceDTO = walletService.walletBalance(userId);
        return ResponseEntity.ok(walletBalanceDTO);
    }

    @PostMapping("/add-money")
    public ResponseEntity<AddMoneyResponse> addMoney(@RequestBody AddMoneyRequest addMoneyRequest){
        addMoneyRequest.setMerchantId(1l);
        AddMoneyResponse addMoneyResponse = restTemplate.postForObject("http://localhost:9090/pg-service/init-payment", addMoneyRequest, AddMoneyResponse.class);
        return ResponseEntity.ok(addMoneyResponse);
    }

    @GetMapping("/add-money-status/{pgTxnId}")
    public ResponseEntity<String> addMoneyStatus(@PathVariable String pgTxnId){
        return ResponseEntity.ok(walletService.processPgTxnId(pgTxnId));
    }

}
