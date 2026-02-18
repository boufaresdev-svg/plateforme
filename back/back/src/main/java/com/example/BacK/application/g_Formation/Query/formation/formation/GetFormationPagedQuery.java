package com.example.BacK.application.g_Formation.Query.formation.formation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetFormationPagedQuery {
    private int page;
    private int size;
    private String sortBy;
    private String sortDirection;
    
    public GetFormationPagedQuery(int page, int size) {
        this.page = page;
        this.size = size;
        this.sortBy = "idFormation";
        this.sortDirection = "ASC";
    }
}
