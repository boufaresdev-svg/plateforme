package com.example.BacK.application.g_Vehicule.Command.carteGazole.deleteGazoil;

  import com.example.BacK.application.interfaces.g_Vehicule.carteGazole.ICarteGazoilRepositoryService;
 import com.example.BacK.application.mediator.RequestHandler;
 import org.springframework.stereotype.Component;


@Component("DeleteCarteGazoilHandler")
public class DeleteCarteGazoilHandler implements RequestHandler<DeleteCarteGazoilCommand, Void> {

    private final ICarteGazoilRepositoryService carteGazoilRepositoryService;

    public DeleteCarteGazoilHandler(ICarteGazoilRepositoryService carteGazoilRepositoryService) {
        this.carteGazoilRepositoryService = carteGazoilRepositoryService;
    }

    @Override
    public Void handle(DeleteCarteGazoilCommand command) {
        this.carteGazoilRepositoryService.delete(command.getId());
        return null;
    }
}