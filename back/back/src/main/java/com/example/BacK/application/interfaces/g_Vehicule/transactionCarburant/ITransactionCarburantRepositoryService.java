package com.example.BacK.application.interfaces.g_Vehicule.transactionCarburant;


import com.example.BacK.application.g_Vehicule.Query.TransactionCarburantResponse.GetTransactionCarburantResponse;
import com.example.BacK.domain.g_Vehicule.TransactionCarburant;

import java.util.List;

public interface ITransactionCarburantRepositoryService {
    String add(TransactionCarburant transaction);
    void update(TransactionCarburant transaction);
    void delete(String id);
    TransactionCarburant get(String id);
    List<GetTransactionCarburantResponse> filtre(TransactionCarburant filter);
    List<GetTransactionCarburantResponse> getall( );
}