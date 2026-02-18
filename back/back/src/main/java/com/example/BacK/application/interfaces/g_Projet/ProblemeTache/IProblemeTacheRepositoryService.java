package com.example.BacK.application.interfaces.g_Projet.ProblemeTache;

import com.example.BacK.application.g_Projet.Query.EmployeAffecte.all.GetEmployeAffecteResponse;
import com.example.BacK.application.g_Projet.Query.ProblemeTache.GetProblemeTacheResponse;
import com.example.BacK.domain.g_Projet.EmployeAffecte;
import com.example.BacK.domain.g_Projet.ProblemeTache;

import java.util.List;

public interface IProblemeTacheRepositoryService {
    String add(ProblemeTache problemeTache );
    void update(ProblemeTache problemeTache );
    void delete(String id);
    GetProblemeTacheResponse get(String id);
    List<GetProblemeTacheResponse> getall( );

}