package com.example.BacK.application.Features.g_Stock.Commands.Alerte;

import com.example.BacK.domain.g_Stock.enumEntity.AlerteStatut;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAlerteStatusCommand {
    private String alerteId;
    private AlerteStatut statut;
}
