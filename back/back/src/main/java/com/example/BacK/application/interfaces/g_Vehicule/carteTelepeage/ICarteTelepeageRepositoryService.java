package com.example.BacK.application.interfaces.g_Vehicule.carteTelepeage;

import com.example.BacK.application.g_Vehicule.Query.CarteTelepeage.GetCarteTelepeageResponse;
import com.example.BacK.domain.g_Vehicule.CarteTelepeage;

import java.util.List;

public interface ICarteTelepeageRepositoryService {
    String add(CarteTelepeage carte);
    void update(CarteTelepeage carte);
    void delete(String id);
    CarteTelepeage get(String id);
    List<GetCarteTelepeageResponse> getall( );
    void mise_a_jourSolde(CarteTelepeage carte , double montant);
    void resete_a_jourSolde(CarteTelepeage carte , double montant);
}