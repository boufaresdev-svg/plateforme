package com.example.BacK.application.g_Vehicule.Command.transactionCarburant.updateTransaction;


 import com.example.BacK.application.interfaces.g_Vehicule.carteGazole.ICarteGazoilRepositoryService;
 import com.example.BacK.application.interfaces.g_Vehicule.carteTelepeage.ICarteTelepeageRepositoryService;
import com.example.BacK.application.interfaces.g_Vehicule.transactionCarburant.ITransactionCarburantRepositoryService;
import com.example.BacK.application.interfaces.g_Vehicule.vehicule.IVehiculeRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Vehicule.CarteGazoil;
import com.example.BacK.domain.g_Vehicule.CarteTelepeage;
import com.example.BacK.domain.g_Vehicule.TransactionCarburant;
import com.example.BacK.domain.g_Vehicule.Vehicule;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdateTransactionCarburantHandler")
public class UpdateTransactionCarburantHandler implements RequestHandler<UpdateTransactionCarburantCommand, Void> {

    private final ITransactionCarburantRepositoryService transactionCarburantRepositoryService;
    private final ModelMapper modelMapper;
    private final IVehiculeRepositoryService vehiculeRepositoryService;
    private final ICarteGazoilRepositoryService carteGazoilRepositoryService;
    private final ICarteTelepeageRepositoryService carteTelepeageRepositoryService;

    public UpdateTransactionCarburantHandler(ITransactionCarburantRepositoryService transactionCarburantRepositoryService, ModelMapper modelMapper, IVehiculeRepositoryService vehiculeRepositoryService, ICarteGazoilRepositoryService carteGazoilRepositoryService, ICarteTelepeageRepositoryService carteTelepeageRepositoryService) {
        this.transactionCarburantRepositoryService = transactionCarburantRepositoryService;
        this.modelMapper = modelMapper;
        this.vehiculeRepositoryService = vehiculeRepositoryService;
        this.carteGazoilRepositoryService = carteGazoilRepositoryService;
        this.carteTelepeageRepositoryService = carteTelepeageRepositoryService;
    }

    @Override
    public Void handle(UpdateTransactionCarburantCommand command) {
        TransactionCarburant existingEntity = this.transactionCarburantRepositoryService.get(command.getId());
        if (existingEntity == null) {
            throw new EntityNotFoundException("Entity TransactionCarburant not found");
        }
        Vehicule foundVehicule = vehiculeRepositoryService.get(command.getVehiculeId());
        CarteGazoil foundCarte = carteGazoilRepositoryService.get(command.getCarteId());
        CarteTelepeage foundTelepeage = carteTelepeageRepositoryService.get(command.getCarteTelepeageId());

        double old_montant_carte = existingEntity.getMontantTotal();
        double old_montant_telepeage = existingEntity.getMontantTelepeage();
        double  montant_carte = command.getMontantTotal() - old_montant_carte;
        double  montant_telepeage = command.getMontantTelepeage() - old_montant_telepeage;

        existingEntity.setStation(command.getStation());
        existingEntity.setQuantite(command.getQuantite());
        existingEntity.setPrixLitre(command.getPrixLitre());
        existingEntity.setMontantTotal(command.getMontantTotal());
        existingEntity.setKilometrage(command.getKilometrage());
        existingEntity.setTypeCarburant(command.getTypeCarburant());
        existingEntity.setConducteur(command.getConducteur());
        existingEntity.setDate(command.getDate());
        existingEntity.setConsommation(command.getConsommation());
        existingEntity.setCarteTelepeageId(command.getCarteTelepeageId());
        existingEntity.setMontantTelepeage(command.getMontantTelepeage());
        existingEntity.setVehicule(foundVehicule);
        existingEntity.setCarte(foundCarte);

        this.transactionCarburantRepositoryService.update(existingEntity);
        vehiculeRepositoryService.mise_a_jour_km(foundVehicule,existingEntity.getKilometrage());
        carteGazoilRepositoryService.mise_a_jourSolde(foundCarte,montant_carte);
        if (foundTelepeage != null) {
            carteTelepeageRepositoryService.mise_a_jourSolde(foundTelepeage , montant_telepeage);
        }

        return null;
    }
}
