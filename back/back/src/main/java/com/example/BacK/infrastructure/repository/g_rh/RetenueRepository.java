package com.example.BacK.infrastructure.repository.g_rh;

 import com.example.BacK.domain.g_RH.Retenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetenueRepository extends JpaRepository<Retenue, String> {

}