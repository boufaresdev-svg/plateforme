package com.example.BacK.application.exceptions;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
/*
    @ExceptionHandler(AutomateException.class)
    public ResponseEntity<List<AutomateReponse>> handleAutomateException(AutomateException ex) {
        AutomateReponse response = new AutomateReponse(AutomateStatus.KO, ex.getMessage(),null);
        return ResponseEntity.ok(Arrays.asList(response));
    }*/
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<List<ReponseException>> handleIllegalArgument(IllegalArgumentException ex) {
        ReponseException response = new ReponseException(ex.getMessage());
        return ResponseEntity.badRequest().body(Arrays.asList(response));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<List<ReponseException>> handleIllegalArgument(EntityNotFoundException ex) {
        ReponseException response = new ReponseException(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Arrays.asList(response));
    }

    @ExceptionHandler(InvalidEntityRelationException.class)
    public ResponseEntity<String> handleInvalidEntityRelationException(InvalidEntityRelationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(FournisseurNameAlreadyExistsException.class)
    public ResponseEntity<List<ReponseException>> handleFournisseurNameAlreadyExists(FournisseurNameAlreadyExistsException ex) {
        ReponseException response = new ReponseException(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Arrays.asList(response));
    }

    @ExceptionHandler(FournisseurNotFoundException.class)
    public ResponseEntity<List<ReponseException>> handleFournisseurNotFound(FournisseurNotFoundException ex) {
        ReponseException response = new ReponseException(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Arrays.asList(response));
    }

    @ExceptionHandler(DettesFournisseurNotFoundException.class)
    public ResponseEntity<List<ReponseException>> handleDettesFournisseurNotFound(DettesFournisseurNotFoundException ex) {
        ReponseException response = new ReponseException(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Arrays.asList(response));
    }

    @ExceptionHandler(DuplicateNumeroFactureException.class)
    public ResponseEntity<List<ReponseException>> handleDuplicateNumeroFacture(DuplicateNumeroFactureException ex) {
        ReponseException response = new ReponseException(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Arrays.asList(response));
    }

    @ExceptionHandler(TranchesExceedTotalAmountException.class)
    public ResponseEntity<List<ReponseException>> handleTranchesExceedTotalAmount(TranchesExceedTotalAmountException ex) {
        ReponseException response = new ReponseException(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Arrays.asList(response));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ReponseException>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ReponseException> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ReponseException(error.getField() + ": " + error.getDefaultMessage()))
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }





}