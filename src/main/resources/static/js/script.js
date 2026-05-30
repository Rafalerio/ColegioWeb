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