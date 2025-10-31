async function carregarDashboard() {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch('/api/v1/tasks', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        });

        if (response.status === 403) {
            window.location.href = "login.html";
            return;
        }
        const tarefas = await response.json();
        const pendentes = tarefas.filter(t => t.status === 'PENDING').length;
        const emProgresso = tarefas.filter(t => t.status === 'IN_PROGRESS').length;
        const concluidas = tarefas.filter(t => t.status === 'COMPLETED').length;

        document.getElementById('totalTasks').textContent = 'Total de Tarefas: ' + tarefas.length;
        document.getElementById('pendingTasks').textContent = 'Tarefas Pendentes: ' + pendentes;
        document.getElementById('progressTasks').textContent = 'Tarefas em Progresso: ' + emProgresso;
        document.getElementById('completedTasks').textContent = 'Tarefas Concluídas: ' + concluidas;

        const tbody = document.querySelector('#tasksTable tbody');
        tbody.innerHTML = '';
        tarefas.forEach(tarefa => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${tarefa.title}</td>
                <td>${tarefa.status}</td>
                <td>${tarefa.priority}</td>
                <td>${new Date(tarefa.createdAt).toLocaleDateString()}</td>
                <td>
                    <button class="btn-primary" data-id="${tarefa.id}">Visualizar</button>
                    <button class="btn-edit" data-id="${tarefa.id}">Editar</button>
                    ${tarefa.status == 'PENDING' ? `<button class="btn-concluir" data-id="${tarefa.id}">Concluir</button>` : ''}
                </td>
            `;
            tbody.appendChild(tr);

            // Aqui você adiciona o handler de clique APÓS adicionar o tr
            tr.querySelector('.btn-edit').onclick = function() {
                const id = this.getAttribute('data-id');
                window.location.href = `editar-tarefa.html?id=${id}`;
            };

            const btnConcluir = tr.querySelector('.btn-concluir');
            if (btnConcluir) {
                btnConcluir.onclick = async function() {
                    const id = this.getAttribute('data-id');
                    const token = localStorage.getItem('token');
                    const response = await fetch(`/api/v1/tasks/${id}/status`, {
                        method: 'PATCH',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': 'Bearer ' + token
                        },
                        body: JSON.stringify({ status: 'COMPLETED' })
                    });
                    if (response.ok) {
                        alert('Tarefa marcada como concluída!');
                        carregarDashboard();
                    } else {
                        alert('Erro ao concluir tarefa');
                    }
                };
            }

        });
    } catch (err) {
        alert('Erro ao carregar tarefas');
    }
}

// Handlers para modal e criação de tarefa
window.onload = function() {
    async function carregarPerfilUsuario() {
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.href = "login.html";
            return;
        }
        const response = await fetch('/api/v1/auth/me', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        });
        if (response.ok) {
            const usuario = await response.json();
            // Atualiza HTML do avatar/nome
            document.getElementById('userNome').textContent = usuario.fullName;
            document.getElementById('userEmail').textContent = usuario.email;
        }
    }

    carregarPerfilUsuario();

    // define handlers UMA vez só!
    document.getElementById('btnNovaTarefa').onclick = function() {
        document.getElementById('novaTarefaModal').style.display = 'flex';
    };
    document.getElementById('closeNovaTarefa').onclick = function() {
        document.getElementById('novaTarefaModal').style.display = 'none';
    };

    document.getElementById('formNovaTarefa').onsubmit = async function(e) {
        e.preventDefault();
        const token = localStorage.getItem('token');
        const form = e.target;
        const data = {
            title: form.title.value,
            description: form.description.value,
            priority: form.priority.value
        };
        const response = await fetch('/api/v1/tasks', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(data)
        });
        if (response.ok) {
            document.getElementById('novaTarefaModal').style.display = 'none';
            carregarDashboard();
        } else {
            alert('Erro ao criar tarefa');
        }
    };

    // Só então carrega dados do dashboard!
    carregarDashboard();

    document.getElementById('btnConversaIA').onclick = function() {
        document.getElementById('modalConversaIA').style.display = 'flex';
        document.getElementById('chatMensagens').innerHTML = "";
    };
    document.getElementById('closeConversaIA').onclick = function() {
        document.getElementById('modalConversaIA').style.display = 'none';
    };

    document.getElementById('formConversaIA').onsubmit = async function(e) {
        e.preventDefault();
        const token = localStorage.getItem('token');
        const mensagem = document.getElementById('inputMsgIA').value;
        const chatMensagens = document.getElementById('chatMensagens');

        // Adiciona mensagem do usuário no chat
        chatMensagens.innerHTML += `<div style="text-align:right; color:#FFD580;">Você: ${mensagem}</div>`;

        // Chama backend GeminiController
        const response = await fetch('/api/v1/gemini', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({ message: mensagem })
        });

        if (response.ok) {
            const data = await response.json();
            chatMensagens.innerHTML += `<div style="color:#6EE7B7;">IA: ${data.reply || data.content}</div>`;
        } else {
            chatMensagens.innerHTML += `<div style="color:#EF4444;">Erro na conversa com IA</div>`;
        }

        document.getElementById('inputMsgIA').value = "";
    };

};
