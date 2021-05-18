package com.ar.mangerspital.repository;

import com.ar.mangerspital.domain.Pacient;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Pacient entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PacientRepository extends JpaRepository<Pacient, Long> {}
