package com.example.BacK.presentation.g_Stock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = "com.example.BacK.presentation.g_Stock")
public class StockGlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        
        String message = ex.getMessage();
        // Clean up validation error messages
        if (message != null && message.startsWith("Validation errors:")) {
            message = message.replace("Validation errors:", "").trim();
        }
        
        errorResponse.put("message", message != null ? message : "Erreur de validation");
        errorResponse.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(IllegalStateException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        
        String message = ex.getMessage();
        if (message != null) {
            // Translate common error messages to French
            if (message.contains("Stock not found")) {
                message = "Stock introuvable pour cet article dans cet entrepôt";
            } else if (message.contains("Article not found")) {
                message = "Article introuvable";
            } else if (message.contains("Entrepot not found") || message.contains("Warehouse not found")) {
                message = "Entrepôt introuvable";
            } else if (message.contains("Category not found")) {
                message = "Catégorie introuvable";
            } else if (message.contains("Marque not found") || message.contains("Brand not found")) {
                message = "Marque introuvable";
            } else if (message.contains("Fournisseur not found") || message.contains("Supplier not found")) {
                message = "Fournisseur introuvable";
            } else if (message.contains("User not found")) {
                message = "Utilisateur introuvable";
            } else if (message.contains("Insufficient stock")) {
                message = "Stock insuffisant pour cette opération";
            } else if (message.contains("Invalid quantity")) {
                message = "Quantité invalide";
            } else if (message.contains("Duplicate")) {
                message = "Cet élément existe déjà";
            } else if (message.contains("cannot be deleted")) {
                message = "Impossible de supprimer cet élément car il est utilisé";
            }
        } else {
            message = "Erreur lors du traitement de la demande";
        }
        
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    String field = translateFieldName(error.getField());
                    String defaultMessage = error.getDefaultMessage();
                    
                    // Translate common validation messages to French
                    if (defaultMessage != null) {
                        if (defaultMessage.contains("must not be null")) {
                            return field + " est obligatoire";
                        } else if (defaultMessage.contains("must not be blank") || defaultMessage.contains("must not be empty")) {
                            return field + " ne peut pas être vide";
                        } else if (defaultMessage.contains("must be greater than")) {
                            return field + " doit être supérieur à zéro";
                        } else if (defaultMessage.contains("must be positive")) {
                            return field + " doit être positif";
                        } else if (defaultMessage.contains("size must be between")) {
                            return field + " doit avoir une taille valide";
                        } else if (defaultMessage.contains("must match")) {
                            return field + " n'est pas au bon format";
                        }
                    }
                    
                    return field + ": " + (defaultMessage != null ? defaultMessage : "invalide");
                })
                .collect(Collectors.joining(", "));
        
        errorResponse.put("message", "Erreur de validation: " + errors);
        errorResponse.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointerException(NullPointerException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "Données manquantes: Veuillez vérifier que tous les champs requis sont remplis");
        errorResponse.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        
        String message = ex.getMessage();
        
        // Translate common error patterns to French
        if (message != null) {
            // Hibernate optimistic locking / concurrency errors
            if (message.contains("Row was updated or deleted by another transaction") || 
                message.contains("OptimisticLockException") ||
                message.contains("StaleStateException")) {
                message = "Les données ont été modifiées par un autre utilisateur. Veuillez actualiser et réessayer";
            }
            // Database constraint violations
            else if (message.contains("ConstraintViolationException") || 
                     message.contains("constraint violation")) {
                message = "Cette opération viole une contrainte de la base de données";
            }
            // Duplicate key errors
            else if (message.contains("duplicate key") || message.contains("Duplicate entry")) {
                message = "Cette valeur existe déjà dans le système";
            }
            // Foreign key constraint
            else if (message.contains("foreign key constraint") || 
                     message.contains("violates foreign key")) {
                message = "Impossible de supprimer: cet élément est référencé par d'autres données";
            }
            // Connection errors
            else if (message.contains("Connection refused") || 
                     message.contains("Unable to connect")) {
                message = "Impossible de se connecter à la base de données";
            }
            // Timeout errors
            else if (message.contains("timeout") || message.contains("Timeout")) {
                message = "L'opération a pris trop de temps. Veuillez réessayer";
            }
            // Generic translation for other errors
            else if (message.toLowerCase().contains("error") || 
                     message.toLowerCase().contains("exception")) {
                // Keep the original message but add context
                message = "Erreur: " + message;
            }
            
            errorResponse.put("message", message);
        } else {
            errorResponse.put("message", "Une erreur inattendue s'est produite");
        }
        
        errorResponse.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Translate common field names to French
     */
    private String translateFieldName(String fieldName) {
        switch (fieldName) {
            case "articleId": return "Article";
            case "entrepotId": return "Entrepôt";
            case "sourceEntrepotId": return "Entrepôt source";
            case "destinationEntrepotId": return "Entrepôt destination";
            case "quantite": return "Quantité";
            case "prixUnitaire": return "Prix unitaire";
            case "prixVente": return "Prix de vente";
            case "prixAchat": return "Prix d'achat";
            case "nom": return "Nom";
            case "description": return "Description";
            case "categorieId": return "Catégorie";
            case "marqueId": return "Marque";
            case "fournisseurId": return "Fournisseur";
            case "reference": return "Référence";
            case "motif": return "Motif";
            case "raison": return "Raison";
            case "dateMouvement": return "Date du mouvement";
            case "dateDexpiration": return "Date d'expiration";
            default: return fieldName;
        }
    }
}
