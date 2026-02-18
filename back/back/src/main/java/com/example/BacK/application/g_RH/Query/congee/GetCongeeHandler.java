package com.example.BacK.application.g_RH.Query.congee;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_rh.CongeeRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
@Component("GetCongeeHandler")
public class GetCongeeHandler implements RequestHandler<GetCongeeQuery, List<GetCongeeResponse>> {

    private final CongeeRepositoryService  congeeRepositoryService;
    private final ModelMapper modelMapper;

    public GetCongeeHandler(CongeeRepositoryService congeeRepositoryService, ModelMapper modelMapper) {
        this.congeeRepositoryService = congeeRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GetCongeeResponse> handle(GetCongeeQuery query) {
         return congeeRepositoryService.getall( );
    }
}
