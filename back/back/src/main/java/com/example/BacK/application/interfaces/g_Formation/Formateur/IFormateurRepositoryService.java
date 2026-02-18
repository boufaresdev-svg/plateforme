package com.example.BacK.application.interfaces.g_Formation.Formateur;

import com.example.BacK.domain.g_Formation.Formateur;

import java.util.List;
import java.util.Optional;

public interface IFormateurRepositoryService {

    Formateur saveFormateur(Formateur formateur);
    Formateur updateFormateur(Long id, Formateur formateur);
    void deleteFormateur(Long id);
    Optional<Formateur> getFormateurById(Long id);
    List<Formateur> getAllFormateurs();

}
