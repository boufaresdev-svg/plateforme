package com.example.BacK.application.interfaces.g_Utilisateur.role;

import com.example.BacK.application.g_Utilisateur.Query.role.GetRoleQuery;
import com.example.BacK.application.g_Utilisateur.Query.role.GetRoleResponse;
import com.example.BacK.domain.g_Utilisateurs.Role;

import java.util.List;
import java.util.Set;

public interface IRoleRepositoryService {
    String add(Role role, Set<String> permissionIds);
    void update(Role role, Set<String> permissionIds);
    void delete(String id);
    void assignPermission(String roleId, String permissionId);
    void removePermission(String roleId, String permissionId);
    Role getById(String id);
    List<GetRoleResponse> getById(GetRoleQuery query);
    List<GetRoleResponse> getAll();
}
