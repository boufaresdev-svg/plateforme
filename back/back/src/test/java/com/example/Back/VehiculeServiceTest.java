package com.example.Back;

import com.example.BacK.domain.g_Vehicule.Vehicule;
import com.example.BacK.infrastructure.repository.g_Vehicule.VehiculeRepository;
import com.example.BacK.infrastructure.services.g_Vehicule.VehiculeRepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VehiculeServiceTest {
    @Mock
    private VehiculeRepository vehiculeRepository;

    @InjectMocks
    private VehiculeRepositoryService vehiculeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetVehiculeById_Found() {
        Vehicule v = new Vehicule();
        v.setId("1");
        v.setMarque("Toyota");

        // Mock de la bonne méthode
        when(vehiculeRepository.findById("1")).thenReturn(Optional.of(v));

        Vehicule result = vehiculeService.get("1");

        assertNotNull(result);
        assertEquals("Toyota", result.getMarque());
    }

    @Test
    void testGetVehiculeById_NotFound() {
        when(vehiculeRepository.findById("1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> vehiculeService.get("1"));

        assertEquals("Vehicule not found", exception.getMessage());
    }

    @Test
    void testSaveVehicule() {
        Vehicule v = new Vehicule();
        v.setMarque("Honda");

        // Le mock retournera 'v' pour n'importe quel Vehicule passé à save
        when(vehiculeRepository.save(any(Vehicule.class))).thenReturn(v);

        // Appel de la méthode du service
        String id = vehiculeService.add(v);

        // Mock pour get
        when(vehiculeRepository.findById(id)).thenReturn(Optional.of(v));

        Vehicule result = vehiculeService.get(id);

        assertNotNull(result); // Toujours vérifier que le résultat n'est pas null
        assertEquals("Honda", result.getMarque());

        verify(vehiculeRepository, times(1)).save(any(Vehicule.class));
        verify(vehiculeRepository, times(1)).findById(id);
    }

    @Test
    void testDeleteVehicule() {
        doNothing().when(vehiculeRepository).deleteById("1");
        vehiculeService.delete("1");
        verify(vehiculeRepository, times(1)).deleteById("1");
    }
}
