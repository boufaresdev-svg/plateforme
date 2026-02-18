package com.example.BacK.infrastructure.services.g_Stock;

import com.example.BacK.application.interfaces.g_Stock.stock.IStockRepositoryService;
import com.example.BacK.domain.g_Stock.Stock;
import com.example.BacK.infrastructure.repository.g_Stock.StockRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StockRepositoryService implements IStockRepositoryService {

    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    public StockRepositoryService(StockRepository stockRepository, ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Stock add(Stock stock) {
        return stockRepository.save(stock);
    }

    @Override
    public Stock update(Stock stock) {
        return stockRepository.save(stock);
    }

    @Override
    public void delete(String id) {
        stockRepository.deleteById(id);
    }

    @Override
    public Stock getById(String id) {
        return stockRepository.findById(id).orElse(null);
    }

    @Override
    public List<Stock> getAll() {
        return stockRepository.findAll();
    }

    @Override
    public List<Stock> getByArticleId(String articleId) {
        return stockRepository.findByArticleId(articleId);
    }

    @Override
    public List<Stock> getByEntrepotId(String entrepotId) {
        return stockRepository.findByEntrepotId(entrepotId);
    }

    @Override
    public Stock getByArticleIdAndEntrepotId(String articleId, String entrepotId) {
        return stockRepository.findByArticleIdAndEntrepotId(articleId, entrepotId).orElse(null);
    }

    @Override
    public Stock findByArticleIdAndEntrepotId(String articleId, String entrepotId) {
        return stockRepository.findByArticleIdAndEntrepotId(articleId, entrepotId).orElse(null);
    }

    @Override
    public List<Stock> getStocksFaibles(Integer seuil) {
        return stockRepository.findStocksFaibles(seuil);
    }

    @Override
    public List<Stock> getStocksExpires() {
        return stockRepository.findStocksExpires();
    }
}
