package com.example.BacK.application.g_Stock.Query.marque;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMarqueQuery {
    
    private String id;
    private String nom;
    private String codeMarque;
    private String pays;
    private Boolean estActif;
    
    // Pagination
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
