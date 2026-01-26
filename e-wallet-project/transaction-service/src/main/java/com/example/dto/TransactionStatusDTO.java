package com.example.dto;

import com.example.entity.TxnStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionStatusDTO {
    private TxnStatus status;
    private String reason;
}

