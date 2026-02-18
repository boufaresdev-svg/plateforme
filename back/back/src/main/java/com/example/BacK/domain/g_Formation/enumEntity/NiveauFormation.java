package com.example.BacK.domain.g_Formation.enumEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum NiveauFormation {

    @JsonProperty("Débutant")
    Debutant,
    
    @JsonProperty("Intermédiaire")
    Intermediaire,
    
    @JsonProperty("Avancé")
    Avance

}
