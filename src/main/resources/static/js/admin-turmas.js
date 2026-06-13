document.addEventListener("DOMContentLoaded", () => {
    carregarTurmas();
    carregarDisciplinas();

    document.getElementById("turmaForm").addEventListener("submit", salvarTurma);
});

function abrirModalNovaTurma() {
    document.getElementById("modalTitle").innerText = "Nova Turma";
    document.getElementById("turmaForm").reset();
    document.getElementById("turmaId").value = "";
    document.getElementById("turmaModal").style.display = "flex";
}

function fecharModal() {
    document.getElementById("turmaModal").style.display = "none";
}

async function carregarTurmas() {
    try {
        const response = await fetch('/api/turmas');
        const turmas = await response.json();
        const tbody = document.getElementById("turmasTbody");
        tbody.innerHTML = "";

        turmas.forEach(turma => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${turma.id}</td>
                <td>${turma.nome}</td>
                <td>${turma.serie}</td>
                <td>${turma.turno}</td>
                <td>Ativo</td>
                <td>
                    <button onclick='abrirModalEditar(${JSON.stringify(turma)})' style="padding: 5px; cursor: pointer;">Editar</button>
                    <button onclick="deletarTurma(${turma.id})" style="padding: 5px; cursor: pointer; color: red;">Inativar</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error("Erro ao carregar turmas", error);
    }
}

async function carregarDisciplinas() {
    try {
        const response = await fetch('/api/admin/disciplinas');
        const disciplinas = await response.json();
        const select = document.getElementById("disciplinas");
        select.innerHTML = "";
        
        disciplinas.forEach(d => {
            if (d.ativo) {
                const option = document.createElement("option");
                option.value = d.id;
                option.innerText = d.nome;
                select.appendChild(option);
            }
        });
    } catch (error) {
        console.error("Erro ao carregar disciplinas", error);
    }
}

function abrirModalEditar(turma) {
    document.getElementById("modalTitle").innerText = "Editar Turma";
    document.getElementById("turmaId").value = turma.id;
    document.getElementById("nome").value = turma.nome;
    document.getElementById("serie").value = turma.serie;
    document.getElementById("turno").value = turma.turno;
    document.getElementById("anoLetivo").value = turma.anoLetivo;
    document.getElementById("capacidadeMax").value = turma.capacidadeMax;

    // Set selected disciplines
    const select = document.getElementById("disciplinas");
    Array.from(select.options).forEach(opt => opt.selected = false);
    if (turma.disciplinaIds) {
        turma.disciplinaIds.forEach(id => {
            Array.from(select.options).forEach(opt => {
                if (parseInt(opt.value) === id) {
                    opt.selected = true;
                }
            });
        });
    }

    document.getElementById("turmaModal").style.display = "flex";
}

async function salvarTurma(event) {
    event.preventDefault();
    
    const id = document.getElementById("turmaId").value;
    const isEditing = id !== "";
    const method = isEditing ? "PUT" : "POST";
    const url = isEditing ? `/api/turmas/${id}` : "/api/turmas";

    const select = document.getElementById("disciplinas");
    const disciplinaIds = Array.from(select.selectedOptions).map(opt => parseInt(opt.value));

    const payload = {
        id: isEditing ? parseInt(id) : null,
        nome: document.getElementById("nome").value,
        serie: document.getElementById("serie").value,
        turno: document.getElementById("turno").value,
        anoLetivo: parseInt(document.getElementById("anoLetivo").value),
        capacidadeMax: parseInt(document.getElementById("capacidadeMax").value),
        disciplinaIds: disciplinaIds
    };

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            fecharModal();
            carregarTurmas();
            alert("Turma salva com sucesso!");
        } else {
            const err = await response.text();
            alert("Erro ao salvar: " + err);
        }
    } catch (error) {
        console.error("Erro ao salvar", error);
        alert("Ocorreu um erro inesperado.");
    }
}

async function deletarTurma(id) {
    if (!confirm("Tem certeza que deseja inativar esta turma?")) return;

    try {
        const response = await fetch(`/api/turmas/${id}`, {
            method: "DELETE"
        });

        if (response.ok || response.status === 204) {
            alert("Turma inativada com sucesso!");
            carregarTurmas();
        } else {
            const err = await response.text();
            alert("Erro: " + err);
        }
    } catch (error) {
        console.error("Erro ao inativar", error);
    }
}
