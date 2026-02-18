package com.example.BacK.presentation.g_Utilisateur;
import com.example.BacK.application.g_Utilisateur.Query.permission.GetModulesQuery;
import com.example.BacK.application.g_Utilisateur.Query.permission.GetPermissionQuery;
import com.example.BacK.application.g_Utilisateur.Query.permission.GetPermissionsByModuleQuery;
import com.example.BacK.application.mediator.Mediator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PermissionController {

    private final Mediator mediator;

    @PreAuthorize("hasAuthority('PERMISSIONS_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAllPermissions() {
        return ResponseEntity.ok(mediator.sendToHandlers(new GetPermissionQuery()));
    }

    @PreAuthorize("hasAuthority('PERMISSIONS_LIRE')")
    @GetMapping("/{id}")
    public ResponseEntity<List<Object>> getPermissionById(@PathVariable String id) {
        return ResponseEntity.ok(mediator.sendToHandlers(new GetPermissionQuery(id, null)));
    }

    // Permissions are auto-generated - CRUD operations commented out
    /*
    @PostMapping
    public ResponseEntity<?> createPermission(@RequestBody AddPermissionCommand command) {
        @SuppressWarnings("unchecked")
        List<AddPermissionResponse> responses = (List<AddPermissionResponse>) (List<?>) mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses.get(0));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePermission(
            @PathVariable String id,
            @RequestBody UpdatePermissionCommand command) {
        command.setId(id);
        Object response = mediator.sendToHandlers(command);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePermission(@PathVariable String id) {
        DeletePermissionCommand command = new DeletePermissionCommand(id);
        Object response = mediator.sendToHandlers(command);
        return ResponseEntity.ok(response);
    }
    */

    @PreAuthorize("hasAuthority('PERMISSIONS_LIRE')")
    @GetMapping("/modules")
    public ResponseEntity<List<Object>> getAllModules() {
        return ResponseEntity.ok(mediator.sendToHandlers(new GetModulesQuery()));
    }
    @PreAuthorize("hasAuthority('PERMISSIONS_LIRE')")
    @GetMapping("/modules/{moduleCode}")
    public ResponseEntity<List<Object>> getPermissionsByModule(@PathVariable String moduleCode) {
        try {
            return ResponseEntity.ok(mediator.sendToHandlers(new GetPermissionsByModuleQuery(moduleCode)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
