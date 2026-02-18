package com.example.BacK.application.g_Vehicule.Command.carteGazole.addGazoil;



import com.example.BacK.application.interfaces.g_Vehicule.carteGazole.ICarteGazoilRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Vehicule.CarteGazoil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddCarteGazoilHandler")
public class AddCarteGazoilHandler implements RequestHandler<AddCarteGazoilCommand, AddCarteGazoilResponse> {

    private final ICarteGazoilRepositoryService carteGazoilRepositoryService;
    private final ModelMapper modelMapper;

    public AddCarteGazoilHandler(ICarteGazoilRepositoryService carteGazoilRepositoryService, ModelMapper modelMapper) {
        this.carteGazoilRepositoryService = carteGazoilRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddCarteGazoilResponse handle(AddCarteGazoilCommand command) {
        CarteGazoil carteGazoil = modelMapper.map(command, CarteGazoil.class);
        String id = this.carteGazoilRepositoryService.add(carteGazoil);
        return new AddCarteGazoilResponse(id);
    }
}