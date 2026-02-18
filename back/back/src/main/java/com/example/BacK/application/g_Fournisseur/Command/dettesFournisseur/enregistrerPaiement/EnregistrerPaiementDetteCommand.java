package com.example.BacK.application.g_Fournisseur.Command.dettesFournisseur.enregistrerPaiement;

public class EnregistrerPaiementDetteCommand {
    private String detteId;
    private Double montant;

    public EnregistrerPaiementDetteCommand() {}

    public EnregistrerPaiementDetteCommand(String detteId, Double montant) {
        this.detteId = detteId;
        this.montant = montant;
    }

    public String getDetteId() {
        return detteId;
    }

    public void setDetteId(String detteId) {
        this.detteId = detteId;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }
}