document.addEventListener("DOMContentLoaded", () => {
    const selectTurmaDisciplina = document.getElementById("select-turma-disciplina");
    const selectAluno = document.getElementById("select-aluno");
    const formRegistrarFalta = document.getElementById("form-registrar-falta");

    selectTurmaDisciplina.addEventListener("change", async (e) => {
        const option = e.target.options[e.target.selectedIndex];
        const turmaId = option.dataset.turmaid;
        
        selectAluno.innerHTML = '<option value="">Selecione um Aluno</option>';
        selectAluno.disabled = true;

        if (turmaId) {
            try {
                const res = await fetch(`/api/professor/turmas/${turmaId}/alunos`);
                if (res.ok) {
                    const alunos = await res.json();
                    alunos.forEach(a => {
                        const opt = document.createElement("option");
                        opt.value = a.alunoId;
                        opt.textContent = a.alunoNome;
                        selectAluno.appendChild(opt);
                    });
                    selectAluno.disabled = false;
                }
            } catch (err) {
                console.error("Erro ao carregar alunos", err);
            }
        }
    });

    formRegistrarFalta.addEventListener("submit", async (e) => {
        e.preventDefault();
        
        const option = selectTurmaDisciplina.options[selectTurmaDisciplina.selectedIndex];
        const disciplinaId = option.dataset.disciplinaid;
        const alunoId = selectAluno.value;
        const dataFalta = document.getElementById("input-data-falta").value;

        try {
            const btn = document.getElementById("btn-salvar-falta");
            btn.disabled = true;
            btn.textContent = "Registrando...";

            const res = await fetch("/api/professor/faltas", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ alunoId, disciplinaId, dataFalta })
            });

            if (res.ok) {
                alert("Falta registrada com sucesso!");
                window.location.reload(); // Recarrega a página para o Thymeleaf atualizar os cards
            } else {
                alert("Erro ao registrar falta.");
                btn.disabled = false;
                btn.textContent = "Registrar Falta";
            }
        } catch (err) {
            console.error(err);
            const btn = document.getElementById("btn-salvar-falta");
            btn.disabled = false;
            btn.textContent = "Registrar Falta";
        }
    });
});

function abrirModalFalta() {
    document.getElementById("modalFalta").style.display = "flex";
    
    // Resetar o form
    document.getElementById("form-registrar-falta").reset();
    document.getElementById("select-aluno").disabled = true;
    document.getElementById("select-aluno").innerHTML = '<option value="">Selecione um Aluno</option>';
    
    // Configurar a data máxima para hoje
    const today = new Date().toISOString().split('T')[0];
    document.getElementById("input-data-falta").setAttribute("max", today);
}

function fecharModalFalta() {
    document.getElementById("modalFalta").style.display = "none";
}

let atualAlunoId = null;
let atualDisciplinaId = null;
let atualAlunoNome = "";

async function abrirModalDetalhesFalta(alunoId, alunoNome, disciplinaId) {
    document.getElementById("modalDetalhesFalta").style.display = "flex";
    document.getElementById("titulo-detalhes-aluno").textContent = `Faltas: ${alunoNome}`;
    
    atualAlunoId = alunoId;
    atualDisciplinaId = disciplinaId;
    atualAlunoNome = alunoNome;

    await carregarDetalhes(alunoId, disciplinaId);
}

async function carregarDetalhes(alunoId, disciplinaId) {
    try {
        const res = await fetch(`/api/professor/faltas/detalhes?alunoId=${alunoId}&disciplinaId=${disciplinaId}`);
        if (res.ok) {
            const faltas = await res.json();
            const container = document.getElementById("lista-datas-faltas");
            container.innerHTML = "";

            if (faltas.length === 0) {
                container.innerHTML = "<p style='margin-top: 15px'>Nenhuma falta registrada para esta disciplina.</p>";
                return;
            }

            faltas.forEach(f => {
                const dataFormatada = f.dataFalta.split("-").reverse().join("/");
                const div = document.createElement("div");
                div.className = "faltas-registro";
                div.innerHTML = `
                    <span>${dataFormatada}</span>
                    <button onclick="removerFalta(${f.faltaId})">Excluir</button>
                `;
                container.appendChild(div);
            });
        }
    } catch (err) {
        console.error("Erro ao carregar detalhes", err);
    }
}

async function removerFalta(faltaId) {
    if (confirm("Tem certeza que deseja excluir esta falta?")) {
        try {
            const res = await fetch(`/api/professor/faltas/${faltaId}`, {
                method: "DELETE"
            });
            if (res.ok) {
                await carregarDetalhes(atualAlunoId, atualDisciplinaId);
                window.location.reload(); // Recarrega para atualizar o Thymeleaf
            } else {
                alert("Erro ao excluir falta.");
            }
        } catch (err) {
            console.error("Erro", err);
        }
    }
}

function fecharModalDetalhesFalta() {
    document.getElementById("modalDetalhesFalta").style.display = "none";
}

window.addEventListener("click", (event) => {
    const modalFalta = document.getElementById("modalFalta");
    const modalDetalhesFalta = document.getElementById("modalDetalhesFalta");

    if (event.target === modalFalta) {
        fecharModalFalta();
    }

    if (event.target === modalDetalhesFalta) {
        fecharModalDetalhesFalta();
    }
})

document.addEventListener("keydown", (event) => {
    if (event.key === "Escape") {
        fecharModalFalta();
        fecharModalDetalhesFalta();
    }
});