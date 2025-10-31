window.onload = async function() {
    const idTarefa = new URLSearchParams(window.location.search).get('id');
    const token = localStorage.getItem('token');
    // Buscar dados atuais da tarefa
    const response = await fetch(`/api/v1/tasks/${idTarefa}`, {
        method: 'GET',
        headers: { 'Authorization': 'Bearer ' + token }
    });
    if (response.ok) {
        const tarefa = await response.json();
        document.getElementById('title').value = tarefa.title;
        document.getElementById('description').value = tarefa.description;
        document.getElementById('priority').value = tarefa.priority;
    }
    // Atualizar tarefa
    document.getElementById('formEditarTarefa').onsubmit = async function(e) {
        e.preventDefault();
        const dados = {
            title: document.getElementById('title').value,
            description: document.getElementById('description').value,
            priority: document.getElementById('priority').value
        };
        const respUpdate = await fetch(`/api/v1/tasks/${idTarefa}`, {
            method: 'PUT', // ou PATCH, depende de seu backend!
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(dados)
        });
        if (respUpdate.ok) {
            alert('Tarefa atualizada com sucesso!');
            window.location.href = "dashboard.html";
        } else {
            alert('Erro ao atualizar tarefa');
        }
    }
};
