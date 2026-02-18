package com.example.BacK.application.g_Utilisateur.Query.userStats;

import com.example.BacK.application.interfaces.g_Utilisateur.user.IUserRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("GetUserStatsHandler")
public class GetUserStatsHandler implements RequestHandler<GetUserStatsQuery, GetUserStatsResponse> {

    private final IUserRepositoryService userRepositoryService;

    public GetUserStatsHandler(IUserRepositoryService userRepositoryService) {
        this.userRepositoryService = userRepositoryService;
    }

    @Override
    public GetUserStatsResponse handle(GetUserStatsQuery query) {
        return userRepositoryService.getUserStats();
    }
}
