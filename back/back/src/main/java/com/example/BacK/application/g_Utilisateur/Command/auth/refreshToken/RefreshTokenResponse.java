package com.example.BacK.application.g_Utilisateur.Command.auth.refreshToken;

import com.example.BacK.application.models.g_Utilisateur.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
    private UserDTO user;
}
