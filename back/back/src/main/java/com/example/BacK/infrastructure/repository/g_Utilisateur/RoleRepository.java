package com.example.BacK.infrastructure.repository.g_Utilisateur;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.BacK.domain.g_Utilisateurs.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByNom(String nom);
    Boolean existsByNom(String nom);
}
