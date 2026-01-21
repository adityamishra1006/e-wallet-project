package com.example.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@ToString
public class UserCreatedPayload implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String userName;
    private String userEmail;
    private String requestId;
}
