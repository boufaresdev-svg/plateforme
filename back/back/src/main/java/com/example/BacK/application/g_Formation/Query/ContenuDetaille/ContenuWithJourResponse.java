package com.example.BacK.application.g_Formation.Query.ContenuDetaille;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContenuWithJourResponse {
    private Long idContenuDetaille;
    private String titre;
    private String methodesPedagogiques;
    private String tags;
    private Double dureeTheorique;
    private Double dureePratique;
    private List<String> contenusCles;
    private Integer numeroJour;  // From ContenuJourFormation
    private Long idContenuJour;  // Reference to ContenuJourFormation
    private List<FileInfo> files = new ArrayList<>();
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileInfo {
        private String fileName;
        private String filePath;
        private String fileType;
        private Long fileSize;
        private Integer levelNumber;
    }
}
