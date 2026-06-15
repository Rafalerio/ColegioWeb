async function entregarAvaliacao(entregaId) {
    if(!confirm("Deseja marcar esta avaliação como entregue?")) return;

    try {
        const res = await fetch(`/api/aluno/entregas/${entregaId}/entregar`, {
            method: 'POST'
        });
        
        if (res.ok) {
            alert("Entrega confirmada com sucesso!");
            window.location.reload();
        } else {
            alert("Erro ao marcar como entregue.");
        }
    } catch (err) {
        console.error(err);
        alert("Erro na requisição.");
    }
}
