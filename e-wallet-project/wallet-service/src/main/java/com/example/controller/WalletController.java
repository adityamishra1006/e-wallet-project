package com.example.controller;

import com.example.dto.WalletBalanceDTO;
import com.example.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet-service")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance/{userId}")
    public ResponseEntity<WalletBalanceDTO> getBalance(@PathVariable Long userId){
        WalletBalanceDTO walletBalanceDTO = walletService.walletBalance(userId);
        return ResponseEntity.ok(walletBalanceDTO);
    }

}
