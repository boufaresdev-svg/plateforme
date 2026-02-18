package com.example.BacK.application.g_Stock.Query.tracabilite;

import com.example.BacK.domain.g_Stock.enumEntity.TypeMouvement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTracabiliteQuery {
    
    private String id;
    private String articleId;
    private String entrepotId;
    private String utilisateurId;
    private TypeMouvement typeMouvement;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;
    
    // Pagination
    private Integer page;
    private Integer size;
    private String sortBy = "dateMouvement";
    private String sortDirection = "DESC";
}
