package com.example.BacK.infrastructure.g_Stock.Repositories;

import com.example.BacK.domain.g_Stock.AlerteStock;
import com.example.BacK.domain.g_Stock.enumEntity.AlertePriorite;
import com.example.BacK.domain.g_Stock.enumEntity.AlerteStatut;
import com.example.BacK.domain.g_Stock.enumEntity.AlerteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlerteStockRepository extends JpaRepository<AlerteStock, String> {
    
    List<AlerteStock> findByStatut(AlerteStatut statut);
    
    List<AlerteStock> findByType(AlerteType type);
    
    List<AlerteStock> findByPriorite(AlertePriorite priorite);
    
    List<AlerteStock> findByIsReadFalse();
    
    List<AlerteStock> findByIsArchivedFalse();
    
    List<AlerteStock> findByArticleId(String articleId);
    
    List<AlerteStock> findByEntrepotId(String entrepotId);
    
    @Query("SELECT a FROM AlerteStock a WHERE a.statut = :statut AND a.isArchived = false ORDER BY a.priorite DESC, a.dateCreation DESC")
    List<AlerteStock> findActiveAlerts(@Param("statut") AlerteStatut statut);
    
    @Query("SELECT a FROM AlerteStock a WHERE " +
           "(:type IS NULL OR a.type = :type) AND " +
           "(:priorite IS NULL OR a.priorite = :priorite) AND " +
           "(:statut IS NULL OR a.statut = :statut) AND " +
           "(:isRead IS NULL OR a.isRead = :isRead) AND " +
           "a.isArchived = false " +
           "ORDER BY a.priorite DESC, a.dateCreation DESC")
    List<AlerteStock> searchAlerts(@Param("type") AlerteType type,
                                    @Param("priorite") AlertePriorite priorite,
                                    @Param("statut") AlerteStatut statut,
                                    @Param("isRead") Boolean isRead);
    
    @Query("SELECT COUNT(a) FROM AlerteStock a WHERE a.statut = :statut AND a.isArchived = false")
    Long countByStatut(@Param("statut") AlerteStatut statut);
    
    @Query("SELECT COUNT(a) FROM AlerteStock a WHERE a.isRead = false AND a.isArchived = false")
    Long countUnread();
}
