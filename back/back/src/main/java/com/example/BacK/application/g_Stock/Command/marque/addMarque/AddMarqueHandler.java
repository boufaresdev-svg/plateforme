package com.example.BacK.application.g_Stock.Command.marque.addMarque;

import com.example.BacK.application.interfaces.g_Stock.marque.IMarqueRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.Marque;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddMarqueHandler")
public class AddMarqueHandler implements RequestHandler<AddMarqueCommand, AddMarqueResponse> {

    private final IMarqueRepositoryService marqueRepositoryService;
    private final ModelMapper modelMapper;

    public AddMarqueHandler(IMarqueRepositoryService marqueRepositoryService, ModelMapper modelMapper) {
        this.marqueRepositoryService = marqueRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddMarqueResponse handle(AddMarqueCommand command) {
        Marque marque = modelMapper.map(command, Marque.class);
        Marque savedMarque = marqueRepositoryService.add(marque);
        return new AddMarqueResponse(savedMarque.getId());
    }
}
