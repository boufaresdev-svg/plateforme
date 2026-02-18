package com.example.BacK.application.g_RH.Command.congee.addCongee;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_RH.Congee;
import com.example.BacK.domain.g_RH.Employee;
import com.example.BacK.infrastructure.services.g_rh.CongeeRepositoryService;
import com.example.BacK.infrastructure.services.g_rh.EmployeeRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component("AddCongeeHandler")
public class AddCongeeHandler implements RequestHandler<AddCongeeCommand, AddCongeeResponse> {

    private final CongeeRepositoryService congeeRepositoryService;
    private final EmployeeRepositoryService employeeRepositoryService ;
    private final ModelMapper modelMapper;

    public AddCongeeHandler(CongeeRepositoryService congeeRepositoryService, EmployeeRepositoryService employeeRepositoryService, ModelMapper modelMapper) {
        this.congeeRepositoryService = congeeRepositoryService;
        this.employeeRepositoryService = employeeRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddCongeeResponse handle(AddCongeeCommand command) {
        Congee congee = modelMapper.map(command, Congee.class);
        Employee employeFound = employeeRepositoryService.get(command.getEmployeeId());
        congee.setEmployee(employeFound);
        String id = this.congeeRepositoryService.add(congee);
        return new AddCongeeResponse(id);
    }
}