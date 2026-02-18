package com.example.BacK.application.g_Fournisseur.Query.dettesFournisseur.getSoldeRestant;

public class GetSoldeRestantDetteResponse {
    private String detteId;
    private String numeroFacture;
    private Float montantTotal;
    private Double soldeRestant;
    private Boolean estPaye;
    private Boolean estEnRetard;

    public GetSoldeRestantDetteResponse() {}

    public GetSoldeRestantDetteResponse(String detteId, String numeroFacture, Float montantTotal, 
                                       Double soldeRestant, Boolean estPaye, Boolean estEnRetard) {
        this.detteId = detteId;
        this.numeroFacture = numeroFacture;
        this.montantTotal = montantTotal;
        this.soldeRestant = soldeRestant;
        this.estPaye = estPaye;
        this.estEnRetard = estEnRetard;
    }

    public String getDetteId() {
        return detteId;
    }

    public void setDetteId(String detteId) {
        this.detteId = detteId;
    }

    public String getNumeroFacture() {
        return numeroFacture;
    }

    public void setNumeroFacture(String numeroFacture) {
        this.numeroFacture = numeroFacture;
    }

    public Float getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(Float montantTotal) {
        this.montantTotal = montantTotal;
    }

    public Double getSoldeRestant() {
        return soldeRestant;
    }

    public void setSoldeRestant(Double soldeRestant) {
        this.soldeRestant = soldeRestant;
    }

    public Boolean getEstPaye() {
        return estPaye;
    }

    public void setEstPaye(Boolean estPaye) {
        this.estPaye = estPaye;
    }

    public Boolean getEstEnRetard() {
        return estEnRetard;
    }

    public void setEstEnRetard(Boolean estEnRetard) {
        this.estEnRetard = estEnRetard;
    }
}