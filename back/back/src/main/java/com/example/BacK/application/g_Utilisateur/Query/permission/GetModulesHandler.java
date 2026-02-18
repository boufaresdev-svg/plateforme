package com.example.BacK.application.g_Utilisateur.Query.permission;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.application.models.g_Utilisateur.ModuleDTO;
import com.example.BacK.domain.g_Utilisateurs.Module;
import com.example.BacK.infrastructure.repository.g_Utilisateur.PermissionRepository;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("GetModulesHandler")
public class GetModulesHandler implements RequestHandler<GetModulesQuery, List<ModuleDTO>> {

    private final PermissionRepository permissionRepository;

    public GetModulesHandler(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<ModuleDTO> handle(GetModulesQuery query) {
        Map<Module, Long> permissionCounts = permissionRepository.findAll().stream()
                .filter(p -> p.getModule() != null)
                .collect(Collectors.groupingBy(
                        com.example.BacK.domain.g_Utilisateurs.Permission::getModule,
                        Collectors.counting()
                ));
        return Arrays.stream(Module.values())
                .map(module -> {
                    ModuleDTO dto = new ModuleDTO(module);
                    dto.setPermissionCount(permissionCounts.getOrDefault(module, 0L).intValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
