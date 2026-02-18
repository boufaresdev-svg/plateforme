package com.example.BacK.infrastructure.services.g_Projet;

import com.example.BacK.application.g_Projet.Query.commentaireTache.GetCommentaireTacheResponse;
import com.example.BacK.application.interfaces.g_Projet.CommentaireTache.ICommentaireTacheRepositoryService;
import com.example.BacK.domain.g_Projet.CommentaireTache;
import com.example.BacK.infrastructure.repository.g_Projet.CommentaireTacheRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentaireTacheRepositoryService implements ICommentaireTacheRepositoryService {

    private final CommentaireTacheRepository commentaireTacheRepository;
    private final ModelMapper mapper;

    public CommentaireTacheRepositoryService(CommentaireTacheRepository commentaireTacheRepository, ModelMapper mapper) {
        this.commentaireTacheRepository = commentaireTacheRepository;
        this.mapper = mapper;
    }

    @Override
    public String add(CommentaireTache commentaire) {
        commentaire.setId(null); // ID null pour création
        commentaireTacheRepository.save(commentaire);
        return "ok";
    }

    @Override
    public void update(CommentaireTache commentaire) {
        if (!commentaireTacheRepository.existsById(commentaire.getId())) {
            throw new IllegalArgumentException("CommentaireTache ID not found");
        }
        commentaireTacheRepository.save(commentaire);
    }

    @Override
    public void delete(String id) {
        commentaireTacheRepository.deleteById(id);
    }

    @Override
    public CommentaireTache get(String id) {
        return commentaireTacheRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetCommentaireTacheResponse> getall() {
        List<CommentaireTache> commentaires = commentaireTacheRepository.findAll();
        return commentaires.stream()
                .map(c -> mapper.map(c, GetCommentaireTacheResponse.class))
                .toList();
    }
}
