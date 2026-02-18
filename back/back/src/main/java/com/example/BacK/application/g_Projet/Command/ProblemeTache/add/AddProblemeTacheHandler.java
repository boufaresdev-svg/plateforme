package com.example.BacK.application.g_Projet.Command.ProblemeTache.add;

import com.example.BacK.application.g_Projet.Command.LivrableTache.add.AddLivrableTacheCommand;
import com.example.BacK.application.g_Projet.Command.LivrableTache.add.AddLivrableTacheResponse;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Projet.LivrableTache;
import com.example.BacK.domain.g_Projet.ProblemeTache;
import com.example.BacK.domain.g_Projet.Tache;
import com.example.BacK.infrastructure.services.g_Projet.LivrableTacheRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.ProblemeTacheRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.TacheRepositoryService;
import com.example.BacK.infrastructure.services.g_rh.EmployeeRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component("AddProblemeTacheHandler")
public class AddProblemeTacheHandler implements RequestHandler<AddProblemeTacheCommand, AddProblemeTacheResponse> {

    private final ProblemeTacheRepositoryService problemeTacheRepositoryService;
    private final EmployeeRepositoryService employeeRepositoryService;
    private final TacheRepositoryService tacheRepositoryService;
    private final ModelMapper modelMapper;

    public AddProblemeTacheHandler(ProblemeTacheRepositoryService problemeTacheRepositoryService, EmployeeRepositoryService employeeRepositoryService, TacheRepositoryService tacheRepositoryService, ModelMapper modelMapper) {
        this.problemeTacheRepositoryService = problemeTacheRepositoryService;
        this.employeeRepositoryService = employeeRepositoryService;
        this.tacheRepositoryService = tacheRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddProblemeTacheResponse handle(AddProblemeTacheCommand command) {
        ProblemeTache problemeTache = modelMapper.map(command, ProblemeTache.class);

        // Récupération de la tâche
        Tache tacheFound = tacheRepositoryService.get(command.getTacheId());
        problemeTache.setTache(tacheFound);

        // Ajout du commentaire
        String id = problemeTacheRepositoryService.add(problemeTache);
        return new AddProblemeTacheResponse(id);
    }
}