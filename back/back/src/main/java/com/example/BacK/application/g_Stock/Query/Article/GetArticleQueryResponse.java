package com.example.BacK.application.g_Stock.Query.Article;

import com.example.BacK.domain.g_Stock.Article;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetArticleQueryResponse {
    private List<Article> articles;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}
