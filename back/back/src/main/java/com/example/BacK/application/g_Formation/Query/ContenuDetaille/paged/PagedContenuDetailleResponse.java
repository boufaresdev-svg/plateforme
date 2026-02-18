package com.example.BacK.application.g_Formation.Query.ContenuDetaille.paged;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedContenuDetailleResponse {
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContenuSummary {
        private Long idContenuDetaille;
        private String titre;
        private String methodePedagogique;
        private Double dureeTheorique;
        private Double dureePratique;
        private Integer levelCount;
    }
    
    private List<ContenuSummary> content;
    private int page;
    private int size;
    private long total;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
}
