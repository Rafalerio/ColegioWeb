// Verificação de conexão com o frontend
console.log("Frontend connected and script loaded!");

// Carrossel
const slides = document.getElementById("slides");

if (slides) {
    let atual = 0;
    const total = slides.children.length;

    document.getElementById("next").onclick = () => {
        atual++;
        if (atual >= total) {
            atual = 0;
        }
        atualizar();
    };

    document.getElementById("prev").onclick = () => {
        atual--;
        if (atual < 0) {
            atual = total - 1;
        }
        atualizar();
    };

    function atualizar() {
        slides.style.transform = `translateX(-${atual * 100}%)`;
    }

    setInterval(() => {
        atual++;
        if (atual >= total) {
            atual = 0;
        }
        atualizar();
    }, 5000);
}

// Chat
const chatButton = document.getElementById("chat-button");
const chatBox = document.getElementById("chat-box");
const input = document.getElementById("chat-input");
const sendButton = document.getElementById("send-button");
const mensagens = document.querySelector(".chat-messages");

// Toggle do chat
if (chatButton && chatBox) {
    chatButton.addEventListener("click", () => {
        if (chatBox.style.display === "flex") {
            chatBox.style.display = "none";
        } else {
            chatBox.style.display = "flex";
        }
    });
}

// Função atualizada para enviar e receber mensagens da IA
async function enviarMensagem() {
    const texto = input.value.trim();

    if (texto === "") return;

    // 1. Exibe a mensagem do usuário
    mensagens.innerHTML += `
        <div class="user-message">
            ${texto}
        </div>
    `;
    input.value = "";
    mensagens.scrollTop = mensagens.scrollHeight;

    // 2. Adiciona um indicador de "digitando..."
    const idCarregando = "msg-" + Date.now();
    mensagens.innerHTML += `
        <div class="bot-message" id="${idCarregando}">
            <em>Digitando...</em>
        </div>
    `;
    mensagens.scrollTop = mensagens.scrollHeight;

    // 3. Faz a requisição para o Back-end
    try {
        const response = await fetch("http://localhost:3000/api/chat", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ mensagem: texto })
        });

        const data = await response.json();

        // 4. Substitui a mensagem de "digitando..." pela resposta do Gemini
        const mensagemBot = document.getElementById(idCarregando);

        if (data.resposta) {
            // Convertendo quebras de linha para <br> no HTML
            mensagemBot.innerHTML = data.resposta.replace(/\n/g, '<br>');
        } else {
            mensagemBot.innerHTML = "Erro ao obter resposta.";
        }

    } catch (error) {
        console.error("Erro:", error);
        const mensagemBot = document.getElementById(idCarregando);
        mensagemBot.innerHTML = "Não foi possível conectar ao servidor.";
    }

    mensagens.scrollTop = mensagens.scrollHeight;
}

// Eventos de clique e teclado (Mantidos do seu código original)
sendButton.onclick = enviarMensagem;

input.addEventListener("keydown", (e) => {
    if (e.key === "Enter") {
        enviarMensagem();
    }
});

// Máscara de Telefone: Somente números e força o padrão (XX)9XXXX-XXXX
const telefoneInput = document.getElementById("telefone");

if (telefoneInput) {
    telefoneInput.addEventListener("input", function (e) {
        // Remove tudo que não for número (neste formato)
        let value = e.target.value.replace(/\D/g, "");

        // Limita a 11 números, que corresponde ao ddd (2) + 9 (1) + número (8)
        if (value.length > 11) {
            value = value.substring(0, 11);
        }

        // Força a inserção do dígito 9 na terceira posição, caso o usuário tenha um código de ddd com o 9 ausente
        if (value.length > 2 && value[2] !== "9") {
            value = value.substring(0, 2) + "9" + value.substring(2);
            if (value.length > 11) {
                value = value.substring(0, 11); // Reduz se passar
            }
        }

        // Aplica a formatação em string
        let formatted = value;
        if (value.length > 0) {
            formatted = "(" + value.substring(0, 2);
            if (value.length > 2) {
                formatted += ")" + value.substring(2, 3);
            }
            if (value.length > 3) {
                formatted += value.substring(3, 7);
            }
            if (value.length > 7) {
                formatted += "-" + value.substring(7, 11); // 4 digitos
            }
        }

        e.target.value = formatted;
    });
}

// Máscara para CPF e RG: Somente números
const cpfRgInputs = document.querySelectorAll("input[name='cpfAluno'], input[name='rgAluno'], input[name='cpfResponsavel'], input[name='rgResponsavel']");

cpfRgInputs.forEach(input => {
    input.addEventListener("input", function(e) {
        // Remove tudo que não for número
        e.target.value = e.target.value.replace(/\D/g, "");
    });
});

// Envio do formulário de matrícula
const formMatricula = document.getElementById("formMatricula");

if (formMatricula) {
    formMatricula.addEventListener("submit", async function(e) {
        e.preventDefault();

        const formData = new FormData(formMatricula);
        const data = Object.fromEntries(formData.entries());

        const erroDiv = document.getElementById("mensagem-erro");
        const sucessoDiv = document.getElementById("mensagem-sucesso");

        erroDiv.style.display = "none";
        sucessoDiv.style.display = "none";
        erroDiv.textContent = "";

        try {
            const response = await fetch("/api/matricula", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                sucessoDiv.style.display = "block";
                formMatricula.reset();
            } else {
                const errorText = await response.text();
                erroDiv.textContent = errorText || "Erro ao realizar matrícula.";
                erroDiv.style.display = "block";
            }
        } catch (error) {
            erroDiv.textContent = "Erro de conexão ao tentar enviar a matrícula.";
            erroDiv.style.display = "block";
        }
    });
}