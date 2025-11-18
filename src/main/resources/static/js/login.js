$(document).ready(function(){

    // --- FUNÇÃO DE LOGIN (CORRIGIDA) ---
    $("#login-form").on("submit", function(e) {
        e.preventDefault(); // Impede o envio padrão do formulário

        $("#btn-login").prop("disabled", true);
        $("#login-error").addClass("d-none").text("");
        $(".is-invalid").removeClass("is-invalid");

        // --- ESTAS LINHAS ESTAVAM FALTANDO ---
        var email = $("#email").val();
        var senha = $("#senha").val();
        let camposOk = true;

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
        // --- FIM DAS LINHAS QUE FALTAVAM ---

        if (!camposOk) {
            $("#btn-login").prop("disabled", false);
            return false; // Para a execução se os campos estiverem vazios
        }

        // Agora a requisição AJAX será chamada corretamente
        $.ajax({
            url: "/api/v1/auth/login",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                email: email,    // 'email' agora existe
                password: senha  // 'senha' agora existe
            }),
            success: function(response) {
                if (response && response.accessToken) {
                    localStorage.setItem("token", response.accessToken);
                    window.location.href = "dashboard.html";
                } else {
                    $("#login-error").removeClass("d-none").text("Erro no login: Token não recebido.");
                }
            },
            error: function(xhr) {
                var msg = "Erro ao autenticar. Verifique seus dados.";
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    msg = xhr.responseJSON.message;
                }
                $("#login-error").removeClass("d-none").text(msg);
            },
            complete: function() {
                $("#btn-login").prop("disabled", false);
            }
        });
    });

    // --- NOVO BLOCO: MOSTRAR/OCULTAR SENHA ---
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