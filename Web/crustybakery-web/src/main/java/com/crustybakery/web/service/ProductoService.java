package com.crustybakery.web.service;

import com.crustybakery.web.client.CategoriaApiClient;
import com.crustybakery.web.client.ProductoApiClient;
import com.crustybakery.web.dto.CategoriaDto;
import com.crustybakery.web.dto.ProductoDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoApiClient productoApiClient;
    private final CategoriaApiClient categoriaApiClient;

    public ProductoService(ProductoApiClient productoApiClient, CategoriaApiClient categoriaApiClient) {
        this.productoApiClient = productoApiClient;
        this.categoriaApiClient = categoriaApiClient;
    }

    public List<ProductoDto> listar() {
        return productoApiClient.listar();
    }

    public ProductoDto obtener(Integer id) {
        return productoApiClient.obtener(id);
    }

    public List<CategoriaDto> listarCategorias() {
        return categoriaApiClient.listar();
    }

    public void guardar(ProductoDto dto) {
        if (dto.getId() == null) {
            productoApiClient.crear(dto);
        } else {
            productoApiClient.actualizar(dto.getId(), dto);
        }
    }

    public void eliminar(Integer id) {
        productoApiClient.eliminar(id);
    }
}
