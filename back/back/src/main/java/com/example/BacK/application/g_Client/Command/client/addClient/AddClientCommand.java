package com.example.BacK.application.g_Client.Command.client.addClient;

import com.example.BacK.application.models.g_Client.*;
import com.example.BacK.domain.g_Client.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddClientCommand {

    private String nom;
    private String prenom;
    private String raisonSociale;
    private String type;
    private String secteur;
    private String statut;
    private String adresse;
    private String ville;
    private String codePostal;
    private String pays;
    private String telephone;
    private String email;
    private String siteWeb;
    private String localisation;
    private String identifiantFiscal;
    private String rib;
    private int pointsFidelite;
    private List<ContactClientDTO> contacts;
    private List<ProjetClientDTO> projets;
    private List<FactureClientDTO> factures;
    private List<PaiementClientDTO> paiements;
    private List<InteractionClientDTO> interactions;
    private List<String> documents;
    private List<String> tags;
    private double chiffreAffaires;


}
