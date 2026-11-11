package br.com.fiap.rotaparatodos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

/**
 * Gera e confere o token de sessao.
 * O token tem o formato "idDoUsuario.validade.assinatura". A assinatura e um HMAC-SHA256
 * feito com um segredo que so o servidor conhece, entao ninguem consegue inventar um token.
 */
@Service
public class TokenService {

    private static final long SEGUNDOS_VALIDADE = 12 * 3600; // 12 horas

    private byte[] segredo;

    // O segredo vem da variavel de ambiente APP_TOKEN_SECRET (application.properties)
    public TokenService(@Value("${app.token.segredo}") String segredo) {
        if (segredo == null || segredo.isBlank()) {
            // Sem segredo configurado: gera um aleatorio (os tokens deixam de valer se a API reiniciar)
            this.segredo = new byte[32];
            new SecureRandom().nextBytes(this.segredo);
        } else {
            this.segredo = segredo.getBytes(StandardCharsets.UTF_8);
        }
    }

    public String gerar(int idUsuario) {
        long validade = Instant.now().getEpochSecond() + SEGUNDOS_VALIDADE;
        String conteudo = idUsuario + "." + validade;
        return conteudo + "." + assinar(conteudo);
    }

    // Devolve o id do usuario se o token for autentico e estiver na validade; senao devolve 0
    public int validar(String token) {
        if (token == null)
            return 0;
        String[] partes = token.split("\\.");
        if (partes.length != 3)
            return 0;
        try {
            String conteudo = partes[0] + "." + partes[1];
            boolean assinaturaConfere = MessageDigest.isEqual(
                    assinar(conteudo).getBytes(StandardCharsets.UTF_8),
                    partes[2].getBytes(StandardCharsets.UTF_8));
            boolean dentroDaValidade = Long.parseLong(partes[1]) >= Instant.now().getEpochSecond();
            if (assinaturaConfere && dentroDaValidade)
                return Integer.parseInt(partes[0]);
            return 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String assinar(String conteudo) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(segredo, "HmacSHA256"));
            byte[] assinatura = mac.doFinal(conteudo.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(assinatura);
        } catch (Exception e) {
            throw new IllegalStateException("Nao foi possivel assinar o token", e);
        }
    }
}
