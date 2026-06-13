package com.ColegioWeb.repositories;

import com.ColegioWeb.models.PeriodoMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeriodoMatriculaRepository extends JpaRepository<PeriodoMatricula, Long> {
    List<PeriodoMatricula> findByAbertoTrue();
}
