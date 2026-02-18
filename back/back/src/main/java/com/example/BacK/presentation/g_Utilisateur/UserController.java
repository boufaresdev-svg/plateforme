package com.example.BacK.presentation.g_Utilisateur;

import com.example.BacK.application.g_Utilisateur.Command.user.addUser.AddUserCommand;
import com.example.BacK.application.g_Utilisateur.Command.user.addUser.AddUserResponse;
import com.example.BacK.application.g_Utilisateur.Command.user.assignRole.AssignRoleCommand;
import com.example.BacK.application.g_Utilisateur.Command.user.assignRole.AssignRoleResponse;
import com.example.BacK.application.g_Utilisateur.Command.user.deleteUser.DeleteUserCommand;
import com.example.BacK.application.g_Utilisateur.Command.user.deleteUser.DeleteUserResponse;
import com.example.BacK.application.g_Utilisateur.Command.user.removeRole.RemoveRoleCommand;
import com.example.BacK.application.g_Utilisateur.Command.user.removeRole.RemoveRoleResponse;
import com.example.BacK.application.g_Utilisateur.Command.user.updateUser.UpdateUserCommand;
import com.example.BacK.application.g_Utilisateur.Command.user.updateUser.UpdateUserResponse;
import com.example.BacK.application.g_Utilisateur.Query.user.GetUserQuery;
import com.example.BacK.application.g_Utilisateur.Query.userStats.GetUserStatsQuery;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "User Management", description = "API de gestion des utilisateurs")
public class UserController {

    private final Mediator mediator;

    public UserController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Récupérer les statistiques des utilisateurs", description = "Retourne les statistiques globales des utilisateurs")
    @PreAuthorize("hasAuthority('USERS_LIRE')")
    @GetMapping("/stats")
    public ResponseEntity<List<Object>> getUserStats() {
        return ResponseEntity.ok(mediator.sendToHandlers(new GetUserStatsQuery()));
    }

    @Operation(summary = "Récupérer un utilisateur par ID", description = "Retourne un utilisateur spécifique")
    @PreAuthorize("hasAuthority('USERS_LIRE')")
    @GetMapping("/{id}")
    public ResponseEntity<List<Object>> getUserById(@PathVariable String id) {
        GetUserQuery query = new GetUserQuery();
        query.setId(id);
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(summary = "Rechercher et filtrer les utilisateurs", description = "Recherche unifiée pour tous les utilisateurs avec support de filtres multiples")
    @PreAuthorize("hasAuthority('USERS_LIRE')")
    @PostMapping("/search")
    public ResponseEntity<List<Object>> search(@RequestBody GetUserQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(summary = "Créer un utilisateur", description = "Crée un nouvel utilisateur")
    @PreAuthorize("hasAuthority('USERS_CREER')")
    @PostMapping
    public ResponseEntity<List<AddUserResponse>> createUser(@Valid @RequestBody AddUserCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(summary = "Mettre à jour un utilisateur", description = "Met à jour un utilisateur existant")
    @PreAuthorize("hasAuthority('USERS_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<List<UpdateUserResponse>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserCommand command) {
        command.setId(id);
        return ResponseEntity.ok(mediator.sendToHandlers(command));
    }

    @Operation(summary = "Supprimer un utilisateur", description = "Supprime un utilisateur par son ID")
    @PreAuthorize("hasAuthority('USERS_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<List<DeleteUserResponse>> deleteUser(@PathVariable String id) {
        return ResponseEntity.ok(mediator.sendToHandlers(new DeleteUserCommand(id)));
    }

    @Operation(summary = "Assigner un rôle à un utilisateur", description = "Ajoute un rôle à un utilisateur")
    @PreAuthorize("hasAuthority('USERS_MODIFIER')")
    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<List<AssignRoleResponse>> assignRoleToUser(
            @PathVariable String userId,
            @PathVariable String roleId) {
        return ResponseEntity.ok(mediator.sendToHandlers(new AssignRoleCommand(userId, roleId)));
    }

    @Operation(summary = "Retirer un rôle d'un utilisateur", description = "Supprime un rôle d'un utilisateur")
    @PreAuthorize("hasAuthority('USERS_MODIFIER')")
    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<List<RemoveRoleResponse>> removeRoleFromUser(
            @PathVariable String userId,
            @PathVariable String roleId) {
        return ResponseEntity.ok(mediator.sendToHandlers(new RemoveRoleCommand(userId, roleId)));
    }
}
