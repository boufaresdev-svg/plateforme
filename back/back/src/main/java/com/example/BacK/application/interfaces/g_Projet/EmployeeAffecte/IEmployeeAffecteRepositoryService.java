package com.example.BacK.application.interfaces.g_Projet.EmployeeAffecte;

import com.example.BacK.application.g_Projet.Query.EmployeAffecte.all.GetEmployeAffecteResponse;
import com.example.BacK.domain.g_Projet.EmployeAffecte;

import java.util.List;

public interface IEmployeeAffecteRepositoryService {
    String add(EmployeAffecte employeAffecte );
    void update(EmployeAffecte employeAffecte );
    void delete(String id);
    EmployeAffecte get(String id);
    List<GetEmployeAffecteResponse> getall( );

}