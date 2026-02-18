package com.example.BacK.domain.g_Formation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"apprenant", "examen"})
public class Certificat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCertificat;
    private String titre;
    private String description;
    private String niveau;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprenant_id")
    private Apprenant apprenant;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examen_id")
    private Examen examen;
}
