package com.crustybakery.web.service;

import com.crustybakery.web.client.ClienteApiClient;
import com.crustybakery.web.dto.ClienteDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteApiClient clienteApiClient;

    public ClienteService(ClienteApiClient clienteApiClient) {
        this.clienteApiClient = clienteApiClient;
    }

    public List<ClienteDto> listar() {
        return clienteApiClient.listar();
    }

    public ClienteDto obtener(Integer id) {
        return clienteApiClient.obtener(id);
    }

    public void guardar(ClienteDto dto) {
        if (dto.getId() == null) {
            clienteApiClient.crear(dto);
        } else {
            clienteApiClient.actualizar(dto.getId(), dto);
        }
    }

    public void eliminar(Integer id) {
        clienteApiClient.eliminar(id);
    }
}
