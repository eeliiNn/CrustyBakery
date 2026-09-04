package com.crustybakery.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClienteDto {

    @JsonProperty("idCliente")
    private Integer id;
    private String nombre;
    private String correo;
    private String telefono;
    private String direccion;

    // Solo se usa al crear (la API la exige, minimo 8 caracteres).
    // En las respuestas de la API nunca viene, asi que siempre llega null al leer.
    private String contrasena;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
