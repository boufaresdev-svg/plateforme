package com.example.BacK.application.g_Stock.Query.tracabilite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTracabiliteStatsResponse {
    
    private int totalActions;
    private Map<String, Long> actionsParType;
    private Map<String, Long> actionsParUtilisateur;
    private int quantiteTotaleModifiee;
    private double valeurTotaleModifiee;
}
