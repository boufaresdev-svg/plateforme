package com.example.BacK.application.g_Stock.Command.Article;

import com.example.BacK.application.interfaces.g_Stock.article.IArticleRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteArticleCommandHandler implements RequestHandler<DeleteArticleCommand, DeleteArticleCommandResponse> {
    
    private final IArticleRepositoryService repositoryService;
    
    @Override
    public DeleteArticleCommandResponse handle(DeleteArticleCommand command) {
        if (!repositoryService.existsById(command.getId())) {
            throw new IllegalArgumentException("Article non trouvé avec l'ID: " + command.getId());
        }
        
        repositoryService.deleteById(command.getId());
        return new DeleteArticleCommandResponse(true, "Article supprimé avec succès");
    }
}
