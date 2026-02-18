package com.example.BacK.application.interfaces.g_Formation.Type;


import com.example.BacK.domain.g_Formation.Type;

import java.util.List;
import java.util.Optional;

public interface ITypeRepositoryService {

    Type saveType(Type type);
    Type updateType(Long id, Type type);
    void deleteType(Long id);
    Optional<Type> getTypeById(Long id);
    List<Type> findByDomaine(Long idDomaine);
    List<Type> getAllTypes();
}
