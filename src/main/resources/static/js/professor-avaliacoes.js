function abrirModalAvaliacao() {
    document.getElementById("modalAvaliacao").style.display = "flex";
}

function fecharModalAvaliacao() {
    document.getElementById("modalAvaliacao").style.display = "none";
}

function abrirModalNotas() {
    document.getElementById("modalNotas").style.display = "flex";
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
})

document.addEventListener("keydown", (event) => {
    if (event.key === "Escape") {
        fecharModalAvaliacao();
        fecharModalNotas();
    }
});