# Crustybakery Web (frontend Spring MVC + Thymeleaf)

Aplicacion web en Java/Spring que consume la API REST de Crustybakery (backend en C#).
No tiene base de datos propia: todo el CRUD y la autenticacion se hacen contra tu API existente.

## Requisitos

- Java 17+
- Maven 3.8+
- Tu API de Crustybakery corriendo (por defecto se asume `https://localhost:5001`)

## Configuracion

Edita `src/main/resources/application.properties`:

```properties
api.base-url=https://localhost:5001
```

Cambialo por la URL real donde corre tu API en C#.

Si tu API usa un certificado HTTPS autofirmado en desarrollo, tendras que:
- Usar HTTP en desarrollo, o
- Confiar en el certificado en el JDK que uses para correr esta app.

## Como correrlo

```bash
mvn spring-boot:run
```

La app queda disponible en `http://localhost:8081`.

## ⚠️ IMPORTANTE: endpoint que hay que agregar a la API en C#

Este frontend delega toda la autenticacion en tu API. Como `UsuarioDto` (el que ya
devuelven tus endpoints existentes) **no expone la contrasena** -y esta bien que sea
asi-, este lado en Java no puede validar credenciales por su cuenta. Por eso el login
se hace llamando a un endpoint nuevo que **debes crear en tu API C#**:

```
POST /api/Auth/login
```

**Request body:**

```json
{
  "username": "jperez",
  "password": "texto-plano-que-escribio-el-usuario"
}
```

**Response body esperado (200 OK, tanto en exito como en fallo):**

```json
{
  "success": true,
  "message": null,
  "usuario": {
    "id": 1,
    "username": "jperez",
    "nombre": "Juan Perez",
    "rol": "VENDEDOR",
    "activo": true
  }
}
```

En caso de credenciales invalidas:

```json
{
  "success": false,
  "message": "Usuario o contrasena incorrectos",
  "usuario": null
}
```

### Implementacion sugerida en C# (ASP.NET Core)

```csharp
[ApiController]
[Route("api/[controller]")]
public class AuthController : ControllerBase
{
    private readonly IUsuarioRepository _usuarios;

    public AuthController(IUsuarioRepository usuarios)
    {
        _usuarios = usuarios;
    }

    [HttpPost("login")]
    public async Task<ActionResult<LoginResponse>> Login([FromBody] LoginRequest request)
    {
        var usuario = await _usuarios.ObtenerPorUsername(request.Username);

        if (usuario == null || !VerificarPassword(request.Password, usuario.PasswordHash))
        {
            return Ok(new LoginResponse
            {
                Success = false,
                Message = "Usuario o contrasena incorrectos"
            });
        }

        if (!usuario.Activo)
        {
            return Ok(new LoginResponse
            {
                Success = false,
                Message = "El usuario esta inactivo"
            });
        }

        return Ok(new LoginResponse
        {
            Success = true,
            Usuario = new UsuarioDto
            {
                Id = usuario.Id,
                Username = usuario.Username,
                Nombre = usuario.Nombre,
                Rol = usuario.Rol,
                Activo = usuario.Activo
            }
        });
    }

    private bool VerificarPassword(string plano, string hash)
    {
        // Usa el mismo mecanismo de hashing que ya usas para crear usuarios
        // (BCrypt, PBKDF2, Identity, etc.)
        return BCrypt.Net.BCrypt.Verify(plano, hash);
    }
}

public class LoginRequest
{
    public string Username { get; set; }
    public string Password { get; set; }
}

public class LoginResponse
{
    public bool Success { get; set; }
    public string Message { get; set; }
    public UsuarioDto Usuario { get; set; }
}
```

Puntos clave:
- **Nunca** devuelvas el password ni el hash en `UsuarioDto`.
- Este endpoint deberia quedar excluido de cualquier autenticacion basada en token que
  ya tengas (es el punto de entrada), pero si tu API ya usa JWT en todo lo demas, aqui
  es donde normalmente tambien generarias y devolverias el token — este README asume
  el escenario simple de sesion basada en cookie de Spring Security, sin JWT.

## Roles y permisos

La app maneja 3 roles, tomados del campo `rol` del `UsuarioDto`:

| Rol        | Acceso                                                        |
|------------|-----------------------------------------------------------------|
| ADMIN      | Todo: categorias, productos, clientes, pedidos, ventas, usuarios |
| VENDEDOR   | Clientes, pedidos, ventas                                       |
| REPOSTERO  | Categorias, productos                                            |

Las reglas estan centralizadas en `config/SecurityConfig.java`. Ajusta ahi si tu
esquema de permisos es distinto.

## Estructura del proyecto

```
src/main/java/com/crustybakery/web/
├── CrustybakeryWebApplication.java
├── config/          -> ApiProperties, RestClientConfig, SecurityConfig
├── security/        -> CustomUserDetails, ApiAuthenticationProvider
├── dto/             -> DTOs que reflejan los de tu API
├── client/          -> un ApiClient por recurso (Auth, Categoria, Cliente, Producto, Pedido, Pago, Usuario)
├── service/         -> logica de orquestacion entre controllers y ApiClients
└── controller/      -> Auth, Dashboard, Categoria, Producto, Cliente, Pedido, Usuario, Venta

src/main/resources/
├── application.properties
├── templates/       -> vistas Thymeleaf, organizadas por modulo
└── static/css/styles.css
```

## Endpoints de la API asumidos

Ademas del nuevo `/api/Auth/login`, los ApiClient llaman a:

- `GET/POST/PUT/DELETE /api/Categoria[/{id}]`
- `GET/POST/PUT/DELETE /api/Cliente[/{id}]`
- `GET/POST/PUT/DELETE /api/Producto[/{id}]`
- `GET/POST/DELETE /api/Pedido[/{id}]`
- `POST /api/Pedido/{id}/detalles`
- `DELETE /api/Pedido/{id}/detalles/{detalleId}`
- `PUT /api/Pedido/{id}/estado`
- `GET/POST /api/Pago`, `GET /api/Pago/pedido/{pedidoId}`
- `GET/POST/PUT/DELETE /api/Usuario[/{id}]`

Si algun path o verbo no coincide exactamente con tu API real, ajustalo en el
`ApiClient` correspondiente dentro de `client/` (son clases pequenas, cada endpoint
esta en un solo metodo).

## Estilos

`static/css/styles.css` esta vacio a proposito. Las plantillas ya usan un set de
clases consistente (`.navbar`, `.btn`, `.table`, `.card`, `.form-group`, etc.) — el
comentario dentro del archivo lista todas para que armes tu propio estilo sin tener
que tocar el HTML.

## Notas / limitaciones conocidas

- La sesion se maneja con `formLogin` clasico de Spring Security (cookie de sesion),
  no con JWT. Si tu API en C# tiene otros endpoints protegidos con JWT, tendras que
  extender `RestClientConfig`/`ApiAuthenticationProvider` para propagar un token.
- Los DTOs asumen nombres de campo en base a lo descrito en la solicitud original;
  si tu API usa otros nombres (ej. `nombreCompleto` en vez de `nombre`), ajusta el DTO
  correspondiente en `dto/`.
- No hay manejo centralizado de errores de la API (timeouts, 500, etc.) mas alla de
  lo minimo en `AuthApiClient`. Para produccion conviene agregar un
  `@ControllerAdvice` que traduzca `RestClientResponseException` en paginas de error
  amigables.
