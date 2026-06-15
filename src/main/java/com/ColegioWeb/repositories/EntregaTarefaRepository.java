package com.ColegioWeb.repositories;

import com.ColegioWeb.models.EntregaTarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EntregaTarefaRepository extends JpaRepository<EntregaTarefa, Long> {
    List<EntregaTarefa> findByTarefaId(Long tarefaId);
    List<EntregaTarefa> findByAlunoIdOrderByTarefaDataEntregaDesc(Long alunoId);
    Optional<EntregaTarefa> findByTarefaIdAndAlunoId(Long tarefaId, Long alunoId);
}
