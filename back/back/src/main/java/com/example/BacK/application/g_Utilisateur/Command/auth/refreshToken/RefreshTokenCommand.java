package com.example.BacK.application.g_Utilisateur.Command.auth.refreshToken;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenCommand {
    
    @NotBlank(message = "Le token de rafraîchissement est obligatoire")
    private String refreshToken;
}
