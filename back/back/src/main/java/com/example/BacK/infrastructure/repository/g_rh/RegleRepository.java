package com.example.BacK.infrastructure.repository.g_rh;

import com.example.BacK.domain.g_RH.Regle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegleRepository extends JpaRepository<Regle, String> {

}