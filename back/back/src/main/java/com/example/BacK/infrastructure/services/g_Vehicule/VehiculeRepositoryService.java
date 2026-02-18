package com.example.BacK.infrastructure.services.g_Vehicule;

import com.example.BacK.application.g_Vehicule.Query.vehicule.GetVehiculeResponse;
import com.example.BacK.application.interfaces.g_Vehicule.vehicule.IVehiculeRepositoryService;
import com.example.BacK.application.models.g_vehicule.ReparationDTO;
import com.example.BacK.application.models.g_vehicule.TransactionCarburantDTO;
import com.example.BacK.domain.g_Vehicule.Vehicule;
import com.example.BacK.infrastructure.repository.g_Vehicule.VehiculeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class VehiculeRepositoryService implements IVehiculeRepositoryService {

    private final ModelMapper _modelMapper;

    private final VehiculeRepository _vehiculeRepository;

    public VehiculeRepositoryService(ModelMapper _modelMapper, VehiculeRepository _vehiculeRepository) {
        this._modelMapper = _modelMapper;
        this._vehiculeRepository = _vehiculeRepository;
    }

    @Override
    public String add(Vehicule vehicule) {
        this._vehiculeRepository.save(vehicule);
        return "ok";
    }

    @Override
    public void update(Vehicule vehicule) {
        if (!this._vehiculeRepository.existsById(vehicule.getId())) {
            throw new IllegalArgumentException("ID Atelier not found");
        }
        this._vehiculeRepository.save(vehicule);

    }

    @Override
    public void delete(String id) {
        this._vehiculeRepository.deleteById(id);
    }

    @Override
    public Vehicule get(String id) {
        return this._vehiculeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicule not found"));
    }

    @Override
    public List<GetVehiculeResponse> getAll( ) {
        List<Vehicule> vehicules = _vehiculeRepository.findAll();

        return vehicules.stream().map(v -> {
            GetVehiculeResponse dto = _modelMapper.map(v, GetVehiculeResponse.class);

            // Mapper les réparations
            List<ReparationDTO> reparationDTOs = v.getReparations().stream()
                    .map(r -> {
                        ReparationDTO rdto = _modelMapper.map(r, ReparationDTO.class);
                        rdto.setVehicleId(v.getId()); // juste l'id
                        return rdto;
                    }).toList();
            dto.setReparations(reparationDTOs);

            // Mapper les transactions
            List<TransactionCarburantDTO> transactionDTOs = v.getTransactions().stream()
                    .map(t -> {
                        TransactionCarburantDTO tdto = _modelMapper.map(t, TransactionCarburantDTO.class);

                        return tdto;
                    }).toList();
            dto.setTransactions(transactionDTOs);

            return dto;
        }).toList();
    }

    @Override
    public void mise_a_jour_km(Vehicule vehicule, double km) {
        vehicule.setKmActuel(km);
        this._vehiculeRepository.save(vehicule);
    }


}