package com.example.BacK.application.models.g_projet;

import com.example.BacK.domain.g_Projet.enumEntity.TypeCommentaire;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentaireTacheDTO {

    private String id;
    private String auteur;
    private String contenu;
    private LocalDate date;
    private TypeCommentaire type;
    private TacheDTO tache;
}
