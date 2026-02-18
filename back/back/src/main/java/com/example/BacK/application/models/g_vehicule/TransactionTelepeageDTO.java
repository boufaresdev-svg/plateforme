package com.example.BacK.application.models.g_vehicule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionTelepeageDTO {

    private String id;
    private LocalDate date;
    private double montant;
    private String conducteur;
    private String description;
    private CarteTelepeageDTO carte;
}
