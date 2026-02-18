package com.example.BacK.domain.g_Formation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"programmeDetaile", "contenusDetailles"})
public class JourFormation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idJour;

    private Integer numeroJour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_detaile_id")
    private ProgrammeDetaile programmeDetaile;

    @OneToMany(mappedBy = "jourFormation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ContenuDetaille> contenusDetailles = new ArrayList<>();
}
