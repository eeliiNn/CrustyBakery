package com.crustybakery.web.dto;

public class UsuarioUpdateDto {

    private String nombre;
    private String telefono;
    private String rol;         // ADMINISTRADOR, EMPLEADO, REPOSTERO (según CHECK ck_usuario_rol)
    private String contrasena;  // antes "password" -> columna real: contrasena; opcional: si viene vacío, no se cambia
    private boolean activo;

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

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}