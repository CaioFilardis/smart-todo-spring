document.addEventListener('DOMContentLoaded', () => {
    // ==================================================================
    // 1. CONFIGURAÇÕES E SELETORES GLOBAIS
    // ==================================================================
    const BASE_URL = '/api/v1/tasks';
    const JWT = localStorage.getItem('token');

    // Elementos da UI (Tarefas)
    const tasksContainer = document.getElementById('tasksListContainer');
    const userNomeElement = document.getElementById('userNome');
    const userEmailElement = document.getElementById('userEmail');

    // Cards de Resumo
    const totalTasksElement = document.getElementById('totalTasks');
    const pendingTasksElement = document.getElementById('pendingTasks');
    const progressTasksElement = document.getElementById('progressTasks');
    const completedTasksElement = document.getElementById('completedTasks');

    // Modais e Forms (Tarefas)
    const modalNovaTarefa = document.getElementById('novaTarefaModal');
    const btnAbrirModal = document.getElementById('btnNovaTarefa');
    const btnFecharModal = document.getElementById('closeNovaTarefa');
    const formNovaTarefa = document.getElementById('formNovaTarefa');

    // Elementos do Chat IA (Adicionados para a solução)
    const modalChat = document.getElementById('modalConversaIA');
    const btnAbrirChat = document.getElementById('btnConversaIA');
    const btnFecharChat = document.getElementById('closeConversaIA');
    const formChat = document.getElementById('formConversaIA');
    const inputChat = document.getElementById('inputMsgIA');
    const areaMensagens = document.getElementById('chatMensagens');


    // ==================================================================
    // 2. VERIFICAÇÃO DE SEGURANÇA
    // ==================================================================
    if (!JWT) {
        window.location.href = 'login.html';
        return;
    }

    // ==================================================================
    // 3. FUNÇÕES AUXILIARES
    // ==================================================================
    const getBadgeClass = (value) => {
        if (!value) return 'badge-secondary';
        const lower = value.toLowerCase();

        const map = {
            'pending': 'status-pending',
            'in_progress': 'status-in-progress',
            'em_progresso': 'status-in-progress', // Caso venha do Enum
            'completed': 'status-completed',
            'high': 'priority-high',
            'medium': 'priority-medium',
            'low': 'priority-low'
        };
        return map[lower] || 'badge-secondary';
    };

    const formatDate = (dateString) => {
        if (!dateString) return '--/--/----';
        const date = new Date(dateString);
        return date.toLocaleDateString('pt-BR');
    };

    // Função Fetch Wrapper para incluir Headers automaticamente
    const authFetch = async (url, options = {}) => {
        const headers = {
            'Authorization': `Bearer ${JWT}`,
            'Content-Type': 'application/json',
            ...options.headers
        };

        const response = await fetch(url, { ...options, headers });

        if (response.status === 401 || response.status === 403) {
            alert('Sessão expirada. Faça login novamente.');
            localStorage.removeItem('token');
            window.location.href = 'login.html';
            return null;
        }
        return response;
    };

    // ==================================================================
    // 4. LÓGICA DE DADOS (CRUD TAREFAS)
    // ==================================================================

    // --- READ (Listar) ---
    const fetchTasks = async () => {
        try {
            const response = await authFetch(BASE_URL);
            if (!response) return;

            if (response.ok) {
                const tasks = await response.json();
                renderTasks(tasks);
                updateSummary(tasks);
            } else {
                console.error('Erro ao buscar tarefas');
            }
        } catch (error) {
            console.error('Erro de rede:', error);
            if (tasksContainer) {
                tasksContainer.innerHTML = '<p class="text-center text-danger">Erro de conexão com o servidor.</p>';
            }
        }
    };

    // --- CREATE (Criar com IA) ---
    if (formNovaTarefa) {
        formNovaTarefa.addEventListener('submit', async (e) => {
            e.preventDefault();

            const title = document.getElementById('taskTitle').value;
            const description = document.getElementById('taskDescription').value;
            const btnSubmit = formNovaTarefa.querySelector('button[type="submit"]');
            const btnOriginalText = btnSubmit.innerHTML;

            // UI Loading State
            btnSubmit.disabled = true;
            btnSubmit.innerHTML = `<span class="spinner-border spinner-border-sm"></span> Processando IA...`;

            try {
                const response = await authFetch(BASE_URL, {
                    method: 'POST',
                    body: JSON.stringify({ title, description }) // IA define prioridade e data
                });

                if (response && response.ok) {
                    modalNovaTarefa.style.display = 'none';
                    formNovaTarefa.reset();
                    await fetchTasks(); // Atualiza a lista
                } else {
                    alert('Não foi possível criar a tarefa.');
                }
            } catch (error) {
                console.error(error);
                alert('Erro ao conectar.');
            } finally {
                btnSubmit.disabled = false;
                btnSubmit.innerHTML = btnOriginalText;
            }
        });
    }

    // --- DELETE e REDIRECT EDIT (Event Delegation) ---
    if (tasksContainer) {
        tasksContainer.addEventListener('click', async (e) => {
            const btnDelete = e.target.closest('.btn-delete');
            const btnEdit = e.target.closest('.btn-edit');

            // EXCLUIR
            if (btnDelete) {
                const id = btnDelete.dataset.id;
                if (confirm('Deseja realmente excluir esta tarefa?')) {
                    const response = await authFetch(`${BASE_URL}/${id}`, { method: 'DELETE' });
                    if (response && (response.ok || response.status === 204)) {
                        await fetchTasks();
                    } else {
                        alert('Erro ao excluir tarefa.');
                    }
                }
            }

            // EDITAR (Redirecionar)
            if (btnEdit) {
                const id = btnEdit.dataset.id;
                window.location.href = `editar-tarefa.html?id=${id}`;
            }
        });
    }

    // ==================================================================
    // 5. RENDERIZAÇÃO E UI TAREFAS
    // ==================================================================

    // ==================================================================
        // 5. RENDERIZAÇÃO E UI (CORRIGIDO PARA ALINHAMENTO GRID)
        // ==================================================================
        const renderTasks = (tasks) => {
            if (!tasksContainer) return;
            tasksContainer.innerHTML = '';

            if (tasks.length === 0) {
                tasksContainer.innerHTML = `
                    <div class="empty-state">
                        <i data-feather="inbox" style="width: 48px; height: 48px; opacity: 0.5;"></i>
                        <p class="mt-3 text-secondary">Você não tem tarefas pendentes.</p>
                    </div>`;
                feather.replace();
                return;
            }

            tasks.forEach(task => {
                const el = document.createElement('div');
                el.className = 'task-item'; // Grid Container

                // Tratamento visual para status
                const statusBadge = getBadgeClass(task.status);
                const priorityBadge = getBadgeClass(task.priority);

                // NOTA: A estrutura abaixo cria 5 filhos diretos para o Grid
                el.innerHTML = `
                    <div class="col-title">
                        <strong>${task.title}</strong>
                        <span class="text-secondary small d-block text-truncate" style="max-width: 100%;">
                            ${task.description || ''}
                        </span>
                    </div>

                    <div class="col-status">
                        <span class="badge ${statusBadge}">${task.status}</span>
                    </div>

                    <div class="col-priority">
                        <span class="badge ${priorityBadge}">${task.priority || 'NORMAL'}</span>
                    </div>

                    <div class="col-date">
                        <span class="text-secondary small">
                            <i data-feather="calendar" style="width:12px; vertical-align: middle;"></i>
                            ${formatDate(task.dueDate)}
                        </span>
                    </div>

                    <div class="col-actions">
                        <button class="btn-icon btn-edit" data-id="${task.id}" title="Editar">
                            <i data-feather="edit-2" style="width: 18px; height: 18px;"></i>
                        </button>
                        <button class="btn-icon btn-delete text-danger" data-id="${task.id}" title="Excluir">
                            <i data-feather="trash-2" style="width: 18px; height: 18px;"></i>
                        </button>
                    </div>
                `;
                tasksContainer.appendChild(el);
            });
            feather.replace();
        };

    const updateSummary = (tasks) => {
        if(totalTasksElement) totalTasksElement.innerText = tasks.length;
        if(pendingTasksElement) pendingTasksElement.innerText = tasks.filter(t => t.status === 'PENDING').length;
        if(progressTasksElement) progressTasksElement.innerText = tasks.filter(t => t.status === 'IN_PROGRESS' || t.status === 'EM_PROGRESSO').length;
        if(completedTasksElement) completedTasksElement.innerText = tasks.filter(t => t.status === 'COMPLETED').length;
    };

    const loadUserProfile = async () => {
        try {
            const response = await authFetch('/api/v1/auth/me');
            if (response && response.ok) {
                const user = await response.json();
                if(userNomeElement) userNomeElement.innerText = user.fullName;
                if(userEmailElement) userEmailElement.innerText = user.email;

                // Avatar via UI Avatars
                const avatarUrl = `https://ui-avatars.com/api/?name=${encodeURIComponent(user.fullName)}&background=5865F2&color=fff`;
                const imgAvatar = document.getElementById('userAvatar');
                if(imgAvatar) imgAvatar.src = avatarUrl;
            }
        } catch (e) {
            console.error("Erro ao carregar perfil", e);
        }
    };

    // ==================================================================
    // 6. EVENTOS DE MODAL (TAREFA)
    // ==================================================================
    if (btnAbrirModal) {
        btnAbrirModal.addEventListener('click', () => {
            modalNovaTarefa.style.display = 'flex';
        });
    }

    if (btnFecharModal) {
        btnFecharModal.addEventListener('click', () => {
            modalNovaTarefa.style.display = 'none';
        });
    }

    window.addEventListener('click', (e) => {
        if (e.target === modalNovaTarefa) {
            modalNovaTarefa.style.display = 'none';
        }
    });

    // ==================================================================
    // 7. NAVEGAÇÃO SIDEBAR (Alternar Visão Geral / Minhas Tarefas)
    // ==================================================================
    const linkVisaoGeral = document.getElementById('link-visao-geral');
    const linkMinhasTarefas = document.getElementById('link-minhas-tarefas');
    const secaoResumo = document.getElementById('secao-resumo');
    const tituloPagina = document.getElementById('titulo-pagina');

    function setActiveNav(activeLink) {
        document.querySelectorAll('.sidebar-nav .nav-link').forEach(link => {
            link.classList.remove('active');
        });
        activeLink.classList.add('active');
        document.body.classList.remove('sidebar-open');
    }

    if (linkVisaoGeral) {
        linkVisaoGeral.addEventListener('click', (e) => {
            e.preventDefault();
            setActiveNav(linkVisaoGeral);
            if (secaoResumo) secaoResumo.style.display = 'grid';
            if (tituloPagina) tituloPagina.textContent = 'Visão Geral';
        });
    }

    if (linkMinhasTarefas) {
        linkMinhasTarefas.addEventListener('click', (e) => {
            e.preventDefault();
            setActiveNav(linkMinhasTarefas);
            if (secaoResumo) secaoResumo.style.display = 'none';
            if (tituloPagina) tituloPagina.textContent = 'Minhas Tarefas';
        });
    }

    // ==================================================================
    // 8. LÓGICA DO CHAT IA (RESTAURADA)
    // ==================================================================

    // Abrir Chat
    if (btnAbrirChat) {
        btnAbrirChat.addEventListener('click', () => {
            modalChat.style.display = 'flex';
            if (inputChat) inputChat.focus();
            // Mensagem de boas-vindas se vazio
            if (areaMensagens && areaMensagens.children.length === 0) {
                addMessageToChat('Olá! Sou sua assistente de produtividade. Como posso ajudar?', 'ia');
            }
        });
    }

    // Fechar Chat
    if (btnFecharChat) {
        btnFecharChat.addEventListener('click', () => {
            modalChat.style.display = 'none';
        });
    }

    // Enviar Mensagem
    if (formChat) {
        formChat.addEventListener('submit', async (e) => {
            e.preventDefault();
            const texto = inputChat.value.trim();
            if (!texto) return;

            // 1. Exibe mensagem do usuário
            addMessageToChat(texto, 'user');
            inputChat.value = '';
            inputChat.style.height = 'auto';

            // 2. Loading
            const loadingId = showTypingIndicator();
            scrollToBottom();

            try {
                // 3. Chama Backend
                const response = await authFetch('/api/v1/google-gemini/chat', {
                    method: 'POST',
                    body: JSON.stringify({ message: texto })
                });

                // Remove loading
                const loader = document.getElementById(loadingId);
                if(loader) loader.remove();

                if (response && response.ok) {
                    const data = await response.json();
                    // Resposta da IA
                    addMessageToChat(data.reply, 'ia');
                } else {
                    addMessageToChat('Erro ao processar resposta.', 'error');
                }
            } catch (error) {
                const loader = document.getElementById(loadingId);
                if(loader) loader.remove();
                addMessageToChat('Erro de conexão.', 'error');
            }
            scrollToBottom();
        });
    }

    // Helpers do Chat
    function addMessageToChat(text, type) {
        if (!areaMensagens) return;
        const div = document.createElement('div');

        if (type === 'user') div.className = 'chat-message user-message';
        else if (type === 'error') div.className = 'chat-message error-message';
        else div.className = 'chat-message ia-message';

        div.innerHTML = text.replace(/\n/g, '<br>');
        areaMensagens.appendChild(div);
    }

    function showTypingIndicator() {
        if (!areaMensagens) return null;
        const id = 'typing-' + Date.now();
        const div = document.createElement('div');
        div.className = 'chat-message ia-typing';
        div.id = id;
        div.innerHTML = '<span></span><span></span><span></span>';
        areaMensagens.appendChild(div);
        return id;
    }

    function scrollToBottom() {
        if (areaMensagens) areaMensagens.scrollTop = areaMensagens.scrollHeight;
    }

    if (inputChat) {
        inputChat.addEventListener('input', function() {
            this.style.height = 'auto';
            this.style.height = (this.scrollHeight) + 'px';
        });

        // Enviar com Enter (sem Shift)
        inputChat.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                formChat.dispatchEvent(new Event('submit'));
            }
        });
    }

    // ==================================================================
    // 9. INICIALIZAÇÃO FINAL
    // ==================================================================
    loadUserProfile();
    fetchTasks();

}); // Fim do DOMContentLoaded