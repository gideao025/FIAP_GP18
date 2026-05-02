package br.com.gastrohub.entity;

import br.com.gastrohub.enums.TipoUsuarioEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoUsuarioEnum tipo;

    @Column(nullable = false)
    private LocalDateTime dataUltimaAlteracao;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    private void preencherDatasCriacao() {
        LocalDateTime dataAtual = LocalDateTime.now();
        this.dataCriacao = dataAtual;
        this.dataUltimaAlteracao = dataAtual;
    }

    @PreUpdate
    private void atualizarDataUltimaAlteracao() {
        this.dataUltimaAlteracao = LocalDateTime.now();
    }
}
