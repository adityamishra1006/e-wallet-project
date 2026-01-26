package com.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TransactionDTO {
    @NotNull
    private Long fromUserId;

    @NotNull
    private Long toUserId;

    @NotNull
    private Double amount;
    private String comment;
}
