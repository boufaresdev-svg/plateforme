package com.example.BacK.application.exceptions;

public class TranchesExceedTotalAmountException extends RuntimeException {
    public TranchesExceedTotalAmountException(String message) {
        super(message);
    }
    
    public TranchesExceedTotalAmountException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static TranchesExceedTotalAmountException withAmounts(Double totalTranches, Float montantTotal) {
        return new TranchesExceedTotalAmountException(
            String.format("Le total des tranches (%.2f) dépasse le montant total de la dette (%.2f)", 
                totalTranches, montantTotal)
        );
    }
}
