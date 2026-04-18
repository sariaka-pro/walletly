package com.smartbudget.smartbudget_backend.dto.auth; 

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private String token;

}