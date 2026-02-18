package com.example.BacK.infrastructure.services.g_Vehicule;

import com.example.BacK.application.g_Vehicule.Query.CarteGazoil.GetCarteGazoilResponse;
import com.example.BacK.application.interfaces.g_Vehicule.carteGazole.ICarteGazoilRepositoryService;
import com.example.BacK.application.models.g_vehicule.TransactionCarburantDTO;
import com.example.BacK.domain.g_Vehicule.CarteGazoil;
import com.example.BacK.infrastructure.repository.g_Vehicule.CarteGazoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CarteGazoilRepositoryService implements ICarteGazoilRepositoryService {
    private final ModelMapper mapper;
    private final CarteGazoleRepository _carteGazoleRepository ;

    public CarteGazoilRepositoryService(ModelMapper mapper, CarteGazoleRepository _carteGazoleRepository) {
        this.mapper = mapper;
        this._carteGazoleRepository = _carteGazoleRepository;
    }

    @Override
    public String add(CarteGazoil carte) {
        carte.setId(null);
        _carteGazoleRepository.save(carte);
        return "ok";
    }

    @Override
    public void update(CarteGazoil carte) {
        if (!_carteGazoleRepository.existsById(carte.getId())) {
            throw new IllegalArgumentException("Carte ID not found");
        }
        _carteGazoleRepository.save(carte);
    }

    @Override
    public void delete(String id) {
        _carteGazoleRepository.deleteById(id);
    }

    @Override
    public CarteGazoil get(String id) {
        return _carteGazoleRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetCarteGazoilResponse> getall( ) {
        List<CarteGazoil> cartes = _carteGazoleRepository.findAll();

        return cartes.stream().map(c -> {
            GetCarteGazoilResponse dto = mapper.map(c, GetCarteGazoilResponse.class);

            // Mapper les transactions
            List<TransactionCarburantDTO> transactionDTOs = c.getTransactions().stream()
                    .map(t -> {
                        TransactionCarburantDTO tdto = mapper.map(t, TransactionCarburantDTO.class);
                       // tdto.setCarteId(c.getId()); // juste l'id de la carte
                        return tdto;
                    }).toList();
            dto.setTransactions(transactionDTOs);

            return dto;
        }).toList();
    }

    @Override
    public void mise_a_jourSolde(CarteGazoil carteGazoil, double montant) {
        carteGazoil.setSolde(carteGazoil.getSolde() - montant);
        carteGazoil.setConsomation(carteGazoil.getConsomation()+montant);
        _carteGazoleRepository.save(carteGazoil);
    }

    @Override
    public void resete_a_jourSolde(CarteGazoil carteGazoil, double montant) {
        carteGazoil.setSolde(carteGazoil.getSolde() + montant);
        carteGazoil.setConsomation(carteGazoil.getConsomation()-montant);
        _carteGazoleRepository.save(carteGazoil);
    }


}