package com.example.BacK.infrastructure.repository.g_Stock;

import com.example.BacK.domain.g_Stock.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    
    Optional<Category> findByNom(String nom);
    
    List<Category> findByEstActifTrue();
    
    List<Category> findByEstActifFalse();
    
    @Query("SELECT c FROM Category c WHERE " +
           "(:nom IS NULL OR LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
           "(:description IS NULL OR LOWER(c.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
           "(:estActif IS NULL OR c.estActif = :estActif)")
    List<Category> findBySearchCriteria(@Param("nom") String nom,
                                       @Param("description") String description,
                                       @Param("estActif") Boolean estActif);
    
    @Query("SELECT c FROM Category c WHERE " +
           "(:nom IS NULL OR LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
           "(:description IS NULL OR LOWER(c.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
           "(:estActif IS NULL OR c.estActif = :estActif)")
    Page<Category> findBySearchCriteriaPageable(@Param("nom") String nom,
                                              @Param("description") String description,
                                              @Param("estActif") Boolean estActif,
                                              Pageable pageable);
    
    @Query("SELECT COUNT(a) FROM Article a WHERE a.categorie.id = :categoryId")
    Long countArticlesByCategory(@Param("categoryId") String categoryId);
}