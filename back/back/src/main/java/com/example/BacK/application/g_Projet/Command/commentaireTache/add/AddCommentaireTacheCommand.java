package com.example.BacK.application.g_Projet.Command.commentaireTache.add;

import com.example.BacK.domain.g_Projet.enumEntity.TypeCommentaire;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCommentaireTacheCommand {
    private String auteur;
    private String contenu;
    private TypeCommentaire type;
    private String tacheId;
}
