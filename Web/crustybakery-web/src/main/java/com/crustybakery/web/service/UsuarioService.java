package com.crustybakery.web.service;

import com.crustybakery.web.client.UsuarioApiClient;
import com.crustybakery.web.dto.UsuarioCreateDto;
import com.crustybakery.web.dto.UsuarioDto;
import com.crustybakery.web.dto.UsuarioUpdateDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioApiClient usuarioApiClient;

    public UsuarioService(UsuarioApiClient usuarioApiClient) {
        this.usuarioApiClient = usuarioApiClient;
    }

    public List<UsuarioDto> listar() {
        return usuarioApiClient.listar();
    }

    public UsuarioDto obtener(Integer id) {
        return usuarioApiClient.obtener(id);
    }

    public void crear(UsuarioCreateDto dto) {
        usuarioApiClient.crear(dto);
    }

    public void actualizar(Integer id, UsuarioUpdateDto dto) {
        usuarioApiClient.actualizar(id, dto);
    }

    public void eliminar(Integer id) {
        usuarioApiClient.eliminar(id);
    }
}
