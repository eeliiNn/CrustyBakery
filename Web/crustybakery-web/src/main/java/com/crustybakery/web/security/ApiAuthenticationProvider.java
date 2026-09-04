package com.crustybakery.web.security;

import com.crustybakery.web.client.AuthApiClient;
import com.crustybakery.web.dto.LoginResponseDto;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class ApiAuthenticationProvider implements AuthenticationProvider {

    private final AuthApiClient authApiClient;

    public ApiAuthenticationProvider(AuthApiClient authApiClient) {
        this.authApiClient = authApiClient;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        LoginResponseDto respuesta = authApiClient.login(username, password);

        if (respuesta == null || !respuesta.isSuccess() || respuesta.getUsuario() == null) {
            throw new BadCredentialsException(
                    respuesta != null && respuesta.getMessage() != null
                            ? respuesta.getMessage()
                            : "Usuario o contrasena incorrectos");
        }

        CustomUserDetails userDetails = new CustomUserDetails(respuesta.getUsuario());

        if (!userDetails.isEnabled()) {
            throw new DisabledException("El usuario esta inactivo");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
