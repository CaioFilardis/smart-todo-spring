async function carregarDashboard() {
    try {
        const response = await fetch('/api/v1/tasks', {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
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
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        alert('Erro ao carregar tarefas');
    }
}

window.onload = carregarDashboard;
