package com.example.BacK.infrastructure.repository.g_Formation;

import com.example.BacK.domain.g_Formation.Certificat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificatRepository extends JpaRepository<Certificat, Long> {

    List<Certificat> findByExamen_IdExamen(Long idExamen);

}
