package com.example.BacK.infrastructure.repository.g_Formation;

import com.example.BacK.domain.g_Formation.ObjectifSpecifique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectifSpecifiqueRepository extends JpaRepository<ObjectifSpecifique, Long> {

    List<ObjectifSpecifique> findByContenuGlobal_IdContenuGlobal(Long idContenu);

    List<ObjectifSpecifique> findByObjectifGlobal_IdObjectifGlobal(Long idObjectifGlobal);

}
