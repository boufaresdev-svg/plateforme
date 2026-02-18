package com.example.BacK.application.g_Vehicule.Command.prix_Carburant;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Vehicule.Prix_Carburant;
import com.example.BacK.infrastructure.repository.g_Vehicule.Prix_CarburantRepository;
import org.springframework.stereotype.Component;

@Component("Update_Prix_CarburantHandler")
public class Update_Prix_CarburantHandler implements RequestHandler<UpdatePrixCarburantCommand, Prix_Carburant> {

    private final Prix_CarburantRepository prixCarburantRepository;

    public Update_Prix_CarburantHandler(Prix_CarburantRepository prixCarburantRepository) {
        this.prixCarburantRepository = prixCarburantRepository;
    }

    @Override
    public Prix_Carburant handle(UpdatePrixCarburantCommand command) {
        Prix_Carburant prix = prixCarburantRepository.findById(command.getId())
                .orElseThrow(() -> new RuntimeException("PrixCarburant introuvable"));

        prix.setESSENCE(command.getEssence());
        prix.setGASOIL(command.getGasoil());
        prix.setGASOIL_50(command.getGasoil50());
        prix.setGPL(command.getGpl());

        return prixCarburantRepository.save(prix);
    }
}