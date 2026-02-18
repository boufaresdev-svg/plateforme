package com.example.BacK.domain.g_Formation.enumEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TypeFormation {
    
    @JsonProperty("Inter_Entreprise")
    Inter_Entreprise,
    
    @JsonProperty("Intra_Entreprise")
    Intra_Entreprise,
    
    @JsonProperty("En_Ligne")
    En_Ligne

}

