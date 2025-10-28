async function carregarDashboard() {
    try {
        // Chamar API (ajuste endpoint e adicione header Authorization se usar JWT)
        const response = await fetch('/api/v1/tasks', {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        const tarefas = await response.json();

        // Contadores e cards
        document.getElementById('totalTasks').textContent = 'Total de Tarefas: ' + tarefas.length;
        const pendentes = tarefas.filter(t => t.status === 'PENDING').length;
        document.getElementById('pendingTasks').textContent = 'Tarefas Pendentes: ' + pendentes;
        const concluidas = tarefas.filter(t => t.status === 'COMPLETED').length;
        document.getElementById('completedTasks').textContent = 'Tarefas Concluídas: ' + concluidas;

        // Tabela de tarefas
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

// Garante que rode após carregar a página
window.onload = carregarDashboard;
