package com.example.BacK.domain.g_Projet;

import com.example.BacK.domain.g_Projet.enumEntity.TypeCommentaire;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CommentaireTache {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    @EqualsAndHashCode.Include
    private String id;

    private String auteur;
    private String contenu;
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private TypeCommentaire type;

    @ManyToOne
    @JoinColumn(name = "tache_id")
    private Tache tache;
}
