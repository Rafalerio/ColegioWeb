package com.ColegioWeb.repositories;

import com.ColegioWeb.models.HistoricoAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoAlunoRepository extends JpaRepository<HistoricoAluno, Long> {
    List<HistoricoAluno> findByAlunoId(Long alunoId);
}
