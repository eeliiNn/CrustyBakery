package com.crustybakery.web.client;

import com.crustybakery.web.dto.CategoriaDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class CategoriaApiClient {

    private final RestClient restClient;
    private static final String BASE_PATH = "/api/categorias";

    public CategoriaApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<CategoriaDto> listar() {
        return restClient.get()
                .uri(BASE_PATH)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<CategoriaDto>>() {});
    }

    public CategoriaDto obtener(Integer id) {
        return restClient.get()
                .uri(BASE_PATH + "/{id}", id)
                .retrieve()
                .body(CategoriaDto.class);
    }

    public CategoriaDto crear(CategoriaDto dto) {
        return restClient.post()
                .uri(BASE_PATH)
                .body(dto)
                .retrieve()
                .body(CategoriaDto.class);
    }

    public void actualizar(Integer id, CategoriaDto dto) {
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
