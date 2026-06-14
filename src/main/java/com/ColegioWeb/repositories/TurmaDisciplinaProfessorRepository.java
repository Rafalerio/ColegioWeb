package com.ColegioWeb.repositories;

import com.ColegioWeb.models.TurmaDisciplinaProfessor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurmaDisciplinaProfessorRepository extends JpaRepository<TurmaDisciplinaProfessor, Long> {
    List<TurmaDisciplinaProfessor> findByTurmaId(Long turmaId);
    List<TurmaDisciplinaProfessor> findByProfessorId(Long professorId);
    TurmaDisciplinaProfessor findByTurmaIdAndDisciplinaId(Long turmaId, Long disciplinaId);
}
