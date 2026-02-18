package com.example.BacK.presentation.g_Stock;

import com.example.BacK.application.g_Stock.Command.entrepot.addEntrepot.AddEntrepotCommand;
import com.example.BacK.application.g_Stock.Command.entrepot.addEntrepot.AddEntrepotResponse;
import com.example.BacK.application.g_Stock.Command.entrepot.updateEntrepot.UpdateEntrepotCommand;
import com.example.BacK.application.g_Stock.Command.entrepot.updateEntrepot.UpdateEntrepotResponse;
import com.example.BacK.application.g_Stock.Command.entrepot.deleteEntrepot.DeleteEntrepotCommand;
import com.example.BacK.application.g_Stock.Command.entrepot.deleteEntrepot.DeleteEntrepotResponse;
import com.example.BacK.application.g_Stock.Query.entrepot.GetEntrepotQuery;
import com.example.BacK.application.g_Stock.Query.entrepot.GetEntrepotResponse;
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
@RequestMapping("api/stock/entrepot")
@Tag(name = "Entrepôt", description = "API de gestion des entrepôts")
@SecurityRequirement(name = "bearerAuth")
public class EntrepotController {

    private final Mediator mediator;

    public EntrepotController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Ajouter un entrepôt", description = "Crée un nouvel entrepôt")
    @PostMapping
    // @PreAuthorize("hasAuthority('ENTREPOT_CREER')")
    public ResponseEntity<AddEntrepotResponse> add(@Valid @RequestBody AddEntrepotCommand command) {
        List<AddEntrepotResponse> results = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(results.get(0));
    }

    @Operation(summary = "Mettre à jour un entrepôt", description = "Met à jour un entrepôt existant")
    @PutMapping("/{id}")
    // @PreAuthorize("hasAuthority('ENTREPOT_MODIFIER')")
    public ResponseEntity<UpdateEntrepotResponse> update(@PathVariable String id, 
                                                         @Valid @RequestBody UpdateEntrepotCommand command) {
        command.setId(id);
        List<UpdateEntrepotResponse> results = mediator.sendToHandlers(command);
        return ResponseEntity.ok(results.get(0));
    }

    @Operation(summary = "Supprimer un entrepôt", description = "Supprime un entrepôt par son ID")
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasAuthority('ENTREPOT_SUPPRIMER')")
    public ResponseEntity<DeleteEntrepotResponse> delete(@PathVariable String id) {
        DeleteEntrepotCommand command = new DeleteEntrepotCommand(id);
        List<DeleteEntrepotResponse> results = mediator.sendToHandlers(command);
        return ResponseEntity.ok(results.get(0));
    }

    @Operation(summary = "Obtenir les entrepôts", description = "Récupère les entrepôts selon les critères")
    @GetMapping
    // @PreAuthorize("hasAuthority('ENTREPOT_LIRE')")
    public ResponseEntity<List<GetEntrepotResponse>> get(@Valid GetEntrepotQuery query) {
        List<List<GetEntrepotResponse>> results = mediator.sendToHandlers(query);
        List<GetEntrepotResponse> entrepots = results.isEmpty() ? List.of() : results.get(0);
        return ResponseEntity.ok(entrepots);
    }

    @Operation(summary = "Obtenir les entrepôts paginés", description = "Récupère les entrepôts avec pagination")
    @GetMapping("/paginated")
    // @PreAuthorize("hasAuthority('ENTREPOT_LIRE')")
    public ResponseEntity<PageResponse<GetEntrepotResponse>> getPaginated(@Valid GetEntrepotQuery query) {
        List<List<GetEntrepotResponse>> results = mediator.sendToHandlers(query);
        List<GetEntrepotResponse> entrepots = results.isEmpty() ? List.of() : results.get(0);
        
        // Calculate pagination
        int page = query.getPage() != null ? query.getPage() : 0;
        int size = query.getSize() != null ? query.getSize() : 10;
        int totalElements = entrepots.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, totalElements);
        List<GetEntrepotResponse> paginatedEntrepots = start < totalElements ? 
            entrepots.subList(start, end) : List.of();
        
        var pageResponse = new PageResponse<GetEntrepotResponse>();
        pageResponse.setContent(paginatedEntrepots);
        pageResponse.setPageNumber(page);
        pageResponse.setPageSize(size);
        pageResponse.setTotalElements((long) totalElements);
        pageResponse.setTotalPages(totalPages);
        pageResponse.setFirst(page == 0);
        pageResponse.setLast(page >= totalPages - 1);
        pageResponse.setEmpty(paginatedEntrepots.isEmpty());
        
        return ResponseEntity.ok(pageResponse);
    }

    @Operation(summary = "Obtenir un entrepôt par ID", description = "Récupère un entrepôt spécifique par son ID")
    @GetMapping("/{id}")
    // @PreAuthorize("hasAuthority('ENTREPOT_LIRE')")
    public ResponseEntity<GetEntrepotResponse> getById(@PathVariable String id) {
        GetEntrepotQuery query = new GetEntrepotQuery();
        query.setId(id);
        List<List<GetEntrepotResponse>> results = mediator.sendToHandlers(query);
        List<GetEntrepotResponse> entrepots = results.isEmpty() ? List.of() : results.get(0);
        
        if (entrepots.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(entrepots.get(0));
    }
}
