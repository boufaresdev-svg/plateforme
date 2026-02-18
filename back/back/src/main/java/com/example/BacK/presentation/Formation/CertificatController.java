package com.example.BacK.presentation.Formation;

import com.example.BacK.application.g_Formation.Command.Certificat.addCertificat.AddCertificatCommand;
import com.example.BacK.application.g_Formation.Command.Certificat.deleteCertificat.DeleteCertificatCommand;
import com.example.BacK.application.g_Formation.Command.Certificat.updateCertificat.UpdateCertificatCommand;
import com.example.BacK.application.g_Formation.Command.Domaine.addDomaine.AddDomaineCommand;
import com.example.BacK.application.g_Formation.Query.Certificat.CertificatByExamen.GetCertificatByExamenQuery;
import com.example.BacK.application.g_Formation.Query.Certificat.CertificatByExamen.GetCertificatByExamenResponse;
import com.example.BacK.application.g_Formation.Query.Certificat.GetCertificatQuery;
import com.example.BacK.application.g_Formation.Query.Certificat.GetCertificatResponse;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificats")
@Tag(name = "Certificat", description = "Gestion des certificats (CRUD)")
public class CertificatController {

    private final Mediator mediator;

    public CertificatController(Mediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    @Operation(summary = "Ajouter un nouveau certificat")
    public ResponseEntity<Object> addCertificat(@Valid @RequestBody AddCertificatCommand command) {
        Object result = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un certificat existant")
    public ResponseEntity<Void> updateCertificat(@PathVariable Long id, @RequestBody UpdateCertificatCommand command) {
        command.setIdCertificat(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un certificat par ID")
    public ResponseEntity<Void> deleteCertificat(@PathVariable Long id) {
        mediator.sendToHandlers(new DeleteCertificatCommand(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Obtenir la liste de tous les certificats")
    public ResponseEntity<List<GetCertificatResponse>> getAllCertificats() {
        List<GetCertificatResponse> responses = mediator.sendToHandlers(new GetCertificatQuery());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un certificat par ID")
    public ResponseEntity<?> getCertificatById(@PathVariable Long id) {
        GetCertificatQuery query = new GetCertificatQuery(id);
        List<GetCertificatResponse> responses = mediator.sendToHandlers(query);

        if (responses == null || responses.isEmpty()) {
            return ResponseEntity.status(404).body("{\"message\": \"Certificat non trouvé\"}");
        }

        return ResponseEntity.ok(responses.get(0));
    }


    @GetMapping("/by-examen/{idExamen}")
    @Operation(summary = "Obtenir les certificats associés à un examen")
    public ResponseEntity<List<GetCertificatByExamenResponse>> getCertificatByExamen(
            @PathVariable Long idExamen) {

        GetCertificatByExamenQuery query = new GetCertificatByExamenQuery(idExamen);
        List<GetCertificatByExamenResponse> responses = mediator.sendToHandlers(query);

        return ResponseEntity.ok(responses);
    }


}
