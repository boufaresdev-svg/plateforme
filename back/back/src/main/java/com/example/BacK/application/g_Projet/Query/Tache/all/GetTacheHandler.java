package com.example.BacK.application.g_Projet.Query.Tache.all;

import com.example.BacK.application.interfaces.g_Projet.tache.ITacheRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

import java.util.List;


@Component("GetTacheHandler")
public class GetTacheHandler implements RequestHandler<GetTacheQuery, List<GetTacheResponse>> {
     private final ITacheRepositoryService tacheRepositoryService;

    public GetTacheHandler(ITacheRepositoryService tacheRepositoryService) {
        this.tacheRepositoryService = tacheRepositoryService;
    }

    @Override
    public List<GetTacheResponse> handle(GetTacheQuery command) {
        return  tacheRepositoryService.getall();
    }
}
