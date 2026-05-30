// Carrossel
const slides = document.getElementById("slides")

if(slides) {

    let atual = 0

    const total = slides.children.length

    document.getElementById("next").onclick = () => {
        atual++

        if(atual >= total) {
            atual = 0
        }

        atualizar()
    }

    document.getElementById("prev").onclick = () => {
        atual--

        if(atual < 0) {
            atual = total - 1
        }

        atualizar()
    }

    function atualizar() {
        slides.style.transform = `translateX(-${atual * 100}%)`
    }

    setInterval(() => {
        atual++

        if(atual >= total) {
            atual = 0
        }

        atualizar()
    }, 5000)

}

// Chat
const chatButton = document.getElementById("chat-button")
const chatBox = document.getElementById("chat-box")
const input = document.getElementById("chat-input")
const sendButton = document.getElementById("send-button")
const mensagens = document.querySelector(".chat-messages")

// Toggle do chat
if(chatButton && chatBox) {

    chatButton.addEventListener("click", () => {

        if(chatBox.style.display === "flex") {
            chatBox.style.display = "none"
        } else {
            chatBox.style.display = "flex"
        }
    })
}

// Mensagens
function enviarMensagem() {

    const texto = input.value.trim()

    if(texto === "") return

    mensagens.innerHTML += `
    <div class="user-message">
        ${texto}
    </div>
    `

    input.value = ""

    mensagens.scrollTop = mensagens.scrollHeight
}

sendButton.onclick = enviarMensagem

input.addEventListener("keydown", (e) => {
    if(e.key === "Enter") {
        enviarMensagem()
    }
})

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
