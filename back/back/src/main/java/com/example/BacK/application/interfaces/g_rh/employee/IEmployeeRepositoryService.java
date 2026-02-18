package com.example.BacK.application.interfaces.g_rh.employee;

import com.example.BacK.application.g_RH.Query.employee.GetEmployeeResponse;
import com.example.BacK.application.g_Vehicule.Query.CarteGazoil.GetCarteGazoilResponse;
import com.example.BacK.domain.g_RH.Employee;
import com.example.BacK.domain.g_Vehicule.CarteGazoil;

import java.util.List;

public interface IEmployeeRepositoryService {
    String add(Employee employee);
    void update(Employee employee);
    void delete(String id);
    Employee  get(String id);
    List<GetEmployeeResponse> getall( );

}