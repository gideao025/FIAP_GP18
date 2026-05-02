package br.com.gastrohub.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.segredo}")
    private String segredo;

    @Value("${jwt.expiracao-em-minutos}")
    private long expiracaoEmMinutos;

    public String gerarToken(String login) {
        Date agora = new Date();
        Date expiracao = calcularExpiracao(agora);

        return Jwts.builder()
                .subject(login)
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(gerarChaveSecreta())
                .compact();
    }

    public String extrairLogin(String token) {
        return extrairClaims(token).getSubject();
    }

    public boolean tokenValido(String token, UserDetails userDetails) {
        String login = extrairLogin(token);
        return login.equals(userDetails.getUsername()) && !tokenExpirado(token);
    }

    public LocalDateTime calcularDataExpiracao() {
        return LocalDateTime.now().plusMinutes(expiracaoEmMinutos);
    }

    private boolean tokenExpirado(String token) {
        return extrairClaims(token).getExpiration().before(new Date());
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(gerarChaveSecreta())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey gerarChaveSecreta() {
        return Keys.hmacShaKeyFor(segredo.getBytes());
    }

    private Date calcularExpiracao(Date agora) {
        long expiracaoEmMs = expiracaoEmMinutos * 60 * 1000;
        return new Date(agora.getTime() + expiracaoEmMs);
    }
}
