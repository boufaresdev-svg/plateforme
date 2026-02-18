package com.example.BacK.presentation.g_Stock;

import com.example.BacK.application.g_Stock.Command.marque.addMarque.AddMarqueCommand;
import com.example.BacK.application.g_Stock.Command.marque.addMarque.AddMarqueResponse;
import com.example.BacK.application.g_Stock.Command.marque.updateMarque.UpdateMarqueCommand;
import com.example.BacK.application.g_Stock.Command.marque.updateMarque.UpdateMarqueResponse;
import com.example.BacK.application.g_Stock.Command.marque.deleteMarque.DeleteMarqueCommand;
import com.example.BacK.application.g_Stock.Command.marque.deleteMarque.DeleteMarqueResponse;
import com.example.BacK.application.g_Stock.Query.marque.GetMarqueQuery;
import com.example.BacK.application.g_Stock.Query.marque.GetMarqueResponse;
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
@RequestMapping("api/stock/marque")
@Tag(name = "Marque", description = "API de gestion des marques")
@SecurityRequirement(name = "bearerAuth")
public class MarqueController {

    private final Mediator mediator;

    public MarqueController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Ajouter une marque", description = "Crée une nouvelle marque")
    @PostMapping
    // @PreAuthorize("hasAuthority('MARQUE_CREER')")
    public ResponseEntity<AddMarqueResponse> add(@Valid @RequestBody AddMarqueCommand command) {
        List<AddMarqueResponse> results = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(results.get(0));
    }

    @Operation(summary = "Mettre à jour une marque", description = "Met à jour une marque existante")
    @PutMapping("/{id}")
    // @PreAuthorize("hasAuthority('MARQUE_MODIFIER')")
    public ResponseEntity<UpdateMarqueResponse> update(@PathVariable String id, 
                                                       @Valid @RequestBody UpdateMarqueCommand command) {
        command.setId(id);
        List<UpdateMarqueResponse> results = mediator.sendToHandlers(command);
        return ResponseEntity.ok(results.get(0));
    }

    @Operation(summary = "Supprimer une marque", description = "Supprime une marque par son ID")
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasAuthority('MARQUE_SUPPRIMER')")
    public ResponseEntity<DeleteMarqueResponse> delete(@PathVariable String id) {
        DeleteMarqueCommand command = new DeleteMarqueCommand(id);
        List<DeleteMarqueResponse> results = mediator.sendToHandlers(command);
        return ResponseEntity.ok(results.get(0));
    }

    @Operation(summary = "Obtenir les marques", description = "Récupère les marques selon les critères")
    @GetMapping
    // @PreAuthorize("hasAuthority('MARQUE_LIRE')")
    public ResponseEntity<List<GetMarqueResponse>> get(@Valid GetMarqueQuery query) {
        List<List<GetMarqueResponse>> results = mediator.sendToHandlers(query);
        List<GetMarqueResponse> marques = results.isEmpty() ? List.of() : results.get(0);
        return ResponseEntity.ok(marques);
    }

    @Operation(summary = "Obtenir les marques paginées", description = "Récupère les marques avec pagination")
    @GetMapping("/paginated")
    // @PreAuthorize("hasAuthority('MARQUE_LIRE')")
    public ResponseEntity<PageResponse<GetMarqueResponse>> getPaginated(@Valid GetMarqueQuery query) {
        List<List<GetMarqueResponse>> results = mediator.sendToHandlers(query);
        List<GetMarqueResponse> marques = results.isEmpty() ? List.of() : results.get(0);
        
        // Calculate pagination
        int page = query.getPage() != null ? query.getPage() : 0;
        int size = query.getSize() != null ? query.getSize() : 10;
        int totalElements = marques.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, totalElements);
        List<GetMarqueResponse> paginatedMarques = start < totalElements ? 
            marques.subList(start, end) : List.of();
        
        var pageResponse = new PageResponse<GetMarqueResponse>();
        pageResponse.setContent(paginatedMarques);
        pageResponse.setPageNumber(page);
        pageResponse.setPageSize(size);
        pageResponse.setTotalElements((long) totalElements);
        pageResponse.setTotalPages(totalPages);
        pageResponse.setFirst(page == 0);
        pageResponse.setLast(page >= totalPages - 1);
        pageResponse.setEmpty(paginatedMarques.isEmpty());
        
        return ResponseEntity.ok(pageResponse);
    }

    @Operation(summary = "Obtenir une marque par ID", description = "Récupère une marque spécifique par son ID")
    @GetMapping("/{id}")
    // @PreAuthorize("hasAuthority('MARQUE_LIRE')")
    public ResponseEntity<GetMarqueResponse> getById(@PathVariable String id) {
        GetMarqueQuery query = new GetMarqueQuery();
        query.setId(id);
        List<List<GetMarqueResponse>> results = mediator.sendToHandlers(query);
        List<GetMarqueResponse> marques = results.isEmpty() ? List.of() : results.get(0);
        if (marques.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(marques.get(0));
    }
}
