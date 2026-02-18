package com.example.BacK.domain.g_Utilisateurs;

import com.example.BacK.domain.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "nom_utilisateur"),
    @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User extends Auditable implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
    
    @Column(name = "nom_utilisateur", unique = true, nullable = false, length = 50)
    private String nomUtilisateur;
    
    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(name = "prenom", nullable = false, length = 50)
    private String prenom;
    
    @Column(name = "nom", nullable = false, length = 50)
    private String nom;
    
    @Column(name = "numero_telephone", length = 20)
    private String numeroTelephone;
    
    @Column(name = "departement", length = 100)
    private String departement;
    
    @Column(name = "poste", length = 100)
    private String poste;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private UserStatus statut = UserStatus.EN_ATTENTE;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    @Column(name = "derniere_connexion")
    private LocalDateTime derniereConnexion;

    @Column(name = "tentatives_connexion_echouees")
    private Integer tentativesConnexionEchouees = 0;

    @Column(name = "compte_verrouille_jusqu_a")
    private LocalDateTime compteVerrouilleJusquA;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getNom()));
            for (Permission permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(
                    permission.getRessource() + "_" + permission.getAction().name()
                ));
            }
        }
        
        return authorities;
    }

    @Override
    public String getPassword() {
        return motDePasse;
    }

    @Override
    public String getUsername() {
        return nomUtilisateur;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (compteVerrouilleJusquA != null && LocalDateTime.now().isBefore(compteVerrouilleJusquA)) {
            return false;
        }
        return statut != UserStatus.SUSPENDU;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; 
    }

    @Override
    public boolean isEnabled() {
        return statut == UserStatus.ACTIF;
    }
}
