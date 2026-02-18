package com.example.BacK.presentation.Formation;

import com.example.BacK.application.g_Formation.Command.Formateur.addFormateur.AddFormateurCommand;
import com.example.BacK.application.g_Formation.Command.Formateur.deleteFormateur.DeleteFormateurCommand;
import com.example.BacK.application.g_Formation.Command.Formateur.updateFormateur.UpdateFormateurCommand;
import com.example.BacK.application.g_Formation.Command.Formateur.updateFormateur.UpdateFormateurWithFile.UpdateFormateurWithFileCommand;
import com.example.BacK.application.g_Formation.Query.Formateur.GetFormateurQuery;
import com.example.BacK.application.g_Formation.Query.Formateur.GetFormateurResponse;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/formateurs")
@Tag(name = "Formateur", description = "Gestion des formateurs (création, modification, consultation, suppression)")
public class FormateurController {

    private final Mediator mediator;

    public FormateurController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(
            summary = "Obtenir la liste des formateurs",
            description = "Retourne la liste complète des formateurs disponibles dans le système"
    )
    @GetMapping
    public ResponseEntity<List<GetFormateurResponse>> getAllFormateurs() {
        List<GetFormateurResponse> formateurs = mediator.sendToHandlers(new GetFormateurQuery(null));
        return ResponseEntity.ok(formateurs);
    }

    @Operation(
            summary = "Créer un nouveau formateur",
            description = "Ajoute un formateur avec ses informations (nom, spécialité, contact, etc.) et un document (CV, diplôme...)"
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> addFormateur(@Valid @ModelAttribute AddFormateurCommand command) {
        Object result = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

        @Operation(
            summary = "Mettre à jour un formateur (sans fichier)",
            description = "Modifie les informations d'un formateur existant à partir de son identifiant, sans changer le document."
    )
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateFormateur(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFormateurCommand command
    ) {
        command.setIdFormateur(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Obtenir un formateur par ID",
            description = "Retourne les détails complets d'un formateur spécifique à partir de son identifiant"
    )
    @GetMapping("/{id}")
    public ResponseEntity<GetFormateurResponse> getFormateurById(@PathVariable Long id) {
        GetFormateurResponse response =
                (GetFormateurResponse) mediator.sendToHandlers(new GetFormateurQuery(id));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Supprimer un formateur",
            description = "Supprime un formateur existant de la base de données"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFormateur(@PathVariable Long id) {
        mediator.sendToHandlers(new DeleteFormateurCommand(id));
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Mettre à jour un formateur (avec fichier)",
            description = "Met à jour les informations d’un formateur et remplace son document."
    )
    @PutMapping(value = "/{id}/with-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateFormateurWithFile(
            @PathVariable Long id,
            @ModelAttribute UpdateFormateurWithFileCommand command
    ) {
        command.setIdFormateur(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

}

