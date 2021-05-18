package com.ar.mangerspital.repository;

import com.ar.mangerspital.domain.Sectie;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Sectie entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SectieRepository extends JpaRepository<Sectie, Long> {}
