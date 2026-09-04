package com.crustybakery.web.client;

import com.crustybakery.web.dto.ActualizarEstadoDto;
import com.crustybakery.web.dto.DetallePedidoDto;
import com.crustybakery.web.dto.PedidoDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class PedidoApiClient {

    private final RestClient restClient;
    private static final String BASE_PATH = "/api/pedidos";

    public PedidoApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<PedidoDto> listar() {
        return restClient.get()
                .uri(BASE_PATH)
                .retrieve()
                .body(new ParameterizedTypeReference<List<PedidoDto>>() {});
    }

    public PedidoDto obtener(Integer id) {
        return restClient.get()
                .uri(BASE_PATH + "/{id}", id)
                .retrieve()
                .body(PedidoDto.class);
    }

    public PedidoDto crear(PedidoDto dto) {
        return restClient.post()
                .uri(BASE_PATH)
                .body(dto)
                .retrieve()
                .body(PedidoDto.class);
    }

    public void eliminar(Integer id) {
        restClient.delete()
                .uri(BASE_PATH + "/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }

    // La API espera { idProducto, cantidad } en POST /api/pedidos/{id}/productos.
    // DetallePedidoDto trae mas campos (id, productoNombre, etc.) pero la API
    // simplemente ignora las propiedades que no reconoce.
    public void agregarDetalle(Integer pedidoId, DetallePedidoDto detalle) {
        restClient.post()
                .uri(BASE_PATH + "/{id}/productos", pedidoId)
                .body(detalle)
                .retrieve()
                .toBodilessEntity();
    }

    public void eliminarDetalle(Integer pedidoId, Integer detalleId) {
        restClient.delete()
                .uri(BASE_PATH + "/{id}/detalles/{detalleId}", pedidoId, detalleId)
                .retrieve()
                .toBodilessEntity();
    }

    // La API expone este endpoint como PATCH, no PUT.
    public void actualizarEstado(Integer pedidoId, ActualizarEstadoDto estado) {
        restClient.patch()
                .uri(BASE_PATH + "/{id}/estado", pedidoId)
                .body(estado)
                .retrieve()
                .toBodilessEntity();
    }
}
