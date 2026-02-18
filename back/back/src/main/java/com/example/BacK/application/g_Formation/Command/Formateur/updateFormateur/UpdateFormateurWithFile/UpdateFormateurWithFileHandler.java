package com.example.BacK.application.g_Formation.Command.Formateur.updateFormateur.UpdateFormateurWithFile;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Formateur;
import com.example.BacK.infrastructure.services.g_Formation.FormateurRepositoryService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component("UpdateFormateurWithFileHandler")
public class UpdateFormateurWithFileHandler implements RequestHandler<UpdateFormateurWithFileCommand, Void> {

    private final FormateurRepositoryService formateurRepositoryService;

    public UpdateFormateurWithFileHandler(FormateurRepositoryService formateurRepositoryService) {
        this.formateurRepositoryService = formateurRepositoryService;
    }

    @Override
    public Void handle(UpdateFormateurWithFileCommand command) {

        if (command.getIdFormateur() == null) {
            throw new IllegalArgumentException("L'ID du formateur est obligatoire.");
        }

        Formateur formateur = formateurRepositoryService.getFormateurById(command.getIdFormateur())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Formateur introuvable avec l’ID : " + command.getIdFormateur()
                ));

        // Update data
        formateur.setNom(command.getNom());
        formateur.setPrenom(command.getPrenom());
        formateur.setSpecialite(command.getSpecialite());
        formateur.setContact(command.getContact());
        formateur.setExperience(command.getExperience());

        MultipartFile newFile = command.getFile();

        if (newFile != null && !newFile.isEmpty()) {
            try {
                String uploadDir = "backend/uploads/formateurs";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                if (formateur.getDocumentUrl() != null) {
                    deleteOldFile(formateur.getDocumentUrl());
                }

                String fileName = System.currentTimeMillis() + "_" + newFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);

                Files.copy(newFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                String newUrl = "/uploads/formateurs/" + fileName;

                formateur.setDocumentUrl(newUrl);

            } catch (IOException e) {
                throw new RuntimeException("Erreur upload fichier : " + e.getMessage(), e);
            }
        }

        formateurRepositoryService.saveFormateur(formateur);
        return null;
    }

    private void deleteOldFile(String documentUrl) {
        try {
            if (documentUrl == null || documentUrl.trim().isEmpty()) {
                return;
            }

            String fileName = documentUrl.substring(documentUrl.lastIndexOf("/") + 1);

            Path filePath = Paths.get("backend/uploads/formateurs/" + fileName);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

        } catch (Exception ex) {
        }
    }


}
