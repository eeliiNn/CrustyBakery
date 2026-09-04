# Crustybakery Web (frontend Spring MVC + Thymeleaf)

Aplicacion web en Java/Spring que consume la API REST de Crustybakery (backend en C#).
No tiene base de datos propia: todo el CRUD y la autenticacion se hacen contra tu API existente.

## Requisitos

- Java 17+
- Maven 3.8+
- Tu API de Crustybakery corriendo (por defecto se asume `https://localhost:5244`)


## Como correrlo

Ejecutar en -> CrustybakeryWebApplication.java

La app queda disponible en `http://localhost:8081`.

## Roles y permisos

La app maneja 3 roles, tomados del campo `rol` del `UsuarioDto`:

| Rol        | Acceso                                                        |
|------------|-----------------------------------------------------------------|
| ADMIN      | Todo: categorias, productos, clientes, pedidos, ventas, usuarios |
| VENDEDOR   | Clientes, pedidos, ventas                                       |
| REPOSTERO  | Categorias, productos                                            |

  lo minimo en `AuthApiClient`. Para produccion conviene agregar un
  `@ControllerAdvice` que traduzca `RestClientResponseException` en paginas de error
  amigables.
