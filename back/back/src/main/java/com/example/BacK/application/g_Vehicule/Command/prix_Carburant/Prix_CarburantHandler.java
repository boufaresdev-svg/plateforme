package com.example.BacK.application.g_Vehicule.Command.prix_Carburant;


import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Vehicule.Prix_Carburant;
import com.example.BacK.infrastructure.repository.g_Vehicule.Prix_CarburantRepository;
import org.springframework.stereotype.Component;

@Component("Prix_CarburantHandler")
public class Prix_CarburantHandler implements RequestHandler<Prix_Carburant, Prix_Carburant> {

    private final Prix_CarburantRepository prix_CarburantRepository;

    public Prix_CarburantHandler(Prix_CarburantRepository prix_CarburantRepository) {
        this.prix_CarburantRepository = prix_CarburantRepository;
    }

    @Override
    public Prix_Carburant handle(Prix_Carburant command) {

        if(prix_CarburantRepository.findAll().isEmpty()) {
            return prix_CarburantRepository.save(command);
        }
        else{
            return prix_CarburantRepository.findAll().get(0);
        }

    }
}