package com.example.BacK.application.g_Fournisseur.Query.tranchePaiement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTranchePaiementQuery {
    private String id;
    private String dettesFournisseurId;
    private String fournisseurId;
    private Boolean estPaye;
    private Boolean enRetard;
    
    // Constructor for backward compatibility
    public GetTranchePaiementQuery(String dettesFournisseurId, String fournisseurId, Boolean estPaye, Boolean enRetard) {
        this.dettesFournisseurId = dettesFournisseurId;
        this.fournisseurId = fournisseurId;
        this.estPaye = estPaye;
        this.enRetard = enRetard;
    }
}