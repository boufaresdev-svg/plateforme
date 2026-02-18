package com.example.BacK.application.g_Formation.Query.Apprenant.CheckEnrollment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckApprenantEnrollmentResponse {
    private Long apprenantId;
    private Long classeId;
    private boolean enrolled;
    private ClasseInfo classe;
    private String error;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClasseInfo {
        private String nom;
        private String code;
        private Boolean isActive;
    }

    // Factory methods for convenience
    public static CheckApprenantEnrollmentResponse notFound(Long apprenantId, Long classeId, String error) {
        CheckApprenantEnrollmentResponse response = new CheckApprenantEnrollmentResponse();
        response.setApprenantId(apprenantId);
        response.setClasseId(classeId);
        response.setEnrolled(false);
        response.setError(error);
        return response;
    }

    public static CheckApprenantEnrollmentResponse notEnrolled(Long apprenantId, Long classeId) {
        CheckApprenantEnrollmentResponse response = new CheckApprenantEnrollmentResponse();
        response.setApprenantId(apprenantId);
        response.setClasseId(classeId);
        response.setEnrolled(false);
        return response;
    }

    public static CheckApprenantEnrollmentResponse enrolled(Long apprenantId, Long classeId, String nom, String code, Boolean isActive) {
        CheckApprenantEnrollmentResponse response = new CheckApprenantEnrollmentResponse();
        response.setApprenantId(apprenantId);
        response.setClasseId(classeId);
        response.setEnrolled(true);
        response.setClasse(new ClasseInfo(nom, code, isActive));
        return response;
    }
}
