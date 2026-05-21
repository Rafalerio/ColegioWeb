package com.ColegioWeb.repositories;

import com.ColegioWeb.models.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
// Métodos personalizados, se necessário
}