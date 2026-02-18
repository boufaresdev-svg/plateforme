package com.example.BacK.application.g_Projet.Query.projet.all;

import com.example.BacK.application.interfaces.g_Projet.projet.IProjetRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("GetProjetHandler")
public class GetProjetHandler implements RequestHandler<GetProjetQuery,List<GetProjetResponse>> {
    private  final IProjetRepositoryService _projetRepositoryService;
    private final ModelMapper _modelMapper;

    public GetProjetHandler(IProjetRepositoryService projetRepositoryService, ModelMapper _modelMapper) {
        this._projetRepositoryService = projetRepositoryService;
        this._modelMapper = _modelMapper;
    }
    @Override
    public List<GetProjetResponse> handle(GetProjetQuery command) {
        return  _projetRepositoryService.getall();
    }
}

