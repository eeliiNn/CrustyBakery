package com.crustybakery.web.client;

import com.crustybakery.web.dto.UsuarioCreateDto;
import com.crustybakery.web.dto.UsuarioDto;
import com.crustybakery.web.dto.UsuarioUpdateDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class UsuarioApiClient {

    private final RestClient restClient;
    private static final String BASE_PATH = "/api/usuarios";

    public UsuarioApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<UsuarioDto> listar() {
        return restClient.get()
                .uri(BASE_PATH)
                .retrieve()
                .body(new ParameterizedTypeReference<List<UsuarioDto>>() {});
    }

    public UsuarioDto obtener(Integer id) {
        return restClient.get()
                .uri(BASE_PATH + "/{id}", id)
                .retrieve()
                .body(UsuarioDto.class);
    }

    public UsuarioDto crear(UsuarioCreateDto dto) {
        return restClient.post()
                .uri(BASE_PATH)
                .body(dto)
                .retrieve()
                .body(UsuarioDto.class);
    }

    public void actualizar(Integer id, UsuarioUpdateDto dto) {
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
