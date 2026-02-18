package com.example.BacK.application.g_Projet.Command.EmployeAffecte.add;

import com.example.BacK.application.interfaces.g_Projet.projet.IProjetRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Projet.EmployeAffecte;
import com.example.BacK.domain.g_Projet.Mission;
import com.example.BacK.domain.g_Projet.Projet;
import com.example.BacK.domain.g_Projet.Tache;
import com.example.BacK.domain.g_RH.Employee;
import com.example.BacK.infrastructure.services.g_Projet.EmployeeAffectRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.MissionRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.ProjectRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.TacheRepositoryService;
import com.example.BacK.infrastructure.services.g_rh.EmployeeRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddEmployeeAffecteHandler")
public class AddEmployeeAffecteHandler implements RequestHandler<AddEmployeeAffecteCommand, AddEmployeeAffecteResponse> {

    private final EmployeeAffectRepositoryService employeeAffectRepositoryService;
    private final MissionRepositoryService missionRepositoryService;
    private final TacheRepositoryService tacheRepositoryService;
    private final EmployeeRepositoryService employeeRepositoryService;
     private final ModelMapper modelMapper;

    public AddEmployeeAffecteHandler(EmployeeAffectRepositoryService employeeAffectRepositoryService, MissionRepositoryService missionRepositoryService, TacheRepositoryService tacheRepositoryService, EmployeeRepositoryService employeeRepositoryService, ModelMapper modelMapper) {
        this.employeeAffectRepositoryService = employeeAffectRepositoryService;
        this.missionRepositoryService = missionRepositoryService;
        this.tacheRepositoryService = tacheRepositoryService;
        this.employeeRepositoryService = employeeRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddEmployeeAffecteResponse handle(AddEmployeeAffecteCommand command) {

        // Créer manuellement l'objet EmployeAffecte
        EmployeAffecte employeeAffecte = new EmployeAffecte();
        employeeAffecte.setRole(command.getRole());
        employeeAffecte.setDateAffectation(command.getDateAffectation());
        employeeAffecte.setTauxHoraire(command.getTauxHoraire());
        employeeAffecte.setHeuresAllouees(command.getHeuresAllouees());
        employeeAffecte.setHeuresRealisees(command.getHeuresRealisees());
        employeeAffecte.setStatut(command.getStatut());

        // Récupérer la mission
        Mission missionFound = missionRepositoryService.get(command.getMissionId());
        if (missionFound == null) {
            throw new RuntimeException("Mission not found with ID: " + command.getMissionId());
        }
        employeeAffecte.setMission(missionFound);

        // Récupérer la tâche (optionnelle)
        if (command.getTacheId() != null) {
            Tache tacheFound = tacheRepositoryService.get(command.getTacheId());
            if (tacheFound == null) {
                throw new RuntimeException("Tache not found with ID: " + command.getTacheId());
            }
            employeeAffecte.setTache(tacheFound);
        }

        // Récupérer l'employé
        Employee employeFound = employeeRepositoryService.get(command.getEmployeId());
        if (employeFound == null) {
            throw new RuntimeException("Employé not found with ID: " + command.getEmployeId());
        }
        employeeAffecte.setEmployee(employeFound);

        // Sauvegarde
        String id = employeeAffectRepositoryService.add(employeeAffecte);

        return new AddEmployeeAffecteResponse(id);
    }

}
