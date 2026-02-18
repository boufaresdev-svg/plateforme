package com.example.BacK.application.interfaces.g_Utilisateur.user;

import com.example.BacK.application.g_Utilisateur.Query.user.GetUserQuery;
import com.example.BacK.application.g_Utilisateur.Query.user.GetUserResponse;
import com.example.BacK.application.g_Utilisateur.Query.userStats.GetUserStatsResponse;
import com.example.BacK.application.models.PageResponse;
import com.example.BacK.domain.g_Utilisateurs.User;

import java.util.List;
import java.util.Set;

public interface IUserRepositoryService {
    
    // Command operations
    String add(User user, Set<String> roleIds);
    void update(User user, Set<String> roleIds, String newPassword);
    void delete(String id);
    void assignRole(String userId, String roleId);
    void removeRole(String userId, String roleId);
    
    // Query operations
    User getById(String id);
    List<GetUserResponse> getById(GetUserQuery query);
    List<GetUserResponse> search(GetUserQuery query);
    List<GetUserResponse> getAll();
    PageResponse<GetUserResponse> getAllPaginated(GetUserQuery query);
    PageResponse<GetUserResponse> searchPaginated(GetUserQuery query);
    GetUserStatsResponse getUserStats();
}
