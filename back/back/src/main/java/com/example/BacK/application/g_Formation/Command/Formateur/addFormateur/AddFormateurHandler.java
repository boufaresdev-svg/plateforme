package com.example.BacK.application.g_Formation.Command.Formateur.addFormateur;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Formateur;
import com.example.BacK.infrastructure.services.g_Formation.FormateurRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Component("AddFormateurHandler")
public class AddFormateurHandler implements RequestHandler<AddFormateurCommand, AddFormateurResponse> {

    private final FormateurRepositoryService formateurRepositoryService;
    private final ModelMapper modelMapper;

    public AddFormateurHandler(FormateurRepositoryService formateurRepositoryService, ModelMapper modelMapper) {
        this.formateurRepositoryService = formateurRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddFormateurResponse handle(AddFormateurCommand command) {

        if (command.getNom() == null || command.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom du formateur ne peut pas être vide.");
        }
        if (command.getPrenom() == null || command.getPrenom().isBlank()) {
            throw new IllegalArgumentException("Le prénom du formateur ne peut pas être vide.");
        }

        String documentUrl = null;

        if (command.getDocument() != null && !command.getDocument().isEmpty()) {
            try {
                String uploadDir = "uploads/formateurs";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = System.currentTimeMillis() + "_" + command.getDocument().getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);

                Files.copy(command.getDocument().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // ✅ URL complète (backend)
                documentUrl = "http://localhost:8080/uploads/formateurs/" + fileName;

            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de l’enregistrement du document : " + e.getMessage(), e);
            }
        }

        Formateur formateur = modelMapper.map(command, Formateur.class);
        formateur.setDocumentUrl(documentUrl);

        formateur = formateurRepositoryService.saveFormateur(formateur);

        return new AddFormateurResponse(
                formateur.getIdFormateur(),
                formateur.getNom(),
                formateur.getPrenom(),
                formateur.getSpecialite(),
                formateur.getContact(),
                formateur.getExperience(),
                formateur.getDocumentUrl(),
                "✅ Formateur créé avec succès"
        );
    }
}
