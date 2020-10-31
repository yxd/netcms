package com.myapp.repository;

import com.myapp.domain.D;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the D entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DRepository extends JpaRepository<D, Long> {
}
