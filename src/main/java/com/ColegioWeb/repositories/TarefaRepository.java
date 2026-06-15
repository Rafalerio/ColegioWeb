package com.ColegioWeb.repositories;

import com.ColegioWeb.models.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    java.util.List<Tarefa> findByProfessorIdOrderByDataEntregaDesc(Long professorId);
}
