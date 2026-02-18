package com.example.BacK.application.g_Vehicule.Query.Reparation;


import com.example.BacK.application.interfaces.g_Vehicule.Reparation.IReparationRepositoryService;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Vehicule.Reparation;
import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetReparationHandler")
public class GetReparationHandler implements RequestHandler<GetReparationQuery, List<GetReparationResponse>> {

    private final IReparationRepositoryService reparationRepositoryService;
    private final ModelMapper modelMapper;

    public GetReparationHandler(IReparationRepositoryService reparationRepositoryService, ModelMapper modelMapper) {
        this.reparationRepositoryService = reparationRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GetReparationResponse> handle(GetReparationQuery query) {
        Reparation filter = modelMapper.map(query, Reparation.class);

        List<GetReparationResponse> results = reparationRepositoryService.filtre(filter);
        return results.stream()
                .map(reparation -> modelMapper.map(reparation, GetReparationResponse.class))
                .collect(Collectors.toList());
    }
}