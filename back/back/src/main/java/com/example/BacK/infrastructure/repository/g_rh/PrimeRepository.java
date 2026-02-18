package com.example.BacK.infrastructure.repository.g_rh;

import com.example.BacK.domain.g_RH.Prime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PrimeRepository extends JpaRepository<Prime, String> {

}