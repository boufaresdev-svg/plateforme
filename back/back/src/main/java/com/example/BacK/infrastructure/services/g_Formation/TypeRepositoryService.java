package com.example.BacK.infrastructure.services.g_Formation;

import com.example.BacK.application.interfaces.g_Formation.Type.ITypeRepositoryService;
import com.example.BacK.domain.g_Formation.Type;
import com.example.BacK.infrastructure.repository.g_Formation.TypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypeRepositoryService implements ITypeRepositoryService {

    private final TypeRepository typeRepository;

    public TypeRepositoryService(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    @Override
    public Type saveType(Type type) {
        return typeRepository.save(type);
    }

    @Override
    public Type updateType(Long id, Type type) {
        if (!typeRepository.existsById(id)) {
            throw new IllegalArgumentException("Type non trouvé avec l'ID : " + id);
        }
        type.setIdType(id);
        return typeRepository.save(type);
    }

    @Override
    public void deleteType(Long id) {
        if (!typeRepository.existsById(id)) {
            throw new IllegalArgumentException("Type non trouvé avec l'ID : " + id);
        }
        typeRepository.deleteById(id);
    }

    @Override
    public Optional<Type> getTypeById(Long id) {
        return typeRepository.findById(id);
    }

    @Override
    public List<Type> findByDomaine(Long idDomaine) {
        return typeRepository.findByDomaine_IdDomaine(idDomaine);
    }

    @Override
    public List<Type> getAllTypes() {
        return typeRepository.findAll();
    }

    public boolean existsById(Long idType) {
        return typeRepository.existsById(idType);
    }
}

