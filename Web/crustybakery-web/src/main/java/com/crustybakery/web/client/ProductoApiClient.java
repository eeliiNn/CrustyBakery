package com.crustybakery.web.client;

import com.crustybakery.web.dto.ProductoDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ProductoApiClient {

    private final RestClient restClient;
    private static final String BASE_PATH = "/api/productos";

    public ProductoApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<ProductoDto> listar() {
        return restClient.get()
                .uri(BASE_PATH)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductoDto>>() {});
    }

    public ProductoDto obtener(Integer id) {
        return restClient.get()
                .uri(BASE_PATH + "/{id}", id)
                .retrieve()
                .body(ProductoDto.class);
    }

    public ProductoDto crear(ProductoDto dto) {
        return restClient.post()
                .uri(BASE_PATH)
                .body(dto)
                .retrieve()
                .body(ProductoDto.class);
    }

    public void actualizar(Integer id, ProductoDto dto) {
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
