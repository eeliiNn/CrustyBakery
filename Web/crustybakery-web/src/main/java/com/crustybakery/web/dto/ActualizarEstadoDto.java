package com.crustybakery.web.dto;

public class ActualizarEstadoDto {

    private String estado;

    // Opcional: permite asignar repostero al mismo tiempo que se cambia
    // el estado, igual que el parámetro @idUsuarioRepostero de
    // sp_actualizar_estado_pedido en la BD.
    private Integer idUsuarioRepostero;

    public ActualizarEstadoDto() {
    }

    public ActualizarEstadoDto(String estado) {
        this.estado = estado;
    }

    public ActualizarEstadoDto(String estado, Integer idUsuarioRepostero) {
        this.estado = estado;
        this.idUsuarioRepostero = idUsuarioRepostero;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getIdUsuarioRepostero() {
        return idUsuarioRepostero;
    }

    public void setIdUsuarioRepostero(Integer idUsuarioRepostero) {
        this.idUsuarioRepostero = idUsuarioRepostero;
    }
}
