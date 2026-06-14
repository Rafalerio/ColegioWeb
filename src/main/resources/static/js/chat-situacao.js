document.addEventListener("DOMContentLoaded", function() {
    const chatForm = document.getElementById('chat-form');
    const chatbox = document.getElementById('chatbox');
    const mensagemInput = document.getElementById('mensagem-input');
    const fileInput = document.getElementById('documento-upload');
    const fileNameDisplay = document.getElementById('file-name-display');
    const btnEnviar = document.getElementById('btn-enviar');
    const btnLimpar = document.getElementById('btn-limpar');

    // Atualiza o nome do arquivo na interface quando selecionado com visual modernizado
    fileInput.addEventListener('change', function() {
        if (this.files && this.files.length > 0) {
            fileNameDisplay.innerHTML = `<i class="bx bx-file" style="color: #20316c;"></i> Arquivo selecionado: <strong style="color: #20316c;">${this.files[0].name}</strong>`;
        } else {
            fileNameDisplay.innerHTML = `<i class="bx bx-info-circle"></i> Nenhum arquivo anexado`;
        }
    });

    // Permite limpar o histórico de chat da sessão
    if (btnLimpar) {
        btnLimpar.addEventListener('click', function() {
            if (confirm("Deseja realmente limpar o histórico da conversa e o documento analisado?")) {
                fetch('/api/chat/limpar', {
                    method: 'POST'
                })
                .then(response => response.json())
                .then(data => {
                    chatbox.innerHTML = `
                        <div class="message ia">
                            Olá, Professor! Envie um documento (laudo, relatório de notas, etc.) e diga o que você gostaria que eu analisasse ou tire suas dúvidas sobre estudos e o ENEM.
                        </div>
                    `;
                    fileInput.value = "";
                    fileNameDisplay.innerHTML = `<i class="bx bx-info-circle"></i> Nenhum arquivo anexado`;
                })
                .catch(error => {
                    console.error('Erro ao limpar histórico:', error);
                    alert("Erro ao tentar limpar o histórico de chat.");
                });
            }
        });
    }

    // Permite enviar com "Enter" sem quebrar linha (Shift+Enter quebra linha)
    mensagemInput.addEventListener('keydown', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            if (mensagemInput.value.trim() !== '') {
                chatForm.dispatchEvent(new Event('submit'));
            }
        }
    });

    chatForm.addEventListener('submit', function(e) {
        e.preventDefault();

        const textoMensagem = mensagemInput.value.trim();
        const arquivo = fileInput.files[0];

        if (!textoMensagem && !arquivo) return;

        // 1. Adiciona a mensagem do professor na tela
        adicionarMensagem(textoMensagem, arquivo ? arquivo.name : null, 'professor');

        // Desabilita input enquanto carrega com feedback de processamento
        btnEnviar.disabled = true;
        btnEnviar.innerHTML = '<span>Analisando...</span> <i class="bx bx-loader-alt bx-spin"></i>';
        mensagemInput.value = '';

        // 2. Prepara os dados para o Backend (FormData permite enviar texto + arquivo)
        const formData = new FormData();
        formData.append('mensagem', textoMensagem);
        if (arquivo) {
            formData.append('documento', arquivo);
        }

        // 3. Faz a requisição AJAX para o Controller REST do Spring Boot
        fetch('/api/chat/analisar', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'sucesso') {
                adicionarMensagem(data.resposta, null, 'ia');
            } else {
                adicionarMensagem("Erro: " + data.resposta, null, 'ia');
            }
        })
        .catch(error => {
            adicionarMensagem("Ocorreu um erro de conexão com o servidor. Verifique se a aplicação está rodando.", null, 'ia');
            console.error('Erro no chat:', error);
        })
        .finally(() => {
            // Reabilita os inputs e limpa o arquivo selecionado
            btnEnviar.disabled = false;
            btnEnviar.innerHTML = '<span>Enviar</span> <i class="bx bx-send"></i>';
            fileInput.value = "";
            fileNameDisplay.innerHTML = `<i class="bx bx-info-circle"></i> Nenhum arquivo anexado`;
        });
    });

    function adicionarMensagem(texto, nomeArquivo, remetente) {
        const divMsg = document.createElement('div');
        divMsg.classList.add('message', remetente);

        let conteudoHTML = '';
        if (nomeArquivo) {
            conteudoHTML += `<strong style="font-size: 0.85rem; display: flex; align-items: center; gap: 4px; margin-bottom: 6px;"><i class="bx bx-paperclip"></i> ${nomeArquivo}</strong>`;
        }

        // Converte quebras de linha em <br> para manter a formatação estruturada do Markdown ou texto do Gemini
        const textoFormatado = texto.replace(/\n/g, '<br>');
        conteudoHTML += `<span>${textoFormatado}</span>`;

        divMsg.innerHTML = conteudoHTML;
        chatbox.appendChild(divMsg);

        // Rola suavemente para o fim do chat
        chatbox.scrollTo({
            top: chatbox.scrollHeight,
            behavior: 'smooth'
        });
    }
});