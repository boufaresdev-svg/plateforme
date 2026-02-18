package com.example.BacK.presentation.g_Projet;
import com.example.BacK.application.g_Projet.Command.EmployeAffecte.add.AddEmployeeAffecteCommand;
import com.example.BacK.application.g_Projet.Command.EmployeAffecte.add.AddEmployeeAffecteResponse;
import com.example.BacK.application.g_Projet.Command.EmployeAffecte.delete.DeleteEmployeeAffecteCommand;
import com.example.BacK.application.g_Projet.Command.EmployeAffecte.update.UpdateEmployeeAffecteCommand;
import com.example.BacK.application.g_Projet.Query.EmployeAffecte.all.GetEmployeAffecteQuery;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/employeAffectes")
public class EmployeAffecteController {

    private final Mediator mediator;

    public EmployeAffecteController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(
            summary = "Ajouter un employé affecté",
            description = "Crée un nouvel employé affecté appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('EMPLOYEAFFECTE_CREER')")
    @PostMapping
    public ResponseEntity<List<AddEmployeeAffecteResponse>> add(@RequestBody AddEmployeeAffecteCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(
            summary = "Mettre à jour un employé affecté",
            description = "Met à jour un employé affecté existant appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('EMPLOYEAFFECTE_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody UpdateEmployeeAffecteCommand command) {
        command.setId(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Supprimer un employé affecté",
            description = "Supprime un employé affecté appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('EMPLOYEAFFECTE_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediator.sendToHandlers(new DeleteEmployeeAffecteCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Filtrer les employés affectés",
            description = "Recherche les employés affectés appliqués en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('EMPLOYEAFFECTE_CREER')")
    @PostMapping("/search")
    public ResponseEntity<List<Object>> filter(@RequestBody GetEmployeAffecteQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(
            summary = "Récupérer tous les employés affectés",
            description = "Retourne la liste de tous les employés affectés appliqués en interne selon le protocole de travail de la société"
    )

    @PreAuthorize("hasAuthority('EMPLOYEAFFECTE_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAll() {
        GetEmployeAffecteQuery query = new GetEmployeAffecteQuery();
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
}
