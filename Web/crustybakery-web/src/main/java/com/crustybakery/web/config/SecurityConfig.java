package com.crustybakery.web.config;

import com.crustybakery.web.security.ApiAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ApiAuthenticationProvider apiAuthenticationProvider;

    public SecurityConfig(ApiAuthenticationProvider apiAuthenticationProvider) {
        this.apiAuthenticationProvider = apiAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(apiAuthenticationProvider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**", "/auth/login").permitAll()
                .requestMatchers("/dashboard/**").authenticated()

                // Categorias y Productos: ADMIN y REPOSTERO gestionan catalogo
                .requestMatchers("/categorias/**", "/productos/**").hasAnyRole("ADMINISTRADOR", "REPOSTERO")

                // Clientes y Pedidos: ADMINISTRADOR y EMPLEADO trabajan con ventas
                .requestMatchers("/clientes/**", "/pedidos/**", "/ventas/**").hasAnyRole("ADMINISTRADOR", "EMPLEADO")

                // Usuarios: solo ADMIN
                .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureHandler(authenticationFailureHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
            );

        return http.build();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        SimpleUrlAuthenticationFailureHandler handler = new SimpleUrlAuthenticationFailureHandler("/auth/login?error");
        return handler;
    }
}
