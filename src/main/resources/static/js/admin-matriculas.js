document.addEventListener("DOMContentLoaded", () => {
    carregarPeriodos();
    carregarSolicitacoes();
    carregarTurmas();

    document.getElementById("periodoForm").addEventListener("submit", criarPeriodo);
    document.getElementById("aprovarForm").addEventListener("submit", confirmarMatricula);
});

let turmas = [];

function abrirModalPeriodo() {
    document.getElementById("periodoForm").reset();
    document.getElementById("periodoModal").style.display = "flex";
}

function fecharModal(modalId) {
    document.getElementById(modalId).style.display = "none";
}

async function carregarPeriodos() {
    try {
        const response = await fetch('/api/admin/matriculas/periodos');
        const periodos = await response.json();
        const tbody = document.getElementById("periodosTbody");
        tbody.innerHTML = "";

        periodos.forEach(p => {
            const statusClass = p.aberto ? "status-aberto" : "status-fechado";
            const statusText = p.aberto ? "Aberto" : "Fechado";
            const acaoText = p.aberto ? "Fechar" : "Abrir";
            const novoStatus = !p.aberto;

            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${p.id}</td>
                <td>${p.nome}</td>
                <td>${p.dataInicio}</td>
                <td>${p.dataFim}</td>
                <td style="font-weight:bold; color: ${p.aberto ? '#10b981' : '#ef4444'}">${statusText}</td>
                <td>
                    <button onclick="alterarStatusPeriodo(${p.id}, ${novoStatus})" style="padding: 5px; cursor: pointer;">${acaoText}</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error("Erro ao carregar periodos", error);
    }
}

async function criarPeriodo(event) {
    event.preventDefault();
    const payload = {
        nome: document.getElementById("nomePeriodo").value,
        dataInicio: document.getElementById("dataInicio").value,
        dataFim: document.getElementById("dataFim").value,
        aberto: document.getElementById("statusPeriodo").value === "true"
    };

    try {
        const response = await fetch('/api/admin/matriculas/periodos', {
            method: 'POST',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (response.ok) {
            fecharModal('periodoModal');
            carregarPeriodos();
        } else {
            alert("Erro ao criar período.");
        }
    } catch (error) {
        console.error(error);
    }
}

async function alterarStatusPeriodo(id, aberto) {
    try {
        const response = await fetch(`/api/admin/matriculas/periodos/${id}/status?aberto=${aberto}`, { method: 'PUT' });
        if (response.ok) {
            carregarPeriodos();
        } else {
            alert("Erro ao alterar status do período.");
        }
    } catch (error) {
        console.error(error);
    }
}

async function carregarSolicitacoes() {
    try {
        const response = await fetch('/api/admin/matriculas/solicitacoes');
        const sol = await response.json();
        const tbody = document.getElementById("solicitacoesTbody");
        tbody.innerHTML = "";

        sol.forEach(s => {
            const d = new Date(s.dataSolicitacao).toLocaleDateString();
            const tr = document.createElement("tr");
            let acoes = "";
            
            if (s.status === "SOLICITADA" || s.status === "EM_ANALISE") {
                acoes = `
                    <button onclick="prepararAprovacao(${s.id})" style="padding: 5px; cursor: pointer; color: #10b981;">Aprovar</button>
                    <button onclick="alterarStatusSolicitacao(${s.id}, 'CANCELADA')" style="padding: 5px; cursor: pointer; color: #ef4444;">Reprovar</button>
                `;
            } else {
                acoes = `<span style="color: #64748b; font-size: 0.9em;">Já processada</span>`;
            }

            tr.innerHTML = `
                <td>${s.id}</td>
                <td>${d}</td>
                <td>${s.periodo ? s.periodo.nome : ''}</td>
                <td>${s.aluno ? s.aluno.nome : ''}</td>
                <td style="font-weight:bold;">${s.status}</td>
                <td>${acoes}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error("Erro ao carregar solicitações", error);
    }
}

async function carregarTurmas() {
    try {
        const response = await fetch('/api/turmas');
        turmas = await response.json();
        const select = document.getElementById("turmaSelect");
        select.innerHTML = "";
        turmas.forEach(t => {
            const opt = document.createElement("option");
            opt.value = t.id;
            opt.innerText = `${t.nome} - ${t.serie} (${t.turno})`;
            select.appendChild(opt);
        });
    } catch (error) {
        console.error("Erro ao carregar turmas", error);
    }
}

function prepararAprovacao(id) {
    document.getElementById("solicitacaoId").value = id;
    document.getElementById("aprovarModal").style.display = "flex";
}

async function confirmarMatricula(event) {
    event.preventDefault();
    const id = document.getElementById("solicitacaoId").value;
    const turmaId = document.getElementById("turmaSelect").value;
    
    try {
        const response = await fetch(`/api/admin/matriculas/solicitacoes/${id}/status?status=CONFIRMADA&turmaId=${turmaId}`, {
            method: 'PUT'
        });
        if (response.ok) {
            fecharModal('aprovarModal');
            carregarSolicitacoes();
            alert("Matrícula confirmada com sucesso!");
        } else {
            const err = await response.text();
            alert("Erro: " + err);
        }
    } catch (error) {
        console.error(error);
    }
}

async function alterarStatusSolicitacao(id, status) {
    if (!confirm("Confirmar a alteração desta solicitação para " + status + "?")) return;
    
    try {
        const response = await fetch(`/api/admin/matriculas/solicitacoes/${id}/status?status=${status}`, {
            method: 'PUT'
        });
        if (response.ok) {
            carregarSolicitacoes();
        } else {
            const err = await response.text();
            alert("Erro: " + err);
        }
    } catch (error) {
        console.error(error);
    }
}
