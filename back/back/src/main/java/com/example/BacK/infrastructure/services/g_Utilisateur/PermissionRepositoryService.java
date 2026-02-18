package com.example.BacK.infrastructure.services.g_Utilisateur;

import com.example.BacK.application.g_Utilisateur.Query.permission.GetPermissionQuery;
import com.example.BacK.application.g_Utilisateur.Query.permission.GetPermissionResponse;
import com.example.BacK.application.interfaces.g_Utilisateur.permission.IPermissionRepositoryService;
import com.example.BacK.domain.g_Utilisateurs.Permission;
import com.example.BacK.infrastructure.repository.g_Utilisateur.PermissionRepository;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PermissionRepositoryService implements IPermissionRepositoryService {

    private final PermissionRepository permissionRepository;
    private final ModelMapper modelMapper;

    public PermissionRepositoryService(PermissionRepository permissionRepository, ModelMapper modelMapper) {
        this.permissionRepository = permissionRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public String add(Permission permission) {
        if (permission.getRessource() != null && permission.getAction() != null) {
            permissionRepository.findByRessourceAndAction(permission.getRessource(), permission.getAction())
                .ifPresent(p -> {
                    throw new IllegalArgumentException("La permission pour '" + permission.getRessource() + ":" + permission.getAction() + "' existe déjà");
                });
        }
        
        permission.setId(null);
        Permission savedPermission = permissionRepository.save(permission);
        return savedPermission.getId();
    }

    @Override
    @Transactional
    public void update(Permission permission) {
        Permission existingPermission = permissionRepository.findById(permission.getId())
                .orElseThrow(() -> new IllegalArgumentException("Permission non trouvée avec l'ID: " + permission.getId()));
        
        if (permission.getRessource() != null && permission.getAction() != null) {
            permissionRepository.findByRessourceAndAction(permission.getRessource(), permission.getAction())
                .ifPresent(p -> {
                    if (!p.getId().equals(permission.getId())) {
                        throw new IllegalArgumentException("La permission pour '" + permission.getRessource() + ":" + permission.getAction() + "' est déjà utilisée");
                    }
                });
        }
        
        existingPermission.setRessource(permission.getRessource());
        existingPermission.setAction(permission.getAction());
        existingPermission.setDescription(permission.getDescription());
        existingPermission.setNomAffichage(permission.getNomAffichage());
        
        permissionRepository.save(existingPermission);
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!permissionRepository.existsById(id)) {
            throw new IllegalArgumentException("Permission non trouvée avec l'ID: " + id);
        }
        permissionRepository.deleteById(id);
    }

    @Override
    public Permission getById(String id) {
        return permissionRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetPermissionResponse> getById(GetPermissionQuery query) {
        Optional<Permission> permission = permissionRepository.findById(query.getId());
        if (permission.isPresent()) {
            return Collections.singletonList(mapToResponse(permission.get()));
        }
        return Collections.emptyList();
    }

    @Override
    public List<GetPermissionResponse> getAll() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GetPermissionResponse mapToResponse(Permission permission) {
        GetPermissionResponse response = new GetPermissionResponse();
        response.setId(permission.getId());
        response.setResource(permission.getRessource());
        response.setAction(permission.getAction());
        response.setDescription(permission.getDescription());
        response.setDisplayName(permission.getNomAffichage());
        response.setModule(permission.getModule());
        return response;
    }
}
