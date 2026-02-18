package com.example.BacK.infrastructure.services.g_Vehicule;

import com.example.BacK.application.g_Vehicule.Query.CarteTelepeage.GetCarteTelepeageResponse;
import com.example.BacK.application.interfaces.g_Vehicule.carteTelepeage.ICarteTelepeageRepositoryService;
import com.example.BacK.application.models.g_vehicule.TransactionTelepeageDTO;
import com.example.BacK.domain.g_Vehicule.CarteTelepeage;
import com.example.BacK.infrastructure.repository.g_Vehicule.CarteTelepeageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CarteTelepeageRepositoryService implements ICarteTelepeageRepositoryService {
    private final ModelMapper mapper;
    private final CarteTelepeageRepository carteTelepeageRepository;

    public CarteTelepeageRepositoryService(ModelMapper mapper, CarteTelepeageRepository carteTelepeageRepository) {
        this.mapper = mapper;
        this.carteTelepeageRepository = carteTelepeageRepository;
    }

    @Override
    public String add(CarteTelepeage carte) {
        carte.setId(null);
        carteTelepeageRepository.save(carte);
        return "ok";
    }


    @Override
    public void update(CarteTelepeage carte) {
        if (!carteTelepeageRepository.existsById(carte.getId())) {
            throw new IllegalArgumentException("Carte ID not found");
        }
        carteTelepeageRepository.save(carte);
    }

    @Override
    public void delete(String id) {
        carteTelepeageRepository.deleteById(id);
    }


    @Override
    public CarteTelepeage get(String id) {
        return carteTelepeageRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetCarteTelepeageResponse> getall() {
        List<CarteTelepeage> cartes = carteTelepeageRepository.findAll();

        return cartes.stream().map(c -> {
            // Mapper la carte
            GetCarteTelepeageResponse dto = mapper.map(c, GetCarteTelepeageResponse.class);
            return dto;
        }).toList();
    }


    @Override
    public void mise_a_jourSolde(CarteTelepeage carte, double montant) {
        carte.setSolde(carte.getSolde() - montant);
        carte.setConsomation(carte.getConsomation() + montant);
        carteTelepeageRepository.save(carte);
    }

    @Override
    public void resete_a_jourSolde(CarteTelepeage carte, double montant) {
        carte.setSolde(carte.getSolde() + montant);
        carte.setConsomation(carte.getConsomation() - montant);
        carteTelepeageRepository.save(carte);
    }
}