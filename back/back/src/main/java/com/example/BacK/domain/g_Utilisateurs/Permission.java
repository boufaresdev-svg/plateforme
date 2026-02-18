package com.example.BacK.domain.g_Utilisateurs;

import com.example.BacK.domain.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "ressource", "action" })
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = "roles")
public class Permission extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Column(name = "ressource", nullable = false, length = 50)
    private String ressource;
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private PermissionAction action;
    @Column(name = "description", length = 200)
    private String description;
    @Column(name = "nom_affichage", length = 100)
    private String nomAffichage;
    @Enumerated(EnumType.STRING)
    @Column(name = "module", length = 50)
    private Module module;
    @JsonIgnore
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();

    public String getPermissionString() {
        return ressource + "_" + action.name();
    }
}
