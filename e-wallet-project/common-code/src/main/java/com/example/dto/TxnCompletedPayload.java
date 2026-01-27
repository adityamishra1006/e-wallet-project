package com.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TxnCompletedPayload {
    private Long id; // Id of the transaction
    private Boolean success;
    private String reason;
    private String requestId;
}
