package com.example.BacK.infrastructure.repository.g_Formation;

import com.example.BacK.domain.g_Formation.ContenuGlobal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ContenuGlobalRepository extends JpaRepository<ContenuGlobal, Long> {

    List<ContenuGlobal> findByFormation_IdFormation(Long idFormation);


}
