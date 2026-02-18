package com.example.BacK.application.Features.g_Stock.Queries.Alerte;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetStockAlertsResponse {
    private List<AlerteStockDto> alertes;
    private List<AlerteStockDto> content;  // For pagination compatibility
    private Integer totalCount;
    private Long totalElements;  // For pagination compatibility
    private Integer totalPages;
    private Integer size;
    private Integer number;  // Current page number (0-based)
    private Integer unreadCount;
    private Integer criticalCount;
    private Boolean first;
    private Boolean last;
    private Boolean empty;
}
