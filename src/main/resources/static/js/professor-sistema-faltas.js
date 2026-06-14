function abrirModalFalta() {
    document.getElementById("modalFalta").style.display = "flex";
}

function fecharModalFalta() {
    document.getElementById("modalFalta").style.display = "none";
}

function abrirModalDetalhesFalta() {
    document.getElementById("modalDetalhesFalta").style.display = "flex";
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