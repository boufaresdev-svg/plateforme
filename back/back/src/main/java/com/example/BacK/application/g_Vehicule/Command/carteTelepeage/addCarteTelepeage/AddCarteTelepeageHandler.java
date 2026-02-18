package com.example.BacK.application.g_Vehicule.Command.carteTelepeage.addCarteTelepeage;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Vehicule.CarteTelepeage;
import com.example.BacK.infrastructure.services.g_Vehicule.CarteTelepeageRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("addCarteTelepeageHandler")
public class AddCarteTelepeageHandler implements RequestHandler<AddCarteTelepeageCommand, AddCarteTelepeageResponse> {

    private final ModelMapper modelMapper;
    private final CarteTelepeageRepositoryService carteTelepeageRepositoryService;

    public AddCarteTelepeageHandler(ModelMapper modelMapper, CarteTelepeageRepositoryService carteTelepeageRepositoryService) {
        this.modelMapper = modelMapper;
        this.carteTelepeageRepositoryService = carteTelepeageRepositoryService;
    }

    @Override
    public AddCarteTelepeageResponse handle(AddCarteTelepeageCommand command) {
        CarteTelepeage carteTelepeage = modelMapper.map(command, CarteTelepeage.class);
        String id = this.carteTelepeageRepositoryService.add(carteTelepeage);
        return new AddCarteTelepeageResponse(id);
    }
}