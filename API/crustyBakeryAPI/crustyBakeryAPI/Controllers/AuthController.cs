using crustyBakeryAPI.Data;
using crustyBakeryAPI.DTOs;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Security.Cryptography;
using System.Text;

namespace crustyBakeryAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly CrustyBakeryContext _context;
        private readonly ILogger<AuthController> _logger;

        public AuthController(CrustyBakeryContext context, ILogger<AuthController> logger)
        {
            _context = context;
            _logger = logger;
        }

        // POST: api/Auth/login
        // Responde SIEMPRE 200 OK (éxito o fallo) — el frontend en Java así lo espera,
        // y evita filtrar por código de estado si un correo existe o no.
        [HttpPost("login")]
        [ProducesResponseType(typeof(LoginResponseDto), StatusCodes.Status200OK)]
        public async Task<ActionResult<LoginResponseDto>> Login(LoginUsuarioDto dto)
        {
            if (!ModelState.IsValid)
            {
                return Ok(new LoginResponseDto
                {
                    Success = false,
                    Message = "Correo y contraseña son obligatorios"
                });
            }

            try
            {
                var usuario = await _context.Usuarios
                    .AsNoTracking()
                    .FirstOrDefaultAsync(u => u.Correo == dto.Correo);

                if (usuario == null || usuario.Contrasena != HashPassword(dto.Contrasena))
                {
                    return Ok(new LoginResponseDto
                    {
                        Success = false,
                        Message = "Correo o contraseña incorrectos"
                    });
                }

                if (!usuario.Activo)
                {
                    return Ok(new LoginResponseDto
                    {
                        Success = false,
                        Message = "El usuario está inactivo"
                    });
                }

                return Ok(new LoginResponseDto
                {
                    Success = true,
                    Usuario = new UsuarioDto
                    {
                        IdUsuario = usuario.IdUsuario,
                        Nombre = usuario.Nombre,
                        Correo = usuario.Correo,
                        Telefono = usuario.Telefono,
                        FechaRegistro = usuario.FechaRegistro,
                        Rol = usuario.Rol,
                        Activo = usuario.Activo
                    }
                });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error al iniciar sesión");
                return Ok(new LoginResponseDto
                {
                    Success = false,
                    Message = "Ocurrió un error al iniciar sesión"
                });
            }
        }

        // Debe coincidir EXACTO con el HashPassword de UsuariosController,
        // porque ahí es donde se generó el hash que se guardó al crear el usuario.
        private static string HashPassword(string password)
        {
            using var sha256 = SHA256.Create();
            var bytes = sha256.ComputeHash(Encoding.UTF8.GetBytes(password));
            return Convert.ToHexString(bytes);
        }
    }
}
