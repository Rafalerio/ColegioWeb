package com.ColegioWeb.repositories;

import com.ColegioWeb.models.HistoricoMatricula;
import com.ColegioWeb.models.SolicitacaoMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoMatriculaRepository extends JpaRepository<HistoricoMatricula, Long> {
    List<HistoricoMatricula> findBySolicitacao(SolicitacaoMatricula solicitacao);
}
