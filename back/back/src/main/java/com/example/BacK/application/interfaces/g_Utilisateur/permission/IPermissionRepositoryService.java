package com.example.BacK.application.interfaces.g_Utilisateur.permission;

import com.example.BacK.application.g_Utilisateur.Query.permission.GetPermissionQuery;
import com.example.BacK.application.g_Utilisateur.Query.permission.GetPermissionResponse;
import com.example.BacK.domain.g_Utilisateurs.Permission;

import java.util.List;

public interface IPermissionRepositoryService {
    String add(Permission permission);
    void update(Permission permission);
    void delete(String id);
    Permission getById(String id);
    List<GetPermissionResponse> getById(GetPermissionQuery query);
    List<GetPermissionResponse> getAll();
}
