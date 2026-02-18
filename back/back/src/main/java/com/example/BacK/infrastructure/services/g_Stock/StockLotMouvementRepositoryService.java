package com.example.BacK.infrastructure.services.g_Stock;

import com.example.BacK.application.interfaces.g_Stock.stockLotMouvement.IStockLotMouvementRepositoryService;
import com.example.BacK.domain.g_Stock.StockLot;
import com.example.BacK.domain.g_Stock.StockLotMouvement;
import com.example.BacK.domain.g_Stock.enumEntity.TypeMouvement;
import com.example.BacK.infrastructure.g_Stock.Repositories.StockLotMouvementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class StockLotMouvementRepositoryService implements IStockLotMouvementRepositoryService {

    private final StockLotMouvementRepository stockLotMouvementRepository;

    public StockLotMouvementRepositoryService(StockLotMouvementRepository stockLotMouvementRepository) {
        this.stockLotMouvementRepository = stockLotMouvementRepository;
    }

    @Override
    public StockLotMouvement add(StockLotMouvement stockLotMouvement) {
        return stockLotMouvementRepository.save(stockLotMouvement);
    }

    @Override
    public StockLotMouvement update(StockLotMouvement stockLotMouvement) {
        return stockLotMouvementRepository.save(stockLotMouvement);
    }

    @Override
    public void delete(String id) {
        stockLotMouvementRepository.deleteById(id);
    }

    @Override
    public StockLotMouvement getById(String id) {
        return stockLotMouvementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("StockLotMouvement introuvable avec l'ID: " + id));
    }

    @Override
    public List<StockLotMouvement> getAll() {
        return stockLotMouvementRepository.findAll();
    }

    @Override
    public List<StockLotMouvement> getByStockLotId(String stockLotId) {
        StockLot stockLot = new StockLot();
        stockLot.setId(stockLotId);
        return stockLotMouvementRepository.findByStockLotOrderByDateMouvementDesc(stockLot);
    }

    @Override
    public List<StockLotMouvement> getHistoryByStockLotId(String stockLotId) {
        return stockLotMouvementRepository.findHistoryByStockLotId(stockLotId);
    }

    @Override
    public List<StockLotMouvement> getHistoryByArticleId(String articleId) {
        return stockLotMouvementRepository.findHistoryByArticleId(articleId);
    }

    @Override
    public List<StockLotMouvement> getByTypeMouvement(TypeMouvement typeMouvement) {
        return stockLotMouvementRepository.findByTypeMouvement(typeMouvement);
    }

    @Override
    public List<StockLotMouvement> getByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return stockLotMouvementRepository.findByDateRange(startDate, endDate);
    }

    @Override
    public List<StockLotMouvement> getByArticleAndEntrepot(String articleId, String entrepotId) {
        return stockLotMouvementRepository.findByArticleAndEntrepot(articleId, entrepotId);
    }
}
