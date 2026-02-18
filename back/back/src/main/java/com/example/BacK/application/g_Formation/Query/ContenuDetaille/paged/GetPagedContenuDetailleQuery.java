package com.example.BacK.application.g_Formation.Query.ContenuDetaille.paged;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPagedContenuDetailleQuery {
    private int page;
    private int size;
    private String sortBy;
    private String sortDirection;
    private Long idJourFormation; // Optional filter
    
    public GetPagedContenuDetailleQuery(int page, int size) {
        this.page = page;
        this.size = size;
        this.sortBy = "idContenuDetaille";
        this.sortDirection = "ASC";
        this.idJourFormation = null;
    }
}
