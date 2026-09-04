package com.crustybakery.web.dto;

public class LoginRequestDto {

    private String correo;      // antes "username" -> se autentica con la columna correo
    private String contrasena;  // antes "password" -> columna real: contrasena

    public LoginRequestDto() {
    }

    public LoginRequestDto(String correo, String contrasena) {
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
