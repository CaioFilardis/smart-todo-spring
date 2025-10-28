$(document).ready(function(){
    $("#login-form").on("submit", function(e) {
        e.preventDefault();

        $("#btn-login").prop("disabled", true);
        $("#login-error").addClass("d-none").text("");

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
            url: "/api/v1/auth/login",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                email: email,
                password: senha
            }),
            success: function(response) {
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
});
