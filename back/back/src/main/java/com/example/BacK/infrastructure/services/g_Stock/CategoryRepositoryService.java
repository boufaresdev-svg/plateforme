package com.example.BacK.infrastructure.services.g_Stock;

import com.example.BacK.application.g_Stock.Query.category.GetCategoryQuery;
import com.example.BacK.application.g_Stock.Query.category.GetCategoryResponse;
import com.example.BacK.application.interfaces.g_Stock.category.ICategoryRepositoryService;
import com.example.BacK.application.models.PageResponse;
import com.example.BacK.domain.g_Stock.Category;
import com.example.BacK.infrastructure.repository.g_Stock.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryRepositoryService implements ICategoryRepositoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryRepositoryService(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public String add(Category category) {
        Category savedCategory = categoryRepository.save(category);
        return savedCategory.getId();
    }

    @Override
    public void update(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public void delete(String id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public Category getById(String id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetCategoryResponse> getById(GetCategoryQuery query) {
        Category category = getById(query.getId());
        if (category != null) {
            return List.of(mapToCategoryResponse(category));
        }
        return List.of();
    }

    @Override
    public List<GetCategoryResponse> search(GetCategoryQuery query) {
        List<Category> categories = categoryRepository.findBySearchCriteria(
            query.getNom(),
            query.getDescription(),
            query.getEstActif()
        );
        
        return categories.stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetCategoryResponse> getAll() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<GetCategoryResponse> getAllPaginated(GetCategoryQuery query) {
        Sort sort = Sort.by("ASC".equalsIgnoreCase(query.getSortDirection()) 
            ? Sort.Direction.ASC : Sort.Direction.DESC, query.getSortBy());
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize(), sort);
        
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        
        List<GetCategoryResponse> content = categoryPage.getContent().stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
        
        PageResponse<GetCategoryResponse> pageResponse = new PageResponse<>();
        pageResponse.setContent(content);
        pageResponse.setTotalElements(categoryPage.getTotalElements());
        pageResponse.setTotalPages(categoryPage.getTotalPages());
        
        return pageResponse;
    }

    @Override
    public PageResponse<GetCategoryResponse> searchPaginated(GetCategoryQuery query) {
        Sort sort = Sort.by("ASC".equalsIgnoreCase(query.getSortDirection()) 
            ? Sort.Direction.ASC : Sort.Direction.DESC, query.getSortBy());
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize(), sort);
        
        Page<Category> categoryPage = categoryRepository.findBySearchCriteriaPageable(
            query.getNom(),
            query.getDescription(),
            query.getEstActif(),
            pageable
        );
        
        List<GetCategoryResponse> content = categoryPage.getContent().stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
        
        PageResponse<GetCategoryResponse> pageResponse = new PageResponse<>();
        pageResponse.setContent(content);
        pageResponse.setTotalElements(categoryPage.getTotalElements());
        pageResponse.setTotalPages(categoryPage.getTotalPages());
        
        return pageResponse;
    }
    
    private GetCategoryResponse mapToCategoryResponse(Category category) {
        GetCategoryResponse response = modelMapper.map(category, GetCategoryResponse.class);
        Long count = categoryRepository.countArticlesByCategory(category.getId());
        response.setNombreProduits(count);
        return response;
    }
}