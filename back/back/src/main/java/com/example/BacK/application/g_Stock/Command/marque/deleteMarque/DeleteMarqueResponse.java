package com.example.BacK.application.g_Stock.Command.marque.deleteMarque;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMarqueResponse {
    private String id;
    private String message;
    
    public DeleteMarqueResponse(String id) {
        this.id = id;
        this.message = "Marque supprimée avec succès";
    }
}
