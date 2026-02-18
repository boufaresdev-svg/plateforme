package com.example.BacK.application.g_Formation.Query.ContenuDetaille.getAllByJour;

import com.example.BacK.application.g_Formation.Query.ContenuDetaille.ContentLevelResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllContenuDetailleByJourResponse {

    private Long idContenuDetaille;
    private String titre;
    private List<String> contenusCles;
    private String methodesPedagogiques;
    private Double dureeTheorique;
    private Double dureePratique;
    private String tags;
    private List<ContentLevelResponseDto> levels;
}
