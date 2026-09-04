package com.crustybakery.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoriaDto {

    @JsonProperty("idCategoria")
    private Integer id;
    private String nombre;
    private String descripcion;

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
