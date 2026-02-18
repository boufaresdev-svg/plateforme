package com.example.BacK.presentation.g_Projet;

import com.example.BacK.application.g_Projet.Command.mission.add.AddMissionCommand;
import com.example.BacK.application.g_Projet.Command.mission.add.AddMissionResponse;
import com.example.BacK.application.g_Projet.Command.mission.delete.DeleteMissionCommand;
import com.example.BacK.application.g_Projet.Command.mission.update.UpdateMissionCommand;
import com.example.BacK.application.g_Projet.Query.mission.all.GetMissionQuery;
import com.example.BacK.application.g_Projet.Query.mission.byId.GetMissionByIDQuery;
import com.example.BacK.application.g_Projet.Query.phase.byId.GetPhaseByIDQuery;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/missions")
public class MissionController {

    private final Mediator mediator;

    public MissionController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(
            summary = "Ajouter une mission",
            description = "Crée une nouvelle mission appliquée en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('MISSION_CREER')")
    @PostMapping
    public ResponseEntity<List<AddMissionResponse>> add(@RequestBody AddMissionCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(
            summary = "Mettre à jour une mission",
            description = "Met à jour une mission existante appliquée en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('MISSION_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody UpdateMissionCommand command) {
        command.setId(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Supprimer une mission",
            description = "Supprime une mission appliquée en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('MISSION_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediator.sendToHandlers(new DeleteMissionCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Filtrer les missions",
            description = "Recherche les missions appliquées en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('MISSION_CREER')")
    @PostMapping("/search")
    public ResponseEntity<List<Object>> filter(@RequestBody GetMissionQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(
            summary = "Récupérer toutes les missions",
            description = "Retourne la liste de toutes les missions appliquées en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('MISSION_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAll() {
        GetMissionQuery query = new GetMissionQuery();
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(summary = "Récupérer une missions par ID")
    @PreAuthorize("hasAuthority('MISSION_LIRE')")
    @GetMapping("/mission/{id}")
    public ResponseEntity<List<Object>>getById(@PathVariable String id) {
        GetMissionByIDQuery query = new GetMissionByIDQuery(id);
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
}
