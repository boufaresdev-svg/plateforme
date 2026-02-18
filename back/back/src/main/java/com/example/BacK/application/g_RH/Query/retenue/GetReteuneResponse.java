package com.example.BacK.application.g_RH.Query.retenue;

import com.example.BacK.application.models.g_rh.EmployeeDTO;
import com.example.BacK.application.models.g_rh.RegleDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetReteuneResponse {
    private String id;
    private String libelle;
    private double montant;
    private int nombrePoints;
    private String description;
    private EmployeeDTO employee;
    private List<RegleDTO> regle ;
}
