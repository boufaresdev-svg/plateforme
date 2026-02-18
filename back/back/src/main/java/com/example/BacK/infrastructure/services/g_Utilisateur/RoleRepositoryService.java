package com.example.BacK.infrastructure.services.g_Utilisateur;

import com.example.BacK.application.g_Utilisateur.Query.role.GetRoleQuery;
import com.example.BacK.application.g_Utilisateur.Query.role.GetRoleResponse;
import com.example.BacK.application.interfaces.g_Utilisateur.role.IRoleRepositoryService;
import com.example.BacK.application.models.g_Utilisateur.PermissionDTO;
import com.example.BacK.domain.g_Utilisateurs.Permission;
import com.example.BacK.domain.g_Utilisateurs.Role;
import com.example.BacK.infrastructure.repository.g_Utilisateur.PermissionRepository;
import com.example.BacK.infrastructure.repository.g_Utilisateur.RoleRepository;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleRepositoryService implements IRoleRepositoryService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ModelMapper modelMapper;

    public RoleRepositoryService(RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public String add(Role role, Set<String> permissionIds) {
        if (roleRepository.existsByNom(role.getNom())) {
            throw new IllegalArgumentException("Le rôle '" + role.getNom() + "' existe déjà");
        }

        // Ensure systemRole is always set (not null)
        if (role.getSystemRole() == null) {
            role.setSystemRole(false);
        }

        if (permissionIds != null && !permissionIds.isEmpty()) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
            role.setPermissions(permissions);
        } else {
            role.setPermissions(new HashSet<>());
        }

        role.setId(null);
        Role savedRole = roleRepository.save(role);
        return savedRole.getId();
    }

    @Override
    @Transactional
    public void update(Role role, Set<String> permissionIds) {
        Role existingRole = roleRepository.findById(role.getId())
                .orElseThrow(() -> new IllegalArgumentException("Rôle non trouvé avec l'ID: " + role.getId()));

        // Protection: SUPER_ADMIN ne peut pas être modifié
        if (Boolean.TRUE.equals(existingRole.getSystemRole()) && "SUPER_ADMIN".equals(existingRole.getNom())) {
            throw new IllegalStateException(
                    "Impossible de modifier le rôle SUPER_ADMIN. " +
                            "Ce rôle système est protégé et ses permissions sont gérées automatiquement.");
        }

        roleRepository.findByNom(role.getNom()).ifPresent(r -> {
            if (!r.getId().equals(role.getId())) {
                throw new IllegalArgumentException("Le nom du rôle '" + role.getNom() + "' est déjà utilisé");
            }
        });

        existingRole.setNom(role.getNom());
        existingRole.setDescription(role.getDescription());

        if (permissionIds != null) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
            existingRole.setPermissions(permissions);
        }

        roleRepository.save(existingRole);
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("Rôle non trouvé avec l'ID: " + id);
        }

        // Vérifier si c'est un rôle système
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rôle non trouvé"));

        if (Boolean.TRUE.equals(role.getSystemRole())) {
            throw new IllegalStateException(
                    "Impossible de supprimer le rôle système '" + role.getNom() + "'. " +
                            "Les rôles système (SUPER_ADMIN, ADMIN) sont protégés et ne peuvent pas être supprimés.");
        }

        // Vérifier aussi par nom pour double protection
        String roleName = role.getNom();
        if ("SUPER_ADMIN".equals(roleName) || "ADMIN".equals(roleName)) {
            throw new IllegalStateException(
                    "Impossible de supprimer le rôle '" + roleName + "'. " +
                            "Ce rôle système est protégé et ne peut pas être supprimé.");
        }

        roleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void assignPermission(String roleId, String permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Rôle non trouvé"));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission non trouvée"));

        role.getPermissions().add(permission);
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public void removePermission(String roleId, String permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Rôle non trouvé"));

        role.getPermissions().removeIf(p -> p.getId().equals(permissionId));
        roleRepository.save(role);
    }

    @Override
    public Role getById(String id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetRoleResponse> getById(GetRoleQuery query) {
        Optional<Role> role = roleRepository.findById(query.getId());
        if (role.isPresent()) {
            return Collections.singletonList(mapToResponse(role.get()));
        }
        return Collections.emptyList();
    }

    @Override
    public List<GetRoleResponse> getAll() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GetRoleResponse mapToResponse(Role role) {
        GetRoleResponse response = new GetRoleResponse();
        response.setId(role.getId());
        response.setName(role.getNom());
        response.setDescription(role.getDescription());
        response.setSystemRole(Boolean.TRUE.equals(role.getSystemRole()));

        // Manually map permissions to avoid circular reference
        if (role.getPermissions() != null) {
            Set<PermissionDTO> permissionDTOs = role.getPermissions().stream()
                    .map(permission -> {
                        PermissionDTO dto = new PermissionDTO();
                        dto.setId(permission.getId());
                        dto.setRessource(permission.getRessource()); // Fixed: use setRessource
                        dto.setAction(permission.getAction());
                        dto.setDescription(permission.getDescription());
                        dto.setNomAffichage(permission.getNomAffichage()); // Fixed: use setNomAffichage
                        dto.setModule(permission.getModule()); // Add module field
                        return dto;
                    })
                    .collect(Collectors.toSet());
            response.setPermissions(permissionDTOs);
        } else {
            response.setPermissions(new HashSet<>());
        }

        return response;
    }
}
