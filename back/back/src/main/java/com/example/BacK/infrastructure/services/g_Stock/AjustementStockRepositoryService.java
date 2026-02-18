package com.example.BacK.infrastructure.services.g_Stock;

import com.example.BacK.application.g_Stock.Query.ajustementStock.GetAjustementStockResponse;
import com.example.BacK.application.interfaces.g_Stock.ajustementStock.IAjustementStockRepositoryService;
import com.example.BacK.domain.g_Stock.AjustementStock;
import com.example.BacK.infrastructure.repository.g_Stock.AjustementStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AjustementStockRepositoryService implements IAjustementStockRepositoryService {
    
    private final AjustementStockRepository ajustementStockRepository;
    
    @Override
    @Transactional
    public String add(AjustementStock ajustementStock) {
        AjustementStock savedAjustement = ajustementStockRepository.save(ajustementStock);
        return savedAjustement.getId();
    }
    
    @Override
    @Transactional
    public void update(AjustementStock ajustementStock) {
        ajustementStockRepository.save(ajustementStock);
    }
    
    @Override
    @Transactional
    public void delete(String id) {
        ajustementStockRepository.deleteById(id);
    }
    
    @Override
    public AjustementStock getById(String id) {
        return ajustementStockRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ajustement de stock non trouvé avec l'ID: " + id));
    }
    
    @Override
    public List<GetAjustementStockResponse> getAll() {
        return ajustementStockRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<GetAjustementStockResponse> getByArticleId(String articleId) {
        return ajustementStockRepository.findByArticleId(articleId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<GetAjustementStockResponse> getByUtilisateurId(String utilisateurId) {
        return ajustementStockRepository.findByUtilisateurId(utilisateurId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<GetAjustementStockResponse> getByDateRange(LocalDate startDate, LocalDate endDate) {
        return ajustementStockRepository.findByDateAjustementBetween(startDate, endDate).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    private GetAjustementStockResponse mapToResponse(AjustementStock ajustement) {
        return new GetAjustementStockResponse(
            ajustement.getId(),
            ajustement.getArticle() != null ? ajustement.getArticle().getId() : null,
            ajustement.getArticle() != null ? ajustement.getArticle().getNom() : null,
            ajustement.getArticle() != null && ajustement.getArticle().getCategorie() != null ? 
                ajustement.getArticle().getCategorie().getId() : null,
            ajustement.getArticle() != null && ajustement.getArticle().getCategorie() != null ? 
                ajustement.getArticle().getCategorie().getNom() : null,
            ajustement.getArticle() != null && ajustement.getArticle().getMarque() != null ? 
                ajustement.getArticle().getMarque().getId() : null,
            ajustement.getArticle() != null && ajustement.getArticle().getMarque() != null ? 
                ajustement.getArticle().getMarque().getNom() : null,
            ajustement.getEntrepot() != null ? ajustement.getEntrepot().getId() : null,
            ajustement.getEntrepot() != null ? ajustement.getEntrepot().getNom() : null,
            ajustement.getQuantiteAvant(),
            ajustement.getQuantiteApres(),
            ajustement.getAjustement(),
            ajustement.getDateAjustement(),
            ajustement.getUtilisateur() != null ? ajustement.getUtilisateur().getId() : null,
            ajustement.getUtilisateur() != null ? 
                ajustement.getUtilisateur().getPrenom() + " " + ajustement.getUtilisateur().getNom() : null,
            ajustement.getRaison(),
            ajustement.getNotes()
        );
    }
}
