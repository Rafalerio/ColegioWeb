document.addEventListener("DOMContentLoaded", () => {
    const formCriarAvaliacao = document.getElementById("form-criar-avaliacao");
    if(formCriarAvaliacao) {
        formCriarAvaliacao.addEventListener("submit", async (e) => {
            e.preventDefault();
            
            const titulo = document.getElementById("input-titulo").value;
            const descricao = document.getElementById("input-descricao").value;
            const tipo = document.getElementById("select-tipo").value;
            const selectTurmaDisciplina = document.getElementById("select-turma-disciplina");
            const option = selectTurmaDisciplina.options[selectTurmaDisciplina.selectedIndex];
            const turmaId = option.dataset.turmaid;
            const disciplinaId = option.dataset.disciplinaid;
            const dataEntrega = document.getElementById("input-data").value;

            try {
                const btn = document.getElementById("btn-salvar-avaliacao");
                btn.disabled = true;
                btn.textContent = "Salvando...";

                const res = await fetch("/api/professor/tarefas", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ titulo, descricao, tipo, turmaId, disciplinaId, dataEntrega })
                });

                if (res.ok) {
                    alert("Avaliação criada com sucesso!");
                    window.location.reload();
                } else {
                    alert("Erro ao criar avaliação.");
                    btn.disabled = false;
                    btn.textContent = "Criar Avaliação";
                }
            } catch (err) {
                console.error(err);
                const btn = document.getElementById("btn-salvar-avaliacao");
                btn.disabled = false;
                btn.textContent = "Criar Avaliação";
            }
        });
    }
});

function abrirModalAvaliacao() {
    document.getElementById("modalAvaliacao").style.display = "flex";
}

function fecharModalAvaliacao() {
    document.getElementById("modalAvaliacao").style.display = "none";
}

async function abrirModalNotas(tarefaId) {
    document.getElementById("modalNotas").style.display = "flex";
    const container = document.getElementById("container-entregas-notas");
    container.innerHTML = "<p>Carregando entregas...</p>";

    try {
        const res = await fetch(`/api/professor/tarefas/${tarefaId}/entregas`);
        if (res.ok) {
            const entregas = await res.json();
            container.innerHTML = "<h3>Lançar Notas</h3>";
            
            if (entregas.length === 0) {
                container.innerHTML += "<p>Nenhuma entrega gerada.</p>";
                return;
            }

            entregas.forEach(e => {
                const div = document.createElement("div");
                div.className = "aluno-nota";
                
                let badgeClass = e.status === 'ENTREGUE' ? 'color: green;' : (e.status === 'PENDENTE' ? 'color: orange;' : 'color: blue;');
                
                div.innerHTML = `
                    <div style="display:flex; flex-direction:column;">
                        <span>${e.alunoNome}</span>
                        <span style="font-size:0.8rem; ${badgeClass}">Status: ${e.status}</span>
                    </div>
                    <div style="display:flex; gap:10px;">
                        <input type="number" id="nota-${e.entregaId}" min="0" max="10" step="0.1" placeholder="Nota" value="${e.nota || ''}">
                        <button onclick="salvarNotaEntrega(${e.entregaId})">Salvar</button>
                    </div>
                `;
                container.appendChild(div);
            });
        }
    } catch (err) {
        console.error("Erro ao carregar entregas", err);
    }
}

async function salvarNotaEntrega(entregaId) {
    const nota = document.getElementById(`nota-${entregaId}`).value;
    if (!nota) {
        alert("Preencha a nota!");
        return;
    }

    try {
        const res = await fetch(`/api/professor/entregas/${entregaId}/avaliar?nota=${nota}&observacao=`, {
            method: "POST"
        });
        if (res.ok) {
            alert("Nota salva!");
        } else {
            alert("Erro ao salvar nota.");
        }
    } catch (err) {
        console.error(err);
    }
}

function fecharModalNotas() {
    document.getElementById("modalNotas").style.display = "none";
}

window.addEventListener("click", (event) => {
    const modalAvaliacao = document.getElementById("modalAvaliacao");
    const modalNotas = document.getElementById("modalNotas");

    if (event.target === modalAvaliacao) {
        fecharModalAvaliacao();
    }

    if (event.target === modalNotas) {
        fecharModalNotas();
    }
});

document.addEventListener("keydown", (event) => {
    if (event.key === "Escape") {
        fecharModalAvaliacao();
        fecharModalNotas();
    }
});