package com.example.BacK.application.g_Utilisateur.Command.auth.login;

import com.example.BacK.application.interfaces.g_Utilisateur.auth.IAuthService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("LoginHandler")
public class LoginHandler implements RequestHandler<LoginCommand, LoginResponse> {

    private final IAuthService authService;

    public LoginHandler(IAuthService authService) {
        this.authService = authService;
    }

    @Override
    public LoginResponse handle(LoginCommand command) {
        return authService.login(command.getUsername(), command.getPassword());
    }
}
