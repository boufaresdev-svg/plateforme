package com.example.BacK.infrastructure.services.g_Projet;

import com.example.BacK.application.g_Projet.Query.charge.GetChargeResponse;
import com.example.BacK.application.interfaces.g_Projet.charge.IChargeRepositoryService;
import com.example.BacK.domain.g_Projet.Charge;
import com.example.BacK.infrastructure.repository.g_Projet.ChargeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChargeRepositoryService implements IChargeRepositoryService {

    private final ChargeRepository chargeRepository;
    private final ModelMapper mapper;

    public ChargeRepositoryService(ChargeRepository chargeRepository, ModelMapper mapper) {
        this.chargeRepository = chargeRepository;
        this.mapper = mapper;
    }

    @Override
    public String add(Charge charge) {
        charge.setId(null); // Assure-toi que l'ID est null pour création
        chargeRepository.save(charge);
        return "ok";
    }

    @Override
    public void update(Charge charge) {
        if (!chargeRepository.existsById(charge.getId())) {
            throw new IllegalArgumentException("Charge ID not found");
        }
        chargeRepository.save(charge);
    }

    @Override
    public void delete(String id) {
        chargeRepository.deleteById(id);
    }

    @Override
    public Charge get(String id) {
        return chargeRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetChargeResponse> getall() {
        List<Charge> charges = chargeRepository.findAll();
        return charges.stream()
                .map(c -> mapper.map(c, GetChargeResponse.class))
                .toList();
    }
}
