package com.crustybakery.web.service;

import com.crustybakery.web.client.ClienteApiClient;
import com.crustybakery.web.client.PedidoApiClient;
import com.crustybakery.web.client.ProductoApiClient;
import com.crustybakery.web.dto.PedidoDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final PedidoApiClient pedidoApiClient;
    private final ProductoApiClient productoApiClient;
    private final ClienteApiClient clienteApiClient;

    public DashboardService(PedidoApiClient pedidoApiClient, ProductoApiClient productoApiClient,
                             ClienteApiClient clienteApiClient) {
        this.pedidoApiClient = pedidoApiClient;
        this.productoApiClient = productoApiClient;
        this.clienteApiClient = clienteApiClient;
    }

    public Map<String, Object> resumen() {
        List<PedidoDto> pedidos = pedidoApiClient.listar();

        long totalPedidos = pedidos.size();
        long pedidosPendientes = pedidos.stream()
                .filter(p -> p.getEstado() != null && !p.getEstado().equalsIgnoreCase("ENTREGADO"))
                .count();

        BigDecimal ventasTotales = pedidos.stream()
                .map(PedidoDto::getTotal)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalProductos = productoApiClient.listar().size();
        int totalClientes = clienteApiClient.listar().size();

        return Map.of(
                "totalPedidos", totalPedidos,
                "pedidosPendientes", pedidosPendientes,
                "ventasTotales", ventasTotales,
                "totalProductos", totalProductos,
                "totalClientes", totalClientes,
                "ultimosPedidos", pedidos.stream().limit(5).toList()
        );
    }
}
