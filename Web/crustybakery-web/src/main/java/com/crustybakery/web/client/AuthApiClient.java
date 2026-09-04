package com.crustybakery.web.client;

import com.crustybakery.web.dto.LoginRequestDto;
import com.crustybakery.web.dto.LoginResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class AuthApiClient {

    private final RestClient restClient;

    public AuthApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Llama a POST /api/Auth/login en la API de C#.
     * El backend espera Correo/Contrasena (LoginUsuarioDto), no username/password.
     * El endpoint SIEMPRE responde 200 OK, incluso con credenciales inválidas
     * (Success=false + Message). El catch solo cubre caídas reales de la API.
     */
    public LoginResponseDto login(String correo, String contrasena) {
        try {
            return restClient.post()
                    .uri("/api/Auth/login")
                    .body(new LoginRequestDto(correo, contrasena))
                    .retrieve()
                    .body(LoginResponseDto.class);
        } catch (RestClientResponseException ex) {
            LoginResponseDto respuesta = new LoginResponseDto();
            respuesta.setSuccess(false);
            respuesta.setMessage("No se pudo conectar con el servidor. Intenta más tarde.");
            return respuesta;
        }
    }
}