package com.example.BacK.application.g_Stock.Query.Article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetArticleQuery {
    private String id;
    private String searchTerm;
    private String categorie;
    private String marque;
    private Boolean estActif;
    private int page;
    private int size;
}
