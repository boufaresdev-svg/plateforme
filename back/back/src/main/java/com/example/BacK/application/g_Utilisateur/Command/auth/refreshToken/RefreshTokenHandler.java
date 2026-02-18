package com.example.BacK.application.g_Utilisateur.Command.auth.refreshToken;

import com.example.BacK.application.interfaces.g_Utilisateur.auth.IAuthService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("RefreshTokenHandler")
public class RefreshTokenHandler implements RequestHandler<RefreshTokenCommand, RefreshTokenResponse> {

    private final IAuthService authService;

    public RefreshTokenHandler(IAuthService authService) {
        this.authService = authService;
    }

    @Override
    public RefreshTokenResponse handle(RefreshTokenCommand command) {
        return authService.refreshToken(command.getRefreshToken());
    }
}
