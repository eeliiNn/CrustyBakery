package com.crustybakery.web.service;

import com.crustybakery.web.client.ClienteApiClient;
import com.crustybakery.web.client.PedidoApiClient;
import com.crustybakery.web.client.ProductoApiClient;
import com.crustybakery.web.dto.ActualizarEstadoDto;
import com.crustybakery.web.dto.ClienteDto;
import com.crustybakery.web.dto.DetallePedidoDto;
import com.crustybakery.web.dto.PedidoDto;
import com.crustybakery.web.dto.ProductoDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoApiClient pedidoApiClient;
    private final ClienteApiClient clienteApiClient;
    private final ProductoApiClient productoApiClient;

    public PedidoService(PedidoApiClient pedidoApiClient, ClienteApiClient clienteApiClient,
                          ProductoApiClient productoApiClient) {
        this.pedidoApiClient = pedidoApiClient;
        this.clienteApiClient = clienteApiClient;
        this.productoApiClient = productoApiClient;
    }

    public List<PedidoDto> listar() {
        return pedidoApiClient.listar();
    }

    public PedidoDto obtener(Integer id) {
        return pedidoApiClient.obtener(id);
    }

    public List<ClienteDto> listarClientes() {
        return clienteApiClient.listar();
    }

    public List<ProductoDto> listarProductos() {
        return productoApiClient.listar();
    }

    public PedidoDto crear(PedidoDto dto) {
        return pedidoApiClient.crear(dto);
    }

    public void eliminar(Integer id) {
        pedidoApiClient.eliminar(id);
    }

    public void agregarDetalle(Integer pedidoId, DetallePedidoDto detalle) {
        pedidoApiClient.agregarDetalle(pedidoId, detalle);
    }

    public void eliminarDetalle(Integer pedidoId, Integer detalleId) {
        pedidoApiClient.eliminarDetalle(pedidoId, detalleId);
    }

    public void cambiarEstado(Integer pedidoId, String estado) {
        pedidoApiClient.actualizarEstado(pedidoId, new ActualizarEstadoDto(estado));
    }
}
