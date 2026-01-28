package com.example.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TxnCompletedPayload {
    private Long id; // I'd of the transaction
    private Boolean success;
    private String reason;
    private String requestId;
}
