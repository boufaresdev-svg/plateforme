package com.example.BacK.application.g_Projet.Query.mission.all;


import com.example.BacK.application.interfaces.g_Projet.mission.IMissionRepositoryService;
 import com.example.BacK.application.mediator.RequestHandler;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;


@Component("GetMissionHandler")
public class GetMissionHandler implements RequestHandler<GetMissionQuery, List<GetMissionResponse>> {
    private  final IMissionRepositoryService _iMissionRepositoryService;
    private final ModelMapper _modelMapper;

    public GetMissionHandler(IMissionRepositoryService _iMissionRepositoryService, ModelMapper _modelMapper) {
        this._iMissionRepositoryService = _iMissionRepositoryService;
        this._modelMapper = _modelMapper;
    }

    @Override
    public List<GetMissionResponse> handle(GetMissionQuery command) {
                    List<GetMissionResponse> l =  _iMissionRepositoryService.getall();
                return l;
    }
}

