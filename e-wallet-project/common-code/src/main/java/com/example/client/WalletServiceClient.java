package com.example.client;

import com.example.dto.WalletBalanceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "wallet-service", url = "http://localhost:8081")
public interface WalletServiceClient {

    @GetMapping("/wallet-service/balance/{userId}")
    public ResponseEntity<WalletBalanceDTO> getBalance(@PathVariable Long userId);
}
