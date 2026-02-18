package com.example.BacK.infrastructure.services.g_Stock;

import com.example.BacK.application.interfaces.g_Stock.stockLot.IStockLotRepositoryService;
import com.example.BacK.domain.g_Stock.Article;
import com.example.BacK.domain.g_Stock.Entrepot;
import com.example.BacK.domain.g_Stock.StockLot;
import com.example.BacK.infrastructure.g_Stock.Repositories.StockLotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class StockLotRepositoryService implements IStockLotRepositoryService {

    private final StockLotRepository stockLotRepository;

    public StockLotRepositoryService(StockLotRepository stockLotRepository) {
        this.stockLotRepository = stockLotRepository;
    }

    @Override
    public StockLot add(StockLot stockLot) {
        return stockLotRepository.save(stockLot);
    }

    @Override
    public StockLot update(StockLot stockLot) {
        return stockLotRepository.save(stockLot);
    }

    @Override
    public void delete(String id) {
        stockLotRepository.deleteById(id);
    }

    @Override
    public StockLot getById(String id) {
        return stockLotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("StockLot introuvable avec l'ID: " + id));
    }

    @Override
    public List<StockLot> getAll() {
        return stockLotRepository.findAll();
    }

    @Override
    public List<StockLot> getByArticleId(String articleId) {
        Article article = new Article();
        article.setId(articleId);
        return stockLotRepository.findByArticle(article);
    }

    @Override
    public List<StockLot> getByEntrepotId(String entrepotId) {
        Entrepot entrepot = new Entrepot();
        entrepot.setId(entrepotId);
        return stockLotRepository.findByEntrepot(entrepot);
    }

    @Override
    public List<StockLot> getByArticleIdAndEntrepotId(String articleId, String entrepotId) {
        return stockLotRepository.findByArticleIdAndEntrepotId(articleId, entrepotId);
    }

    @Override
    public List<StockLot> getAvailableLotsFIFO(String articleId, String entrepotId) {
        return stockLotRepository.findAvailableLotsFIFO(articleId, entrepotId);
    }

    @Override
    public List<StockLot> getAvailableLotsLIFO(String articleId, String entrepotId) {
        return stockLotRepository.findAvailableLotsLIFO(articleId, entrepotId);
    }

    @Override
    public List<StockLot> getExpiringBefore(LocalDate date) {
        return stockLotRepository.findExpiringBefore(date);
    }

    @Override
    public Integer getTotalQuantiteByArticleAndEntrepot(String articleId, String entrepotId) {
        return stockLotRepository.getTotalQuantiteByArticleAndEntrepot(articleId, entrepotId).orElse(0);
    }

    @Override
    public Double getTotalValueByArticleAndEntrepot(String articleId, String entrepotId) {
        return stockLotRepository.getTotalValueByArticleAndEntrepot(articleId, entrepotId).orElse(0.0);
    }

    @Override
    public List<StockLot> getAllWithAvailableQuantity() {
        return stockLotRepository.findAllWithAvailableQuantity();
    }
}
