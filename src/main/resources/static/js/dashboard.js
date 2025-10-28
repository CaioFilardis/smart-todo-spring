// Após obter tarefas do backend
const pendentes = tarefas.filter(t => t.status === 'PENDING').length;
document.getElementById('pendingTasks').textContent = 'Tarefas Pendentes: ' + pendentes;
