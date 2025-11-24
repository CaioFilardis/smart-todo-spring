// Conteúdo completo para login.js
$(document).ready(function() {

    // --- FUNÇÃO DE LOGIN ---
    $("#login-form").on("submit", function(e) {
        e.preventDefault(); // Impede o envio padrão

        $("#btn-login").prop("disabled", true);
        $("#login-error").addClass("d-none").text("");
        $(".is-invalid").removeClass("is-invalid");

        // Definição das variáveis
        var email = $("#email").val();
        var senha = $("#senha").val();
        let camposOk = true;

        // Validação de campos
        if (email.trim() === "") {
            $("#email").addClass("is-invalid");
            $("#email-feedback").text("Preencha o e-mail.");
            camposOk = false;
        }
        if (senha.trim() === "") {
            $("#senha").addClass("is-invalid");
            $("#senha-feedback").text("Preencha a senha.");
            camposOk = false;
        }

        if (!camposOk) {
            $("#btn-login").prop("disabled", false);
            return false; // Para a execução
        }

        // Requisição AJAX
        $.ajax({
            url: "/api/v1/auth/login",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                email: email,
                password: senha
            }),
            success: function(response) {
                // ****** ESTA É A LÓGICA CRÍTICA ******
                // Verifica se a resposta existe E se o campo accessToken existe
                if (response && response.accessToken) {
                    // SUCESSO: Salva o token real e redireciona
                    localStorage.setItem("token", response.accessToken);
                    window.location.href = "dashboard.html";
                } else {
                    // FALHA (mas 200 OK): O backend não enviou o token
                    $("#login-error").removeClass("d-none").text("Erro no login: Token não recebido.");
                    $("#btn-login").prop("disabled", false);
                }
            },
            error: function(xhr) {
                // FALHA (401 ou 500): O backend rejeitou o login
                var msg = "Erro ao autenticar. Verifique seus dados.";
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    msg = xhr.responseJSON.message; // Usa a mensagem do GlobalExceptionHandler
                }
                $("#login-error").removeClass("d-none").text(msg);
                $("#btn-login").prop("disabled", false);
            }
        });
    });

    // --- MOSTRAR/OCULTAR SENHA ---
    $("#toggleSenha").on("click", function() {
        var $senhaInput = $("#senha");
        var inputType = $senhaInput.attr("type");

        if (inputType === "password") {
            $senhaInput.attr("type", "text");
            $(this).html('&#128064;'); // Olho fechado
        } else {
            $senhaInput.attr("type", "password");
            $(this).html('&#128065;'); // Olho aberto
        }
    });
});