package com.crustybakery.web.client;

import com.crustybakery.web.dto.ClienteDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ClienteApiClient {

    private final RestClient restClient;
    private static final String BASE_PATH = "/api/clientes";

    public ClienteApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<ClienteDto> listar() {
        return restClient.get()
                .uri(BASE_PATH)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ClienteDto>>() {});
    }

    public ClienteDto obtener(Integer id) {
        return restClient.get()
                .uri(BASE_PATH + "/{id}", id)
                .retrieve()
                .body(ClienteDto.class);
    }

    public ClienteDto crear(ClienteDto dto) {
        return restClient.post()
                .uri(BASE_PATH)
                .body(dto)
                .retrieve()
                .body(ClienteDto.class);
    }

    public void actualizar(Integer id, ClienteDto dto) {
        restClient.put()
                .uri(BASE_PATH + "/{id}", id)
                .body(dto)
                .retrieve()
                .toBodilessEntity();
    }

    public void eliminar(Integer id) {
        restClient.delete()
                .uri(BASE_PATH + "/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
