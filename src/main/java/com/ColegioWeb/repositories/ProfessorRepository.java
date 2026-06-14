package com.ColegioWeb.repositories;

import com.ColegioWeb.models.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByCpf(String cpf);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM Professor p LEFT JOIN FETCH p.disciplinas WHERE p.id = :id")
    Optional<Professor> findByIdWithDisciplinas(@org.springframework.data.repository.query.Param("id") Long id);
}