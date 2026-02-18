package com.example.BacK.presentation.g_Utilisateur;

import com.example.BacK.application.g_Utilisateur.Command.auth.login.LoginCommand;
import com.example.BacK.application.g_Utilisateur.Command.auth.login.LoginResponse;
import com.example.BacK.application.g_Utilisateur.Command.auth.refreshToken.RefreshTokenCommand;
import com.example.BacK.application.g_Utilisateur.Command.auth.refreshToken.RefreshTokenResponse;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Authentication", description = "API d'authentification")
public class AuthController {

    private final Mediator mediator;

    public AuthController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Connexion", description = "Authentifie un utilisateur et génère des tokens JWT")
    @PostMapping("/login")
    public ResponseEntity<List<LoginResponse>> login(@Valid @RequestBody LoginCommand command) {
        return ResponseEntity.ok(mediator.sendToHandlers(command));
    }

    @Operation(summary = "Rafraîchir le token", description = "Génère de nouveaux tokens à partir d'un refresh token valide")
    @PostMapping("/refresh")
    public ResponseEntity<List<RefreshTokenResponse>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        // Extract token from Bearer header
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        RefreshTokenCommand command = new RefreshTokenCommand(token);
        return ResponseEntity.ok(mediator.sendToHandlers(command));
    }
}
