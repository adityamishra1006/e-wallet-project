package com.example.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Setter
@Getter
@ToString
public class UserProfileDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private UserDTO userDetails;
    private Double walletBalance;
}
