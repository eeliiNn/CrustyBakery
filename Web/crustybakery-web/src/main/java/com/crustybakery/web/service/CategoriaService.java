package com.crustybakery.web.service;

import com.crustybakery.web.client.CategoriaApiClient;
import com.crustybakery.web.dto.CategoriaDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaApiClient categoriaApiClient;

    public CategoriaService(CategoriaApiClient categoriaApiClient) {
        this.categoriaApiClient = categoriaApiClient;
    }

    public List<CategoriaDto> listar() {
        return categoriaApiClient.listar();
    }

    public CategoriaDto obtener(Integer id) {
        return categoriaApiClient.obtener(id);
    }

    public void guardar(CategoriaDto dto) {
        if (dto.getId() == null) {
            categoriaApiClient.crear(dto);
        } else {
            categoriaApiClient.actualizar(dto.getId(), dto);
        }
    }

    public void eliminar(Integer id) {
        categoriaApiClient.eliminar(id);
    }
}
