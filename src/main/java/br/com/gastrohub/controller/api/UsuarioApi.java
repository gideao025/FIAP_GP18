package br.com.gastrohub.controller.api;

import br.com.gastrohub.dto.request.*;
import br.com.gastrohub.dto.response.LoginResponse;
import br.com.gastrohub.dto.response.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema")
public interface UsuarioApi {

        @Operation(summary = "Criar usuário", description = "Cria um novo usuário do tipo DONO_RESTAURANTE, CLIENTE ou ADMIN. Endpoint público.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                        @ApiResponse(responseCode = "409", description = "Login ou email já cadastrado")
        })
        ResponseEntity<UsuarioResponse> criarUsuario(@Valid @RequestBody CriarUsuarioRequest request);

        @Operation(summary = "Buscar usuário por ID", description = "Qualquer usuário autenticado pode buscar por qualquer ID.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
        })
        ResponseEntity<UsuarioResponse> buscarUsuarioPorId(@PathVariable Long id);

        @Operation(summary = "Listar todos os usuários", description = "Qualquer usuário autenticado pode listar.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
        })
        ResponseEntity<List<UsuarioResponse>> listarTodosUsuarios();

        @Operation(summary = "Buscar usuários por nome", description = "Qualquer usuário autenticado. Busca parcial sem distinção de acento ou maiúsculas.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista de usuários encontrados")
        })
        ResponseEntity<List<UsuarioResponse>> buscarPorNome(@RequestParam(name = "nome") String nome);

        @Operation(summary = "Atualizar dados do usuário", description = "ADMIN pode atualizar qualquer usuário. DONO_RESTAURANTE e CLIENTE só atualizam o próprio.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                        @ApiResponse(responseCode = "403", description = "Sem permissão para atualizar este usuário"),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
                        @ApiResponse(responseCode = "409", description = "Email já cadastrado")
        })
        ResponseEntity<UsuarioResponse> atualizarDadosDoUsuario(
                        @PathVariable Long id,
                        @Valid @RequestBody AtualizarUsuarioRequest request);

        @Operation(summary = "Excluir usuário", description = "ADMIN pode excluir qualquer usuário. Usuário comum pode excluir apenas a própria conta.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Sem permissão para excluir este usuário"),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
        })
        ResponseEntity<Void> excluirUsuario(@PathVariable Long id);

        @Operation(summary = "Trocar senha", description = "O próprio usuário pode trocar sua senha (exige senha atual). ADMIN pode trocar a senha de qualquer usuário sem informar a senha atual.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                        @ApiResponse(responseCode = "403", description = "Sem permissão para trocar a senha deste usuário"),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
                        @ApiResponse(responseCode = "422", description = "Senha atual incorreta")
        })
        ResponseEntity<Void> trocarSenhaDoUsuario(
                        @PathVariable Long id,
                        @Valid @RequestBody TrocarSenhaRequest request);

        @Operation(summary = "Validar login", description = "Valida as credenciais do usuário e retorna um token JWT Bearer em caso de sucesso. Endpoint público.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Login validado com sucesso — retorna token JWT"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                        @ApiResponse(responseCode = "401", description = "Login ou senha incorretos")
        })
        ResponseEntity<LoginResponse> validarLogin(@Valid @RequestBody ValidarLoginRequest request);
}
