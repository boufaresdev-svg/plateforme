package com.example.BacK.application.g_Projet.Command.commentaireTache.update;

import com.example.BacK.domain.g_Projet.enumEntity.TypeCommentaire;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentaireTacheCommand {
    private String id;
    private String auteur;
    private String contenu;
    private LocalDate date;
    private TypeCommentaire type;
    private String tache;
}
