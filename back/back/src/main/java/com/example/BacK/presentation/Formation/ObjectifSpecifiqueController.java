package com.example.BacK.presentation.Formation;

import com.example.BacK.application.g_Formation.Command.ObjectifSpecifique.addObjectifSpecifique.AddObjectifSpecifiqueCommand;
import com.example.BacK.application.g_Formation.Command.ObjectifSpecifique.create.CreateObjectifSpecifiqueCommand;
import com.example.BacK.application.g_Formation.Command.ObjectifSpecifique.create.CreateObjectifSpecifiqueHandler;
import com.example.BacK.application.g_Formation.Command.ObjectifSpecifique.deleteObjectifSpecifique.DeleteObjectifSpecifiqueCommand;
import com.example.BacK.application.g_Formation.Command.ObjectifSpecifique.updateObjectifSpecifique.UpdateObjectifSpecifiqueCommand;
import com.example.BacK.application.g_Formation.Command.ObjectifSpecifique.copyAndLink.CopyAndLinkObjectifSpecifiqueCommand;
import com.example.BacK.domain.g_Formation.ObjectifSpecifique;
import com.example.BacK.application.g_Formation.Query.ObjectifSpecifique.GetObjectifSpecifiqueQuery;
import com.example.BacK.application.g_Formation.Query.ObjectifSpecifique.GetObjectifSpecifiqueResponse;
import com.example.BacK.application.g_Formation.Query.ObjectifSpecifique.ObjectifsByContenu.GetObjectifsByContenuQuery;
import com.example.BacK.application.g_Formation.Query.ObjectifSpecifique.SearchObjectifSpecifiqueQuery;
import com.example.BacK.application.g_Formation.Query.ObjectifSpecifique.SearchObjectifSpecifiqueResponse;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/objectifsspecifiques")
@Tag(name = "Objectif Spécifique", description = "Gestion des objectifs spécifiques liés à un contenu global de formation")
public class ObjectifSpecifiqueController {

    private final Mediator mediator;
    private final CreateObjectifSpecifiqueHandler createObjectifSpecifiqueHandler;

    public ObjectifSpecifiqueController(Mediator mediator,
                                       CreateObjectifSpecifiqueHandler createObjectifSpecifiqueHandler) {
        this.mediator = mediator;
        this.createObjectifSpecifiqueHandler = createObjectifSpecifiqueHandler;
    }

    @Operation(summary = "Obtenir la liste des objectifs spécifiques", description = "Retourne tous les objectifs spécifiques existants")
    @GetMapping
    public ResponseEntity<List<GetObjectifSpecifiqueResponse>> getAllObjectifsSpecifiques() {
        List<GetObjectifSpecifiqueResponse> responses = mediator.sendToHandlers(new GetObjectifSpecifiqueQuery(null));
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Obtenir un objectif spécifique par ID", description = "Retourne les détails d'un objectif spécifique donné")
    @GetMapping("/{id}")
    public ResponseEntity<GetObjectifSpecifiqueResponse> getObjectifSpecifiqueById(@PathVariable Long id) {
        List<GetObjectifSpecifiqueResponse> responses = mediator.sendToHandlers(new GetObjectifSpecifiqueQuery(id));
        if (responses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responses.get(0));
    }

    @Operation(summary = "Créer un nouvel objectif spécifique et le lier à une formation", description = "Ajoute un objectif spécifique lié à une formation")
    @PostMapping
    public ResponseEntity<ObjectifSpecifique> addObjectifSpecifique(@Valid @RequestBody CreateObjectifSpecifiqueCommand command) {
        ObjectifSpecifique result = createObjectifSpecifiqueHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Mettre à jour un objectif spécifique", description = "Modifie les informations d’un objectif spécifique existant")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateObjectifSpecifique(
            @PathVariable Long id,
            @Valid @RequestBody UpdateObjectifSpecifiqueCommand command) {
        command.setIdObjectifSpecifique(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Supprimer un objectif spécifique", description = "Supprime un objectif spécifique existant")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObjectifSpecifique(@PathVariable Long id) {
        mediator.sendToHandlers(new DeleteObjectifSpecifiqueCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Obtenir les objectifs spécifiques d'un contenu global",
            description = "Retourne la liste des objectifs spécifiques associés à un contenu global donné"
    )
    @GetMapping("/by-contenu/{idContenuGlobal}")
    public ResponseEntity<List<GetObjectifSpecifiqueResponse>> getObjectifsSpecifiquesByContenuGlobal(
            @PathVariable Long idContenuGlobal) {

        List<GetObjectifSpecifiqueResponse> responses =
                mediator.sendToHandlers(new GetObjectifsByContenuQuery(idContenuGlobal));

        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Chercher des objectifs spécifiques par titre",
            description = "Retourne les objectifs spécifiques correspondant au critère de recherche"
    )
    @GetMapping("/search")
    public ResponseEntity<List<SearchObjectifSpecifiqueResponse>> searchObjectifSpecifiques(
            @RequestParam(value = "titre", required = false) String titre) {
        
        List<SearchObjectifSpecifiqueResponse> responses =
                mediator.sendToHandlers(new SearchObjectifSpecifiqueQuery(titre));
        
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Copier et lier un objectif spécifique à un objectif global",
            description = "Crée une copie d'un objectif spécifique existant et la lie à un objectif global"
    )
    @PostMapping("/copy-and-link")
    public ResponseEntity<Void> copyAndLinkObjectifSpecifique(
            @RequestBody CopyAndLinkObjectifSpecifiqueCommand command) {
        
        mediator.sendToHandlers(command);
        
        return ResponseEntity.ok().build();
    }
}
