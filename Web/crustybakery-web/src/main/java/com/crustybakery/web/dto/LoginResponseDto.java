package com.crustybakery.web.dto;

/**
 * Respuesta esperada del endpoint POST /api/Auth/login que debes agregar
 * a la API en C#. Ver README.md para el detalle de implementacion.
 */
public class LoginResponseDto {

    private boolean success;
    private String message;
    private UsuarioDto usuario;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UsuarioDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDto usuario) {
        this.usuario = usuario;
    }
}
