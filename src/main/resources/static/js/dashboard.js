/**
 * ===================================================================
 * SMART TODO - DASHBOARD SCRIPT (Refatorado para Layout Moderno)
 * ===================================================================
 * * Este script controla todo o dashboard, incluindo:
 * 1. Carregamento inicial de dados (Perfil, Tarefas)
 * 2. Renderização da lista de tarefas no novo layout de DIVs
 * 3. Gerenciamento de eventos (CRUD de tarefas, Modais, Chat, Busca)
 * 4. Lógica de responsividade do menu mobile.
 */

// Espera o DOM (estrutura HTML) estar completamente carregado antes de executar o script.
document.addEventListener('DOMContentLoaded', () => {

    // -----------------------------------------------------------------
    // 1. SELETORES DE ELEMENTOS
    // -----------------------------------------------------------------
    const token = localStorage.getItem('token');

    // Cache local de tarefas para busca rápida
    let allTasks = [];

    // Perfil
    const userAvatar = document.getElementById('userAvatar');
    const userNome = document.getElementById('userNome');
    const userEmail = document.getElementById('userEmail');

    // Menu Mobile
    const sidebar = document.getElementById('sidebar');
    const openBtn = document.getElementById('open-menu-btn');
    const closeBtn = document.getElementById('close-menu-btn');
    const overlay = document.createElement('div');
    overlay.className = 'sidebar-overlay';
    document.body.appendChild(overlay);

    // Cards de Resumo
    const totalTasksEl = document.getElementById('totalTasks');
    const pendingTasksEl = document.getElementById('pendingTasks');
    const progressTasksEl = document.getElementById('progressTasks');
    const completedTasksEl = document.getElementById('completedTasks');

    // Seção de Tarefas
    const tasksListContainer = document.getElementById('tasksListContainer');
    const btnNovaTarefa = document.getElementById('btnNovaTarefa');
    const inputBuscaTask = document.getElementById('inputBuscaTask');

    // Modal de Nova Tarefa
    const novaTarefaModal = document.getElementById('novaTarefaModal');
    const closeNovaTarefa = document.getElementById('closeNovaTarefa');
    const formNovaTarefa = document.getElementById('formNovaTarefa');

    // Modal de Chat IA
    const btnConversaIA = document.getElementById('btnConversaIA');
    const modalConversaIA = document.getElementById('modalConversaIA');
    const closeConversaIA = document.getElementById('closeConversaIA');
    const chatMensagens = document.getElementById('chatMensagens');
    const formConversaIA = document.getElementById('formConversaIA');
    const inputMsgIA = document.getElementById('inputMsgIA');

    // -----------------------------------------------------------------
    // 2. FUNÇÃO HELPER DE FETCH (Centraliza a Lógica de API)
    // -----------------------------------------------------------------

    /**
     * Uma função wrapper para o 'fetch' que automaticamente:
     * 1. Adiciona o cabeçalho 'Authorization' com o token.
     * 2. Define 'Content-Type' como 'application/json' (quando aplicável).
     * 3. Redireciona para o login se o token for inválido (401/403).
     * 4. Lança um erro para respostas não-ok.
     */
    async function fetchApi(url, options = {}) {
        const headers = {
            'Authorization': `Bearer ${token}`,
            ...options.headers,
        };

        // Adiciona Content-Type se houver um corpo (body) e não for FormData
        if (options.body && !(options.body instanceof FormData)) {
            headers['Content-Type'] = 'application/json';
        }

        const response = await fetch(url, { ...options, headers });

        // Se o token for inválido ou expirado, o backend retorna 401 ou 403.
        // Redirecionamos para o login.
        if (response.status === 401 || response.status === 403) {
            localStorage.removeItem('token'); // Limpa o token inválido
            window.location.href = 'login.html';
            throw new Error('Não autorizado. Redirecionando para login.');
        }

        // Para requisições DELETE que retornam 204 No Content
        if (response.status === 204) {
            return null; // Não há JSON para decodificar
        }

        // Para outras respostas, tentamos decodificar o JSON
        const data = await response.json().catch(() => {
            // Se a resposta não for JSON (ex: erro 500 com HTML)
            throw new Error(`Erro ${response.status}: A resposta não é um JSON válido.`);
        });

        // Se a resposta for um erro (ex: 400 Bad Request, 404 Not Found)
        if (!response.ok) {
            throw new Error(data.message || `Erro HTTP: ${response.status}`);
        }

        return data;
    }

    // -----------------------------------------------------------------
    // 3. FUNÇÕES PRINCIPAIS (Carregar, Renderizar, Atualizar)
    // -----------------------------------------------------------------

    /**
     * Carrega o perfil do usuário e atualiza a sidebar.
     */
    async function loadUserProfile() {
        try {
            // Este endpoint /me foi adicionado ao AuthController
            const user = await fetchApi('/api/v1/auth/me');
            userNome.textContent = user.fullName; // Assumindo que o campo é fullName
            userEmail.textContent = user.email;
            // userAvatar.src = user.avatarUrl || 'default-avatar.png'; // Futura melhoria
        } catch (error) {
            console.error('Falha ao carregar perfil do usuário:', error.message);
        }
    }

    /**
     * Carrega as estatísticas (cards) e a lista de tarefas.
     */
    async function loadDashboardData() {
        try {
            allTasks = await fetchApi('/api/v1/tasks');
            updateSummaryCards(allTasks);
            renderTasks(allTasks);
        } catch (error) {
            console.error('Falha ao carregar dados do dashboard:', error.message);
            tasksListContainer.innerHTML = '<div class="task-item"><span class="task-title">Erro ao carregar tarefas. Tente recarregar a página.</span></div>';
        }
    }

    /**
     * Atualiza os 4 cards de resumo com base na lista de tarefas.
     * CORRIGIDO: Agora atualiza apenas o número, pois o label está no HTML.
     */
    function updateSummaryCards(tasks) {
        totalTasksEl.textContent = tasks.length;
        pendingTasksEl.textContent = tasks.filter(t => t.status === 'PENDING').length;
        progressTasksEl.textContent = 0; // Corrigido (não temos 'IN_PROGRESS')
        completedTasksEl.textContent = tasks.filter(t => t.status === 'COMPLETED').length;
    }

    /**
     * Renderiza a lista de tarefas no DOM.
     * REFEITO: Esta função agora cria 'div.task-item' para o novo layout.
     */
    function renderTasks(tasks) {
        tasksListContainer.innerHTML = ''; // Limpa a lista atual

        if (tasks.length === 0) {
            tasksListContainer.innerHTML = '<div class="task-item"><span class="task-title">Você ainda não tem tarefas cadastradas.</span></div>';
            return;
        }

        tasks.forEach(task => {
            // Define as classes de CSS com base no status e prioridade
            const statusClass = `status-${task.status.toLowerCase()}`;
            const priorityClass = `priority-${task.priority ? task.priority.toLowerCase() : 'low'}`;

            let formattedDate = 'N/A';
            if (task.dueDate) {
                // Converte a data AAAA-MM-DD para DD/MM/AAAA
                try {
                    const [year, month, day] = task.dueDate.split('-');
                    formattedDate = `${day}/${month}/${year}`;
                } catch (e) {
                    console.warn("Formato de data inválido recebido:", task.dueDate);
                }
            }

            const taskItem = document.createElement('div');
            taskItem.className = 'task-item';
            taskItem.setAttribute('data-task-id', task.id); // Armazena o ID no elemento

            // Gera o HTML do botão "Concluir" ou "Editar" condicionalmente
            const actionButton = task.status === 'PENDING' ?
                `<button class="btn btn-success btn-action-concluir" aria-label="Concluir"><i data-feather="check"></i> <span>Concluir</span></button>` :
                `<button class="btn btn-icon-only btn-action-editar" aria-label="Editar"><i data-feather="edit-2"></i></button>`;

            // Gera o HTML do item da tarefa
            taskItem.innerHTML = `
                <span class="task-title" title="${task.description || ''}">${task.title}</span>
                <span class="task-status ${statusClass}">${task.status}</span>
                <span class="task-priority ${priorityClass}">${task.priority || 'N/D'}</span>
                <span class="task-date">${formattedDate}</span>
                <div class="task-actions">
                    <button class="btn btn-secondary btn-action-visualizar">Visualizar</button>
                    ${actionButton}
                    <button class="btn btn-icon-only btn-danger btn-action-excluir" aria-label="Excluir"><i data-feather="trash-2"></i></button>
                </div>
            `;

            tasksListContainer.appendChild(taskItem);
        });

        feather.replace(); // Re-ativa os ícones do Feather
    }

    // -----------------------------------------------------------------
    // 4. HANDLERS DE EVENTOS (A Lógica da Aplicação)
    // -----------------------------------------------------------------

    // --- Lógica de Navegação Mobile (Adicionada) ---
    openBtn.addEventListener('click', () => {
        document.body.classList.add('sidebar-open');
    });

    closeBtn.addEventListener('click', () => {
        document.body.classList.remove('sidebar-open');
    });

    overlay.addEventListener('click', () => {
        document.body.classList.remove('sidebar-open');
    });

    // --- Lógica dos Modais (Corrigida) ---
    // Verifica se os elementos existem antes de adicionar eventos
    if (btnNovaTarefa && novaTarefaModal) {
        btnNovaTarefa.addEventListener('click', () => {
            novaTarefaModal.style.display = 'flex';
        });
    }
    if (closeNovaTarefa && novaTarefaModal) {
        closeNovaTarefa.addEventListener('click', () => {
            novaTarefaModal.style.display = 'none';
        });
    }

    if (btnConversaIA && modalConversaIA) {
        btnConversaIA.addEventListener('click', () => {
            modalConversaIA.style.display = 'flex';
            chatMensagens.innerHTML = ""; // Limpa o chat
            inputMsgIA.focus();
        });
    }
    if (closeConversaIA && modalConversaIA) {
        closeConversaIA.addEventListener('click', () => {
            modalConversaIA.style.display = 'none';
        });
    }

    // --- Lógica de Ações de Tarefa (Event Delegation) ---
    // Adiciona UM ouvinte de evento ao contêiner pai
    tasksListContainer.addEventListener('click', async (e) => {
        // Encontra o botão exato que foi clicado
        const button = e.target.closest('button');
        if (!button) return; // Não foi um clique em um botão

        // Encontra o item de tarefa pai para obter o ID
        const taskItem = button.closest('.task-item');
        const taskId = taskItem.dataset.taskId;

        // Ação: Concluir Tarefa
        if (button.classList.contains('btn-action-concluir')) {
            try {
                // O body precisa ser um DTO { "status": "COMPLETED" }
                await fetchApi(`/api/v1/tasks/${taskId}/status`, {
                    method: 'PATCH',
                    body: JSON.stringify({ status: 'COMPLETED' })
                });
                await loadDashboardData(); // Recarrega tudo
            } catch (error) {
                console.error('Erro ao concluir tarefa:', error.message);
                alert('Não foi possível concluir a tarefa.');
            }
        }

        // Ação: Excluir Tarefa
        if (button.classList.contains('btn-action-excluir')) {
            if (confirm('Tem certeza que deseja excluir esta tarefa?')) {
                try {
                    await fetchApi(`/api/v1/tasks/${taskId}`, {
                        method: 'DELETE'
                    });
                    await loadDashboardData(); // Recarrega tudo
                } catch (error) {
                    console.error('Erro ao excluir tarefa:', error.message);
                    alert('Não foi possível excluir a tarefa.');
                }
            }
        }

        // Ação: Editar (Implementado)
                if (button.classList.contains('btn-action-editar')) {
                    // Redireciona o navegador para a página de edição, passando o ID da tarefa na URL.
                    window.location.href = `editar-tarefa.html?id=${taskId}`;
                }

                // Ação: Visualizar (Placeholder)
                if (button.classList.contains('btn-action-visualizar')) {
                    alert(`A visualização da tarefa ${taskId} ainda não foi implementada.`);
                    // No futuro, aqui você poderia abrir um modal com os detalhes.
                }
    });

    // --- Lógica de Criação de Tarefa (Corrigida) ---
    formNovaTarefa.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(formNovaTarefa);
        const data = {
            title: formData.get('title'),
            description: formData.get('description')
            // CORRIGIDO: Removemos a prioridade, pois a IA irá defini-la
        };

        try {
            await fetchApi('/api/v1/tasks', {
                method: 'POST',
                body: JSON.stringify(data)
            });
            novaTarefaModal.style.display = 'none';
            formNovaTarefa.reset();
            await loadDashboardData(); // Recarrega tudo
        } catch (error) {
            console.error('Erro ao criar tarefa:', error.message);
            alert('Não foi possível criar a tarefa.');
        }
    });

    // --- Lógica de Busca (Melhorada) ---
    // Filtra dinamicamente enquanto o usuário digita
    inputBuscaTask.addEventListener('input', (e) => {
        const searchTerm = e.target.value.toLowerCase();

        // Filtra a lista de tarefas local (em cache)
        const filteredTasks = allTasks.filter(task =>
            task.title.toLowerCase().includes(searchTerm) ||
            (task.description && task.description.toLowerCase().includes(searchTerm))
        );

        renderTasks(filteredTasks);
        // Nota: A busca local não atualiza os cards de resumo.
    });

    // --- Lógica do Chat IA (Melhorada para usar CSS) ---
    // --- Lógica do Chat IA (ATUALIZADA COM LOADING) ---
        formConversaIA.addEventListener('submit', async (e) => {
            e.preventDefault();
            const mensagem = inputMsgIA.value;
            if (!mensagem.trim()) return;

            // 1. Adiciona a bolha do usuário
            const userBubble = document.createElement('div');
            userBubble.className = 'chat-message user-message';
            userBubble.innerHTML = mensagem.replace(/\n/g, '<br>');
            chatMensagens.appendChild(userBubble);

            // 2. Limpa o input e rola a tela
            chatMensagens.scrollTop = chatMensagens.scrollHeight;
            inputMsgIA.value = "";
            inputMsgIA.style.height = 'auto'; // Reseta altura do textarea

            // 3. (NOVO) Cria e adiciona o indicador de "digitando"
            const typingIndicator = document.createElement('div');
            typingIndicator.className = 'chat-message ia-typing'; // Usa o CSS que acabamos de adicionar
            typingIndicator.id = 'typing-indicator'; // Um ID para fácil remoção
            typingIndicator.innerHTML = '<span></span><span></span><span></span>';
            chatMensagens.appendChild(typingIndicator);

            // Rola para o novo indicador de loading
            chatMensagens.scrollTop = chatMensagens.scrollHeight;

            try {
                // 4. Chama o endpoint do GeminiController
                const response = await fetchApi('/api/v1/google-gemini/chat', {
                    method: 'POST',
                    body: JSON.stringify({ message: mensagem })
                });

                // 5. (NOVO) Remove o indicador de "digitando"
                document.getElementById('typing-indicator')?.remove(); // O '?' é uma segurança

                // 6. Adiciona a resposta real da IA
                const reply = response.content || response.reply || "Desculpe, não entendi.";

                const iaBubble = document.createElement('div');
                iaBubble.className = 'chat-message ia-message';
                iaBubble.innerHTML = reply.replace(/\n/g, '<br>');
                chatMensagens.appendChild(iaBubble);

            } catch (err) {
                // 7. (NOVO) Remove o indicador de "digitando" também em caso de erro
                document.getElementById('typing-indicator')?.remove();

                const errorBubble = document.createElement('div');
                errorBubble.className = 'chat-message error-message';
                errorBubble.textContent = 'Erro ao conectar com a IA.';
                chatMensagens.appendChild(errorBubble);
            }

            // 8. Rola para a nova mensagem (resposta ou erro)
            chatMensagens.scrollTop = chatMensagens.scrollHeight;
            inputMsgIA.focus();
        });

    // Auto-resize do textarea do chat
    inputMsgIA.addEventListener('input', function() {
        this.style.height = 'auto';
        const maxHeight = 120; // 120px (definido no CSS)
        this.style.height = (Math.min(this.scrollHeight, maxHeight)) + 'px';
    });

    // Enviar com Enter (e Shift+Enter para nova linha)
    inputMsgIA.addEventListener('keydown', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            formConversaIA.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));
        }
    });

    // -----------------------------------------------------------------
    // 5. INICIALIZAÇÃO
    // -----------------------------------------------------------------
    if (token) {
        loadUserProfile();
        loadDashboardData();
    } else {
        // Se não houver token, chuta para a tela de login
        window.location.href = 'login.html';
    }

}); // Fim do DOMContentLoaded