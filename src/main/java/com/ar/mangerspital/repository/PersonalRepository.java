package com.ar.mangerspital.repository;

import com.ar.mangerspital.domain.Personal;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Personal entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PersonalRepository extends JpaRepository<Personal, Long> {}
