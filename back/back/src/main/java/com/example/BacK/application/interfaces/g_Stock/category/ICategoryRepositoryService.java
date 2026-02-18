package com.example.BacK.application.interfaces.g_Stock.category;

import com.example.BacK.application.g_Stock.Query.category.GetCategoryQuery;
import com.example.BacK.application.g_Stock.Query.category.GetCategoryResponse;
import com.example.BacK.application.models.PageResponse;
import com.example.BacK.domain.g_Stock.Category;

import java.util.List;

public interface ICategoryRepositoryService {
    
    String add(Category category);
    void update(Category category);
    void delete(String id);
    Category getById(String id);
    List<GetCategoryResponse> getById(GetCategoryQuery query);
    List<GetCategoryResponse> search(GetCategoryQuery query);
    List<GetCategoryResponse> getAll();
    PageResponse<GetCategoryResponse> getAllPaginated(GetCategoryQuery query);
    PageResponse<GetCategoryResponse> searchPaginated(GetCategoryQuery query);
}