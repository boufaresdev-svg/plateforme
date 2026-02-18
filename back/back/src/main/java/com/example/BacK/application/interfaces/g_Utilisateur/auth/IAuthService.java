package com.example.BacK.application.interfaces.g_Utilisateur.auth;

import com.example.BacK.application.g_Utilisateur.Command.auth.login.LoginResponse;
import com.example.BacK.application.g_Utilisateur.Command.auth.refreshToken.RefreshTokenResponse;

public interface IAuthService {
    LoginResponse login(String username, String password);
    RefreshTokenResponse refreshToken(String refreshToken);
}
