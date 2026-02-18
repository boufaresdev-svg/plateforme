package com.example.BacK.application.g_RH.Query.employee;

import com.example.BacK.application.g_RH.Query.congee.GetCongeeQuery;
import com.example.BacK.application.g_RH.Query.congee.GetCongeeResponse;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_rh.CongeeRepositoryService;
import com.example.BacK.infrastructure.services.g_rh.EmployeeRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;


@Component("GetEmployeeHandler")
public class GetEmployeeHandler implements RequestHandler<GetEmployeeQuery, List<GetEmployeeResponse>> {

    private final EmployeeRepositoryService employeeRepositoryService;

    public GetEmployeeHandler(EmployeeRepositoryService employeeRepositoryService) {
        this.employeeRepositoryService = employeeRepositoryService;
    }

    @Override
    public List<GetEmployeeResponse> handle(GetEmployeeQuery query) {
        return employeeRepositoryService.getall();
    }
}