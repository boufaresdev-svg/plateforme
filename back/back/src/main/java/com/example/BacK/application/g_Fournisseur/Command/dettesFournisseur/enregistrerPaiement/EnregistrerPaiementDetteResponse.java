package com.example.BacK.application.g_Fournisseur.Command.dettesFournisseur.enregistrerPaiement;

public class EnregistrerPaiementDetteResponse {
    private String detteId;
    private Boolean estPaye;
    private Double soldeRestant;
    private String message;

    public EnregistrerPaiementDetteResponse() {}

    public EnregistrerPaiementDetteResponse(String detteId, Boolean estPaye, Double soldeRestant, String message) {
        this.detteId = detteId;
        this.estPaye = estPaye;
        this.soldeRestant = soldeRestant;
        this.message = message;
    }

    public String getDetteId() {
        return detteId;
    }

    public void setDetteId(String detteId) {
        this.detteId = detteId;
    }

    public Boolean getEstPaye() {
        return estPaye;
    }

    public void setEstPaye(Boolean estPaye) {
        this.estPaye = estPaye;
    }

    public Double getSoldeRestant() {
        return soldeRestant;
    }

    public void setSoldeRestant(Double soldeRestant) {
        this.soldeRestant = soldeRestant;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}