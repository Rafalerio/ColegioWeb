package com.ColegioWeb.repositories;

import com.ColegioWeb.models.Aviso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvisoRepository extends JpaRepository<Aviso, Long> {
    List<Aviso> findByTurmaIdOrIsGeralTrueOrderByDataCriacaoDesc(Long turmaId);
    List<Aviso> findByIsGeralTrueOrderByDataCriacaoDesc();
    List<Aviso> findByAutorIdOrderByDataCriacaoDesc(Long autorId);
    List<Aviso> findAllByOrderByDataCriacaoDesc();
}
