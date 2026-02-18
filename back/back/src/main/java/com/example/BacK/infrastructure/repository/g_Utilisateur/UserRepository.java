package com.example.BacK.infrastructure.repository.g_Utilisateur;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.BacK.domain.g_Utilisateurs.User;
import com.example.BacK.domain.g_Utilisateurs.UserStatus;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

       Optional<User> findByNomUtilisateur(String nomUtilisateur);

       Optional<User> findByEmail(String email);

       Boolean existsByNomUtilisateur(String nomUtilisateur);

       Boolean existsByEmail(String email);

       @Query("SELECT u FROM User u WHERE " +
                     "(:search IS NULL OR " +
                     "LOWER(u.nomUtilisateur) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                     "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                     "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                     "LOWER(u.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                     "LOWER(u.departement) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
                     "(:statut IS NULL OR u.statut = :statut)")
       Page<User> searchUsers(@Param("search") String search,
                     @Param("statut") UserStatus statut,
                     Pageable pageable);
}
