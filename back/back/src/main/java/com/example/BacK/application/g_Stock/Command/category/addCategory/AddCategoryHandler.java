package com.example.BacK.application.g_Stock.Command.category.addCategory;

import com.example.BacK.application.interfaces.g_Stock.category.ICategoryRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.Category;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddCategoryHandler")
public class AddCategoryHandler implements RequestHandler<AddCategoryCommand, AddCategoryResponse> {

    private final ICategoryRepositoryService categoryRepositoryService;
    private final ModelMapper modelMapper;

    public AddCategoryHandler(ICategoryRepositoryService categoryRepositoryService, ModelMapper modelMapper) {
        this.categoryRepositoryService = categoryRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddCategoryResponse handle(AddCategoryCommand command) {
        Category category = modelMapper.map(command, Category.class);
        String id = this.categoryRepositoryService.add(category);
        return new AddCategoryResponse(id);
    }
}