package com.example.BacK.application.g_Stock.Query.Article;

import com.example.BacK.application.interfaces.g_Stock.article.IArticleRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.Article;
import com.example.BacK.infrastructure.repository.g_Stock.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetArticleQueryHandler implements RequestHandler<GetArticleQuery, GetArticleQueryResponse> {
    
    private final IArticleRepositoryService repositoryService;
    private final StockRepository stockRepository;
    
    @Override
    public GetArticleQueryResponse handle(GetArticleQuery query) {
        if (query.getId() != null && !query.getId().isEmpty()) {
            Article article = repositoryService.findById(query.getId())
                .orElseThrow(() -> new IllegalArgumentException("Article non trouvé avec l'ID: " + query.getId()));
            populateQuantiteDisponible(article);
            return new GetArticleQueryResponse(Collections.singletonList(article), 1, 1, 0);
        }
        
        Pageable pageable = PageRequest.of(
            query.getPage(),
            query.getSize(),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );
        
        Page<Article> articlePage = repositoryService.search(
            query.getSearchTerm(),
            query.getCategorie(),
            query.getMarque(),
            query.getEstActif(),
            pageable
        );
        
        List<Article> articlesWithQuantity = articlePage.getContent().stream()
            .map(article -> {
                populateQuantiteDisponible(article);
                return article;
            })
            .collect(Collectors.toList());
        
        return new GetArticleQueryResponse(
            articlesWithQuantity,
            articlePage.getTotalElements(),
            articlePage.getTotalPages(),
            articlePage.getNumber()
        );
    }
    
    private void populateQuantiteDisponible(Article article) {
        Integer totalQuantite = stockRepository.getTotalQuantiteByArticleId(article.getId());
        article.setQuantiteDisponible(totalQuantite != null ? totalQuantite : 0);
    }
}
