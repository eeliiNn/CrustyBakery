package com.crustybakery.web.service;

import com.crustybakery.web.client.PagoApiClient;
import com.crustybakery.web.client.PedidoApiClient;
import com.crustybakery.web.dto.PagoDto;
import com.crustybakery.web.dto.PedidoDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VentaService {

    private final PedidoApiClient pedidoApiClient;
    private final PagoApiClient pagoApiClient;

    public VentaService(PedidoApiClient pedidoApiClient, PagoApiClient pagoApiClient) {
        this.pedidoApiClient = pedidoApiClient;
        this.pagoApiClient = pagoApiClient;
    }

    public List<PedidoDto> listarVentas() {
        return pedidoApiClient.listar();
    }

    public List<PagoDto> listarPagos(Integer pedidoId) {
        return pagoApiClient.listarPorPedido(pedidoId);
    }

    public PagoDto registrarPago(PagoDto dto) {
        return pagoApiClient.crear(dto);
    }
}
