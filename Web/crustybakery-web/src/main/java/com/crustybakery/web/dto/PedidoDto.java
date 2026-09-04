package com.crustybakery.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoDto {

    @JsonProperty("idPedido")
    private Integer id;

    @JsonProperty("idCliente")
    private Integer clienteId;
    private String clienteNombre;
    private String empleadoNombre;
    private String reposteroNombre;
    private LocalDateTime fechaPedido;
    private LocalDateTime fechaEntrega;
    private String estado;
    private BigDecimal total;
    private List<DetallePedidoDto> detalles;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getEmpleadoNombre() {
        return empleadoNombre;
    }

    public void setEmpleadoNombre(String empleadoNombre) {
        this.empleadoNombre = empleadoNombre;
    }

    public String getReposteroNombre() {
        return reposteroNombre;
    }

    public void setReposteroNombre(String reposteroNombre) {
        this.reposteroNombre = reposteroNombre;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<DetallePedidoDto> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedidoDto> detalles) {
        this.detalles = detalles;
    }
}
