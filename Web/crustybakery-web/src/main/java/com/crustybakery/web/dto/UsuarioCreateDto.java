package com.crustybakery.web.dto;

public class UsuarioCreateDto {

    private String correo;      // antes "username" -> columna real: correo
    private String contrasena;  // antes "password" -> columna real: contrasena
    private String nombre;
    private String telefono;
    private String rol;         // ADMINISTRADOR, EMPLEADO, REPOSTERO (según CHECK ck_usuario_rol)

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
