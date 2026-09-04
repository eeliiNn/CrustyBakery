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
    public class ClientesController : ControllerBase
    {
        private readonly CrustyBakeryContext _context;
        private readonly ILogger<ClientesController> _logger;

        public ClientesController(CrustyBakeryContext context, ILogger<ClientesController> logger)
        {
            _context = context;
            _logger = logger;
        }

        // GET: api/clientes
        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<ClienteDto>), StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<ClienteDto>>> GetClientes()
        {
            var clientes = await _context.Clientes.AsNoTracking().ToListAsync();
            return Ok(clientes.Select(MapToDto));
        }

        // GET: api/clientes/5
        [HttpGet("{id:int}")]
        [ProducesResponseType(typeof(ClienteDto), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<ClienteDto>> GetCliente(int id)
        {
            var cliente = await _context.Clientes
                .AsNoTracking()
                .FirstOrDefaultAsync(c => c.IdCliente == id);

            if (cliente == null)
                return NotFound(new { mensaje = $"No se encontró el cliente con id {id}" });

            return Ok(MapToDto(cliente));
        }

        // POST: api/clientes  (registro desde la app móvil)
        [HttpPost]
        [ProducesResponseType(typeof(ClienteDto), StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        public async Task<ActionResult<ClienteDto>> RegistrarCliente(ClienteCreateDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            bool correoRegistrado = await _context.Clientes.AnyAsync(c => c.Correo == dto.Correo);
            if (correoRegistrado)
                return BadRequest(new { mensaje = "Ya existe una cuenta registrada con ese correo" });

            var cliente = new Cliente
            {
                Nombre = dto.Nombre,
                Correo = dto.Correo,
                Contrasena = HashPassword(dto.Contrasena),
                Telefono = dto.Telefono,
                Direccion = dto.Direccion
            };

            try
            {
                _context.Clientes.Add(cliente);
                await _context.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error al registrar cliente");
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new { mensaje = "Ocurrió un error al registrar el cliente" });
            }

            return CreatedAtAction(nameof(GetCliente), new { id = cliente.IdCliente }, MapToDto(cliente));
        }

        // PUT: api/clientes/5
        [HttpPut("{id:int}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> ActualizarCliente(int id, ClienteUpdateDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var cliente = await _context.Clientes.FindAsync(id);
            if (cliente == null)
                return NotFound(new { mensaje = $"No se encontró el cliente con id {id}" });

            cliente.Nombre = dto.Nombre;
            cliente.Telefono = dto.Telefono;
            cliente.Direccion = dto.Direccion;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error al actualizar cliente {Id}", id);
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new { mensaje = "Ocurrió un error al actualizar el cliente" });
            }

            return NoContent();
        }

        // DELETE: api/clientes/5  (baja lógica: se conserva el historial de pedidos)
        [HttpDelete("{id:int}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> DesactivarCliente(int id)
        {
            var cliente = await _context.Clientes.FindAsync(id);
            if (cliente == null)
                return NotFound(new { mensaje = $"No se encontró el cliente con id {id}" });

            cliente.Activo = false;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error al desactivar cliente {Id}", id);
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new { mensaje = "Ocurrió un error al desactivar el cliente" });
            }

            return NoContent();
        }

        private static ClienteDto MapToDto(Cliente c) => new()
        {
            IdCliente = c.IdCliente,
            Nombre = c.Nombre,
            Correo = c.Correo,
            Telefono = c.Telefono,
            Direccion = c.Direccion,
            FechaRegistro = c.FechaRegistro,
            Activo = c.Activo
        };

        // Simplificado para el proyecto académico. En producción usa BCrypt.Net o
        // Microsoft.AspNetCore.Identity.PasswordHasher<T>, que agregan "salt" y son
        // resistentes a ataques de fuerza bruta.
        private static string HashPassword(string password)
        {
            using var sha256 = SHA256.Create();
            var bytes = sha256.ComputeHash(Encoding.UTF8.GetBytes(password));
            return Convert.ToHexString(bytes);
        }
    }
}
