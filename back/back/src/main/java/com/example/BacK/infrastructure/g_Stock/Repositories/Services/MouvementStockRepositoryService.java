package com.example.BacK.infrastructure.g_Stock.Repositories.Services;

import com.example.BacK.application.interfaces.g_Stock.mouvementStock.IMouvementStockRepositoryService;
import com.example.BacK.domain.g_Stock.Article;
import com.example.BacK.domain.g_Stock.Entrepot;
import com.example.BacK.domain.g_Stock.MouvementStock;
import com.example.BacK.domain.g_Stock.enumEntity.Statut;
import com.example.BacK.domain.g_Stock.enumEntity.TypeMouvement;
import com.example.BacK.domain.g_Utilisateurs.User;
import com.example.BacK.infrastructure.g_Stock.Repositories.MouvementStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MouvementStockRepositoryService implements IMouvementStockRepositoryService {
    
    private final MouvementStockRepository mouvementStockRepository;
    
    @Override
    public MouvementStock add(MouvementStock mouvementStock) {
        return mouvementStockRepository.save(mouvementStock);
    }
    
    @Override
    public MouvementStock update(MouvementStock mouvementStock) {
        return mouvementStockRepository.save(mouvementStock);
    }
    
    @Override
    public void delete(String id) {
        mouvementStockRepository.deleteById(id);
    }
    
    @Override
    public MouvementStock getById(String id) {
        return mouvementStockRepository.findById(id).orElse(null);
    }
    
    @Override
    public List<MouvementStock> getAll() {
        return mouvementStockRepository.findAll();
    }
    
    @Override
    public List<MouvementStock> getByArticleId(String articleId) {
        Article article = new Article();
        article.setId(articleId);
        return mouvementStockRepository.findByArticle(article);
    }
    
    @Override
    public List<MouvementStock> getByDestinationEntrepotId(String entrepotId) {
        Entrepot entrepot = new Entrepot();
        entrepot.setId(entrepotId);
        return mouvementStockRepository.findByDestinationEntrepot(entrepot);
    }
    
    @Override
    public List<MouvementStock> getBySourceEntrepotId(String entrepotId) {
        Entrepot entrepot = new Entrepot();
        entrepot.setId(entrepotId);
        return mouvementStockRepository.findBySourceEntrepot(entrepot);
    }
    
    @Override
    public List<MouvementStock> getBySourceOrDestinationEntrepot(String entrepotId) {
        Entrepot entrepot = new Entrepot();
        entrepot.setId(entrepotId);
        List<MouvementStock> sourceMovements = mouvementStockRepository.findBySourceEntrepot(entrepot);
        List<MouvementStock> destinationMovements = mouvementStockRepository.findByDestinationEntrepot(entrepot);
        
        // Merge both lists and remove duplicates
        sourceMovements.addAll(destinationMovements);
        return sourceMovements.stream().distinct().toList();
    }
    
    @Override
    public List<MouvementStock> getByUtilisateurId(String utilisateurId) {
        User user = new User();
        user.setId(utilisateurId);
        return mouvementStockRepository.findByUtilisateur(user);
    }
    
    @Override
    public List<MouvementStock> getByTypeMouvement(TypeMouvement typeMouvement) {
        return mouvementStockRepository.findByTypeMouvement(typeMouvement);
    }
    
    @Override
    public List<MouvementStock> getByStatut(Statut statut) {
        return mouvementStockRepository.findByStatut(statut);
    }
    
    @Override
    public List<MouvementStock> getByDateMouvementBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return mouvementStockRepository.findByDateMouvementBetween(startDate, endDate);
    }
    
    @Override
    public List<MouvementStock> getByReference(String reference) {
        return mouvementStockRepository.findByReference(reference);
    }
}
