package com.ColegioWeb.repositories;

import com.ColegioWeb.models.SolicitacaoMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoMatriculaRepository extends JpaRepository<SolicitacaoMatricula, Long> {
    List<SolicitacaoMatricula> findByAlunoId(Long alunoId);
}
