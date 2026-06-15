package com.ColegioWeb.repositories;

import com.ColegioWeb.models.Falta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaltaRepository extends JpaRepository<Falta, Long> {
    java.util.List<Falta> findByAlunoIdAndDisciplinaIdOrderByDataFaltaDesc(Long alunoId, Long disciplinaId);
    long countByAlunoIdAndDisciplinaId(Long alunoId, Long disciplinaId);
}
