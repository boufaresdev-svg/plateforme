package com.example.BacK.domain.g_Utilisateurs;

import com.example.BacK.domain.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles", uniqueConstraints = @UniqueConstraint(columnNames = "nom"))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = {"permissions", "utilisateurs"})
@JsonIgnoreProperties({"utilisateurs"})
public class Role extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
    @Column(name = "nom", unique = true, nullable = false, length = 50)
    private String nom;
    @Column(name = "description", length = 255)
    private String description;
    @Column(name = "system_role")
    private Boolean systemRole = false;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @JsonIgnoreProperties({"roles", "createdDate", "lastModifiedDate", "createdBy", "lastModifiedBy"})
    private Set<Permission> permissions = new HashSet<>();
    @ManyToMany(mappedBy = "roles")
    @JsonIgnoreProperties({"roles", "permissions", "utilisateurs"})
    private Set<User> utilisateurs = new HashSet<>();
}
