package com.example.BacK.infrastructure.g_Stock.Repositories;

import com.example.BacK.domain.g_Stock.Article;
import com.example.BacK.domain.g_Stock.Entrepot;
import com.example.BacK.domain.g_Stock.MouvementStock;
import com.example.BacK.domain.g_Stock.enumEntity.Statut;
import com.example.BacK.domain.g_Stock.enumEntity.TypeMouvement;
import com.example.BacK.domain.g_Utilisateurs.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, String> {
    
    List<MouvementStock> findByArticle(Article article);
    
    List<MouvementStock> findByDestinationEntrepot(Entrepot entrepot);
    
    List<MouvementStock> findBySourceEntrepot(Entrepot entrepot);
    
    List<MouvementStock> findByUtilisateur(User utilisateur);
    
    List<MouvementStock> findByTypeMouvement(TypeMouvement typeMouvement);
    
    List<MouvementStock> findByStatut(Statut statut);
    
    List<MouvementStock> findByDateMouvementBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<MouvementStock> findByReference(String reference);
    
    List<MouvementStock> findByNumeroBonReception(String numeroBonReception);
    
    List<MouvementStock> findByReferenceBonCommande(String referenceBonCommande);
    
    List<MouvementStock> findByNumeroBonLivraison(String numeroBonLivraison);
    
    List<MouvementStock> findByReferenceCommandeClient(String referenceCommandeClient);
    
    @Query("SELECT m FROM MouvementStock m WHERE " +
           "(:articleId IS NULL OR m.article.id = :articleId) AND " +
           "(:entrepotId IS NULL OR m.destinationEntrepot.id = :entrepotId OR m.sourceEntrepot.id = :entrepotId) AND " +
           "(:typeMouvement IS NULL OR m.typeMouvement = :typeMouvement) AND " +
           "(:statut IS NULL OR m.statut = :statut) AND " +
           "(:startDate IS NULL OR m.dateMouvement >= :startDate) AND " +
           "(:endDate IS NULL OR m.dateMouvement <= :endDate)")
    List<MouvementStock> search(@Param("articleId") String articleId,
                                @Param("entrepotId") String entrepotId,
                                @Param("typeMouvement") TypeMouvement typeMouvement,
                                @Param("statut") Statut statut,
                                @Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);
}
