package com.example.BacK.infrastructure.repository.g_Stock;

import com.example.BacK.domain.g_Stock.Marque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarqueRepository extends JpaRepository<Marque, String> {
    
    Optional<Marque> findByNom(String nom);
    
    Optional<Marque> findByCodeMarque(String codeMarque);
    
    List<Marque> findByEstActifTrue();
    
    List<Marque> findByEstActifFalse();
    
    @Query("SELECT m FROM Marque m WHERE " +
           "(:nom IS NULL OR LOWER(m.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
           "(:codeMarque IS NULL OR LOWER(m.codeMarque) LIKE LOWER(CONCAT('%', :codeMarque, '%'))) AND " +
           "(:pays IS NULL OR LOWER(m.pays) LIKE LOWER(CONCAT('%', :pays, '%'))) AND " +
           "(:estActif IS NULL OR m.estActif = :estActif)")
    List<Marque> search(@Param("nom") String nom,
                        @Param("codeMarque") String codeMarque,
                        @Param("pays") String pays,
                        @Param("estActif") Boolean estActif);
    
    @Query("SELECT COUNT(a) FROM Article a WHERE a.marque.id = :marqueId")
    Long countArticlesByMarque(@Param("marqueId") String marqueId);
}
