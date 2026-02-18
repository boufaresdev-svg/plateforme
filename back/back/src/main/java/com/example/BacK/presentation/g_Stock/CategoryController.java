package com.example.BacK.presentation.g_Stock;

import com.example.BacK.application.g_Stock.Command.category.addCategory.AddCategoryCommand;
import com.example.BacK.application.g_Stock.Command.category.addCategory.AddCategoryResponse;
import com.example.BacK.application.g_Stock.Command.category.updateCategory.UpdateCategoryCommand;
import com.example.BacK.application.g_Stock.Command.category.updateCategory.UpdateCategoryResponse;
import com.example.BacK.application.g_Stock.Command.category.deleteCategory.DeleteCategoryCommand;
import com.example.BacK.application.g_Stock.Command.category.deleteCategory.DeleteCategoryResponse;
import com.example.BacK.application.g_Stock.Query.category.GetCategoryQuery;
import com.example.BacK.application.g_Stock.Query.category.GetCategoryResponse;
import com.example.BacK.application.mediator.Mediator;
import com.example.BacK.application.models.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/stock/category")
@Tag(name = "Category", description = "API de gestion des catégories de stock")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final Mediator mediator;

    public CategoryController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Ajouter une catégorie", description = "Crée une nouvelle catégorie de stock")
    @PostMapping
    // @PreAuthorize("hasAuthority('CATEGORIE_CREER')")
    public ResponseEntity<AddCategoryResponse> add(@Valid @RequestBody AddCategoryCommand command) {
        List<AddCategoryResponse> results = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(results.get(0));
    }

    @Operation(summary = "Mettre à jour une catégorie", description = "Met à jour une catégorie existante")
    @PutMapping("/{id}")
    // @PreAuthorize("hasAuthority('CATEGORIE_MODIFIER')")
    public ResponseEntity<UpdateCategoryResponse> update(@PathVariable String id, 
                                                              @Valid @RequestBody UpdateCategoryCommand command) {
        command.setId(id);
        List<UpdateCategoryResponse> results = mediator.sendToHandlers(command);
        return ResponseEntity.ok(results.get(0));
    }

    @Operation(summary = "Supprimer une catégorie", description = "Supprime une catégorie par son ID")
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasAuthority('CATEGORIE_SUPPRIMER')")
    public ResponseEntity<DeleteCategoryResponse> delete(@PathVariable String id) {
        DeleteCategoryCommand command = new DeleteCategoryCommand(id);
        List<DeleteCategoryResponse> results = mediator.sendToHandlers(command);
        return ResponseEntity.ok(results.get(0));
    }

    @Operation(summary = "Obtenir les catégories", description = "Récupère les catégories selon les critères")
    @GetMapping
    // @PreAuthorize("hasAuthority('CATEGORIE_LIRE')")
    public ResponseEntity<List<GetCategoryResponse>> get(@Valid GetCategoryQuery query) {
        List<List<GetCategoryResponse>> results = mediator.sendToHandlers(query);
        // Flatten the list of lists into a single list
        List<GetCategoryResponse> categories = results.isEmpty() ? List.of() : results.get(0);
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Obtenir les catégories paginées", description = "Récupère les catégories avec pagination")
    @GetMapping("/paginated")
    // @PreAuthorize("hasAuthority('CATEGORIE_LIRE')")
    public ResponseEntity<PageResponse<GetCategoryResponse>> getPaginated(@Valid GetCategoryQuery query) {
        List<List<GetCategoryResponse>> results = mediator.sendToHandlers(query);
        List<GetCategoryResponse> categories = results.isEmpty() ? List.of() : results.get(0);
        
        // Calculate pagination
        int page = query.getPage() != null ? query.getPage() : 0;
        int size = query.getSize() != null ? query.getSize() : 10;
        int totalElements = categories.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, totalElements);
        List<GetCategoryResponse> paginatedCategories = start < totalElements ? 
            categories.subList(start, end) : List.of();
        
        PageResponse<GetCategoryResponse> pageResponse = new PageResponse<>();
        pageResponse.setContent(paginatedCategories);
        pageResponse.setTotalElements((long) totalElements);
        pageResponse.setTotalPages(totalPages);
        pageResponse.setPageNumber(page);
        pageResponse.setPageSize(size);
        
        return ResponseEntity.ok(pageResponse);
    }

    @Operation(summary = "Obtenir une catégorie par ID", description = "Récupère une catégorie spécifique par son ID")
    @GetMapping("/{id}")
    // @PreAuthorize("hasAuthority('CATEGORIE_LIRE')")
    public ResponseEntity<GetCategoryResponse> getById(@PathVariable String id) {
        GetCategoryQuery query = new GetCategoryQuery();
        query.setId(id);
        List<List<GetCategoryResponse>> results = mediator.sendToHandlers(query);
        List<GetCategoryResponse> categories = results.isEmpty() ? List.of() : results.get(0);
        if (categories.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categories.get(0));
    }
}