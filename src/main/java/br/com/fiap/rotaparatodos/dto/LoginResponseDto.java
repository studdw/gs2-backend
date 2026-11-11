package br.com.fiap.rotaparatodos.dto;

/** Saida do login: o token de sessao e os dados de quem entrou. */
public class LoginResponseDto {

    private String token;

    private UsuarioResponseDto usuario;

    public LoginResponseDto() {}

    public LoginResponseDto(String token, UsuarioResponseDto usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UsuarioResponseDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResponseDto usuario) {
        this.usuario = usuario;
    }
}
