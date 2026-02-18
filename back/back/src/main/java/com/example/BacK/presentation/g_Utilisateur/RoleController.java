package com.example.BacK.presentation.g_Utilisateur;

import com.example.BacK.application.g_Utilisateur.Command.role.addRole.AddRoleCommand;
import com.example.BacK.application.g_Utilisateur.Command.role.addRole.AddRoleResponse;
import com.example.BacK.application.g_Utilisateur.Command.role.assignPermission.AssignPermissionCommand;
import com.example.BacK.application.g_Utilisateur.Command.role.deleteRole.DeleteRoleCommand;
import com.example.BacK.application.g_Utilisateur.Command.role.removePermission.RemovePermissionCommand;
import com.example.BacK.application.g_Utilisateur.Command.role.updateRole.UpdateRoleCommand;
import com.example.BacK.application.g_Utilisateur.Query.role.GetRoleQuery;
import com.example.BacK.application.mediator.Mediator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class RoleController {

    private final Mediator mediator;

    @PreAuthorize("hasAuthority('ROLES_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAllRoles() {
        return ResponseEntity.ok(mediator.sendToHandlers(new GetRoleQuery()));
    }

    @PreAuthorize("hasAuthority('ROLES_LIRE')")
    @GetMapping("/{id}")
    public ResponseEntity<List<Object>> getRoleById(@PathVariable String id) {
        return ResponseEntity.ok(mediator.sendToHandlers(new GetRoleQuery(id, null)));
    }

    @PreAuthorize("hasAuthority('ROLES_CREER')")
    @PostMapping
    public ResponseEntity<List<AddRoleResponse>> createRole(@RequestBody AddRoleCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @PreAuthorize("hasAuthority('ROLES_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<List<Object>> updateRole(
            @PathVariable String id,
            @RequestBody UpdateRoleCommand command) {
        command.setId(id);
        return ResponseEntity.ok(mediator.sendToHandlers(command));
    }

    @PreAuthorize("hasAuthority('ROLES_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<List<Object>> deleteRole(@PathVariable String id) {
        return ResponseEntity.ok(mediator.sendToHandlers(new DeleteRoleCommand(id)));
    }

    @PreAuthorize("hasAuthority('ROLES_MODIFIER')")
    @PostMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<List<Object>> assignPermissionToRole(
            @PathVariable String roleId,
            @PathVariable String permissionId) {
        return ResponseEntity.ok(mediator.sendToHandlers(new AssignPermissionCommand(roleId, permissionId)));
    }

    @PreAuthorize("hasAuthority('ROLES_MODIFIER')")
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<List<Object>> removePermissionFromRole(
            @PathVariable String roleId,
            @PathVariable String permissionId) {
        return ResponseEntity.ok(mediator.sendToHandlers(new RemovePermissionCommand(roleId, permissionId)));
    }
}
