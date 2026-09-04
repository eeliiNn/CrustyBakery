package com.crustybakery.web.client;

import com.crustybakery.web.dto.PagoDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Component
public class PagoApiClient {

    private final RestClient restClient;
    private static final String BASE_PATH = "/api/pagos";

    public PagoApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    // La API maneja un pago por pedido (relacion 1 a 1), no una lista.
    // Se envuelve en una lista de 0 o 1 elementos para no tener que tocar
    // VentaService/VentaController/pagos.html, que ya iteran sobre una lista.
    public List<PagoDto> listarPorPedido(Integer pedidoId) {
        try {
            PagoDto pago = restClient.get()
                    .uri(BASE_PATH + "/pedido/{pedidoId}", pedidoId)
                    .retrieve()
                    .body(PagoDto.class);
            return pago != null ? List.of(pago) : List.of();
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                return List.of();
            }
            throw ex;
        }
    }

    // El idPedido va en la URL (lo trae dto.getPedidoId(), seteado por VentaController).
    public PagoDto crear(PagoDto dto) {
        return restClient.post()
                .uri(BASE_PATH + "/pedido/{pedidoId}", dto.getPedidoId())
                .body(dto)
                .retrieve()
                .body(PagoDto.class);
    }
}
