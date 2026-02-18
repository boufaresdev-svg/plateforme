package com.example.BacK.application.g_Utilisateur.Command.auth.login;

import com.example.BacK.application.models.g_Utilisateur.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private UserDTO user;
}
