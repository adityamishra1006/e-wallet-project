package com.example.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserProfileDTO {
    private UserDTO userDetails;
    private Double walletBalance;
}
