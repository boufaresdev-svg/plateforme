package com.example.BacK.application.interfaces.g_Projet.CommentaireTache;

import com.example.BacK.application.g_Projet.Query.commentaireTache.GetCommentaireTacheResponse;
import com.example.BacK.domain.g_Projet.CommentaireTache;

import java.util.List;

public interface ICommentaireTacheRepositoryService {
    String add(CommentaireTache commentaireTache  );
    void update(CommentaireTache commentaireTache );
    void delete(String id);
    CommentaireTache get(String id);
    List<GetCommentaireTacheResponse> getall( );

}