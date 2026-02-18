package com.example.BacK.application.g_Fournisseur.Query.dettesFournisseur.getSoldeRestant;

public class GetSoldeRestantDetteQuery {
    private String detteId;

    public GetSoldeRestantDetteQuery() {}

    public GetSoldeRestantDetteQuery(String detteId) {
        this.detteId = detteId;
    }

    public String getDetteId() {
        return detteId;
    }

    public void setDetteId(String detteId) {
        this.detteId = detteId;
    }
}