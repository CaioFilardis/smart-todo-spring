$(document).ready(function(){

    // --- FUNÇÃO DE LOGIN ---
    $("#login-form").on("submit", function(e) {
        e.preventDefault();

        $("#btn-login").prop("disabled", true);
        $("#login-error").addClass("d-none").text("");
        // Limpa validações anteriores
        $(".is-invalid").removeClass("is-invalid");

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

        if (!camposOk) {
            $("#btn-login").prop("disabled", false);
            return false;
        }

        $.ajax({
            url: "/api/v1/auth/login", // Ajuste para a URL correta da sua API
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                email: email,
                password: senha // O backend espera 'password' ou 'senha'? Verifique seu DTO
            }),
            success: function(response) {
                // Supondo que o token venha na resposta
                localStorage.setItem("token", response.token);
                window.location.href = "dashboard.html";
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
            // Altera o ícone para "olho fechado"
            $(this).html('&#128064;');
        } else {
            $senhaInput.attr("type", "password");
            // Altera de volta para "olho aberto"
            $(this).html('&#128065;');
        }
    });
});