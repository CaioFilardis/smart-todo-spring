package com.projetoprionyx.smart_todo.api.security.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        // Gera uma chave segura para o algoritmo HS256
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        // Codifica a chave para Base64
        String secretString = Encoders.BASE64.encode(key.getEncoded());

        System.out.println("Sua nova chave Base64 é:");
        System.out.println(secretString);
    }
}
