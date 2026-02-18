package com.example.BacK.application.g_Formation.Query.Apprenant.CheckEnrollment;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Classe;
import com.example.BacK.infrastructure.services.g_Formation.ApprenantRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.ClasseRepositoryService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("CheckApprenantEnrollmentHandler")
public class CheckApprenantEnrollmentHandler implements RequestHandler<CheckApprenantEnrollmentQuery, CheckApprenantEnrollmentResponse> {

    private final ApprenantRepositoryService apprenantRepositoryService;
    private final ClasseRepositoryService classeRepositoryService;

    public CheckApprenantEnrollmentHandler(ApprenantRepositoryService apprenantRepositoryService,
                                           ClasseRepositoryService classeRepositoryService) {
        this.apprenantRepositoryService = apprenantRepositoryService;
        this.classeRepositoryService = classeRepositoryService;
    }

    @Override
    public CheckApprenantEnrollmentResponse handle(CheckApprenantEnrollmentQuery query) {
        Long apprenantId = query.getApprenantId();
        Long classeId = query.getClasseId();

        // Check if apprenant exists
        if (!apprenantRepositoryService.existsById(apprenantId)) {
            return CheckApprenantEnrollmentResponse.notFound(
                    apprenantId, 
                    classeId, 
                    "Apprenant non trouvé avec l'ID: " + apprenantId
            );
        }

        // Get classes for this apprenant
        List<Classe> classes = classeRepositoryService.getClassesByApprenant(apprenantId);
        
        // Check if enrolled in the specific class
        Optional<Classe> enrolledClasse = classes.stream()
                .filter(c -> c.getId().equals(classeId))
                .findFirst();

        if (enrolledClasse.isPresent()) {
            Classe classe = enrolledClasse.get();
            return CheckApprenantEnrollmentResponse.enrolled(
                    apprenantId,
                    classeId,
                    classe.getNom(),
                    classe.getCode(),
                    classe.getIsActive()
            );
        }

        return CheckApprenantEnrollmentResponse.notEnrolled(apprenantId, classeId);
    }
}
