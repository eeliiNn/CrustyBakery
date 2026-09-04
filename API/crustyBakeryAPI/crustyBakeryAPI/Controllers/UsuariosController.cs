using crustyBakeryAPI.Data;
using crustyBakeryAPI.DTOs;
using crustyBakeryAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Security.Cryptography;
using System.Text;

namespace crustyBakeryAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class UsuariosController : ControllerBase
    {
        private readonly CrustyBakeryContext _context;
        private readonly ILogger<UsuariosController> _logger;

        public UsuariosController(CrustyBakeryContext context, ILogger<UsuariosController> logger)
        {
            _context = context;
            _logger = logger;
        }

        // GET: api/usuarios
        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<UsuarioDto>), StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<UsuarioDto>>> GetUsuarios()
        {
            var usuarios = await _context.Usuarios.AsNoTracking().ToListAsync();
            return Ok(usuarios.Select(MapToDto));
        }

        // GET: api/usuarios/5
        [HttpGet("{id:int}")]
        [ProducesResponseType(typeof(UsuarioDto), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<UsuarioDto>> GetUsuario(int id)
        {
            var usuario = await _context.Usuarios
                .AsNoTracking()
                .FirstOrDefaultAsync(u => u.IdUsuario == id);

            if (usuario == null)
                return NotFound(new { mensaje = $"No se encontró el usuario con id {id}" });

            return Ok(MapToDto(usuario));
        }

        // POST: api/usuarios  (lo crea el Administrador)
        [HttpPost]
        [ProducesResponseType(typeof(UsuarioDto), StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        public async Task<ActionResult<UsuarioDto>> CrearUsuario(UsuarioCreateDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            bool correoRegistrado = await _context.Usuarios.AnyAsync(u => u.Correo == dto.Correo);
            if (correoRegistrado)
                return BadRequest(new { mensaje = "Ya existe un usuario registrado con ese correo" });

            var usuario = new Usuario
            {
                Nombre = dto.Nombre,
                Correo = dto.Correo,
                Contrasena = HashPassword(dto.Contrasena),
                Telefono = dto.Telefono,
                Rol = dto.Rol
            };

            try
            {
                _context.Usuarios.Add(usuario);
                await _context.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error al crear usuario");
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new { mensaje = "Ocurrió un error al crear el usuario" });
            }

            return CreatedAtAction(nameof(GetUsuario), new { id = usuario.IdUsuario }, MapToDto(usuario));
        }

        // PUT: api/usuarios/5
        [HttpPut("{id:int}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> ActualizarUsuario(int id, UsuarioUpdateDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var usuario = await _context.Usuarios.FindAsync(id);
            if (usuario == null)
                return NotFound(new { mensaje = $"No se encontró el usuario con id {id}" });

            usuario.Nombre = dto.Nombre;
            usuario.Telefono = dto.Telefono;
            usuario.Rol = dto.Rol;
            usuario.Activo = dto.Activo;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error al actualizar usuario {Id}", id);
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new { mensaje = "Ocurrió un error al actualizar el usuario" });
            }

            return NoContent();
        }

        // DELETE: api/usuarios/5  (baja lógica: se conserva el historial de pedidos)
        [HttpDelete("{id:int}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> DesactivarUsuario(int id)
        {
            var usuario = await _context.Usuarios.FindAsync(id);
            if (usuario == null)
                return NotFound(new { mensaje = $"No se encontró el usuario con id {id}" });

            usuario.Activo = false;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error al desactivar usuario {Id}", id);
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new { mensaje = "Ocurrió un error al desactivar el usuario" });
            }

            return NoContent();
        }

        private static UsuarioDto MapToDto(Usuario u) => new()
        {
            IdUsuario = u.IdUsuario,
            Nombre = u.Nombre,
            Correo = u.Correo,
            Telefono = u.Telefono,
            FechaRegistro = u.FechaRegistro,
            Rol = u.Rol,
            Activo = u.Activo
        };

        private static string HashPassword(string password)
        {
            return BCrypt.Net.BCrypt.HashPassword(password);
        }
    }
}
