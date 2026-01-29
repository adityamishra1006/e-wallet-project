package com.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddMoneyRequest {
    private Double amount;
    private Long userId;

    private Long merchantId;
}
