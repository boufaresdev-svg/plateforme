package com.example.BacK.application.g_Projet.Query.phase.all;

import com.example.BacK.application.interfaces.g_Projet.phase.IPhaseRespositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;


@Component("GetPhaseHandler")
public class GetPhaseHandler implements RequestHandler<GetPhaseQuery, List<GetPhaseResponse>> {
    private  final IPhaseRespositoryService  _phaseRespositoryService;
    private final ModelMapper _modelMapper;

    public GetPhaseHandler(IPhaseRespositoryService _phaseRespositoryService, ModelMapper _modelMapper) {
        this._phaseRespositoryService = _phaseRespositoryService;
        this._modelMapper = _modelMapper;
    }

    @Override
    public List<GetPhaseResponse> handle(GetPhaseQuery query) {
        return  _phaseRespositoryService.getall();
    }
}