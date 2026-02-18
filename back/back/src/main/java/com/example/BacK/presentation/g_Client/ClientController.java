package com.example.BacK.presentation.g_Client;

import com.example.BacK.application.g_Client.Command.client.DeleteClient.DeleteClientCommand;
import com.example.BacK.application.g_Client.Command.client.UpdateClient.UpdateClientCommand;
import com.example.BacK.application.g_Client.Command.client.addClient.AddClientCommand;
import com.example.BacK.application.g_Client.Command.client.addClient.AddClientResponse;
import com.example.BacK.application.g_Client.Query.client.GetClientQuery;
import com.example.BacK.application.g_Client.Query.client.GetClientResponse;
import com.example.BacK.application.mediator.Mediator;
import com.example.BacK.application.models.g_Client.ClientDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("api/client")
public class ClientController {

    private final Mediator mediator;

    public ClientController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Ajouter un client", description = "Crée un nouveau client")
    @PreAuthorize("hasAuthority('CLIENT_CREER')")
    @PostMapping
    public ResponseEntity<List<AddClientResponse>> add(@RequestBody AddClientCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(summary = "Mettre à jour un client", description = "Met à jour un client existant")
    @PreAuthorize("hasAuthority('CLIENT_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody UpdateClientCommand command) {
        command.setId(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Supprimer un client", description = "Supprime un client par son ID")
    @PreAuthorize("hasAuthority('CLIENT_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediator.sendToHandlers(new DeleteClientCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Filtrer les clients", description = "Recherche des clients selon des critères")
    @PreAuthorize("hasAuthority('CLIENT_LIRE')")
    @PostMapping("/search")
    public ResponseEntity<List<Object>> filter(@RequestBody GetClientQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(summary = "Récupérer tous les clients", description = "Retourne la liste de tous les clients")
    @PreAuthorize("hasAuthority('CLIENT_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAll() {
        GetClientQuery query = new GetClientQuery();
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
}
