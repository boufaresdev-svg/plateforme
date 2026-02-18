package com.example.BacK.domain.g_Formation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectifGlobal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idObjectifGlobal;

    @Column(nullable = false, length = 500)
    private String libelle;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String tags;

    @JsonIgnore
    @ManyToMany(mappedBy = "objectifsGlobaux", fetch = FetchType.LAZY)
    private List<Formation> formations = new ArrayList<>();

    // Ignore to avoid recursive serialization issues with multiple back-references
    @JsonIgnore
    @OneToMany(mappedBy = "objectifGlobal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ObjectifSpecifique> objectifsSpecifiques = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectifGlobal)) return false;
        ObjectifGlobal that = (ObjectifGlobal) o;
        return idObjectifGlobal != null && idObjectifGlobal.equals(that.idObjectifGlobal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idObjectifGlobal);
    }
}
