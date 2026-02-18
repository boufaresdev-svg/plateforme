package com.example.BacK.presentation.g_RH;


import com.example.BacK.application.g_RH.Command.employee.addEmployee.AddEmployeeCommand;
import com.example.BacK.application.g_RH.Command.employee.addEmployee.AddEmployeeResponse;
import com.example.BacK.application.g_RH.Command.employee.deleteEmployee.DeleteEmployeeCommand;
import com.example.BacK.application.g_RH.Command.employee.updateEmployee.UpdateEmployeeCommand;
import com.example.BacK.application.g_RH.Query.employee.GetEmployeeQuery;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/Employee")
public class EmployeeController {

    private final Mediator mediator;

    public EmployeeController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(
            summary = "Ajouter un employé",
            description = "Crée un nouvel employé appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('EMPLOYEE_CREER')")
    @PostMapping
    public ResponseEntity<List<AddEmployeeResponse>> add(@RequestBody AddEmployeeCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(
            summary = "Mettre à jour un employé",
            description = "Met à jour un employé existant appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('EMPLOYEE_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody UpdateEmployeeCommand command) {
        command.setId(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Supprimer un employé",
            description = "Supprime un employé appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('EMPLOYEE_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediator.sendToHandlers(new DeleteEmployeeCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Filtrer les employés",
            description = "Recherche les employés appliqués en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('EMPLOYEE_LIRE')")
    @PostMapping("/search")
    public ResponseEntity<List<Object>> filter(@RequestBody GetEmployeeQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(
            summary = "Récupérer tous les employés",
            description = "Retourne la liste de tous les employés appliqués en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('EMPLOYEE_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAll() {
        GetEmployeeQuery query = new GetEmployeeQuery();
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
}