using crustyBakeryAPI.Data;
using crustyBakeryAPI.DTOs;
using crustyBakeryAPI.Models;
using crustyBakeryAPI.Models.Enums;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using crustyBakeryAPI.Models;
using crustyBakeryAPI.Models.Enums;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace crustyBakeryAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class PedidosController : ControllerBase
    {
        private readonly CrustyBakeryContext _context;
        private readonly ILogger<PedidosController> _logger;

        public PedidosController(CrustyBakeryContext context, ILogger<PedidosController> logger)
        {
            _context = context;
            _logger = logger;
        }

        // GET: api/pedidos?idCliente=3&estado=PENDIENTE
        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<PedidoDto>), StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<PedidoDto>>> GetPedidos(
            [FromQuery] int? idCliente, [FromQuery] EstadoPedido? estado)
        {
            var query = _context.Pedidos
                .AsNoTracking()
                .Include(p => p.Cliente)
                .Include(p => p.Empleado)
                .Include(p => p.Repostero)
                .Include(p => p.Detalles).ThenInclude(d => d.Producto)
                .AsQueryable();

            if (idCliente.HasValue)
                query = query.Where(p => p.IdCliente == idCliente.Value);

            if (estado.HasValue)
                query = query.Where(p => p.Estado == estado.Value);

            var pedidos = await query.OrderByDescending(p => p.FechaPedido).ToListAsync();
            return Ok(pedidos.Select(MapToDto));
        }

        // GET: api/pedidos/5
        [HttpGet("{id:int}")]
        [ProducesResponseType(typeof(PedidoDto), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<PedidoDto>> GetPedido(int id)
        {
            var pedido = await _context.Pedidos
                .AsNoTracking()
                .Include(p => p.Cliente)
                .Include(p => p.Empleado)
                .Include(p => p.Repostero)
                .Include(p => p.Detalles).ThenInclude(d => d.Producto)
                .FirstOrDefaultAsync(p => p.IdPedido == id);

            if (pedido == null)
                return NotFound(new { mensaje = $"No se encontró el pedido con id {id}" });

            return Ok(MapToDto(pedido));
        }

        // POST: api/pedidos  (el cliente inicia un pedido vacío)
        [HttpPost]
        [ProducesResponseType(typeof(PedidoDto), StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        public async Task<ActionResult<PedidoDto>> CrearPedido(PedidoCreateDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            bool clienteExiste = await _context.Clientes.AnyAsync(c => c.IdCliente == dto.IdCliente);
            if (!clienteExiste)
                return BadRequest(new { mensaje = "El cliente indicado no existe" });

            var pedido = new Pedido
            {
                IdCliente = dto.IdCliente,
                Estado = EstadoPedido.PENDIENTE,
                Total = 0
            };

            try
            {
                _context.Pedidos.Add(pedido);
                await _context.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error al crear pedido");
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new { mensaje = "Ocurrió un error al crear el pedido" });
            }

            await _context.Entry(pedido).Reference(p => p.Cliente).LoadAsync();

            return CreatedAtAction(nameof(GetPedido), new { id = pedido.IdPedido }, MapToDto(pedido));
        }

        // POST: api/pedidos/5/productos
        [HttpPost("{id:int}/productos")]
        [ProducesResponseType(typeof(PedidoDto), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<PedidoDto>> AgregarProducto(int id, AgregarProductoPedidoDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var pedido = await _context.Pedidos.FindAsync(id);
            if (pedido == null)
                return NotFound(new { mensaje = $"No se encontró el pedido con id {id}" });

            if (pedido.Estado != EstadoPedido.PENDIENTE)
                return BadRequest(new { mensaje = "Solo se pueden agregar productos a un pedido en estado PENDIENTE" });

            var producto = await _context.Productos.FindAsync(dto.IdProducto);
            if (producto == null || !producto.Disponible)
                return BadRequest(new { mensaje = "El producto no existe o no está disponible" });

            var detalle = new DetallePedido
            {
                IdPedido = id,
                IdProducto = dto.IdProducto,
                Cantidad = dto.Cantidad,
                PrecioUnitario = producto.Precio
            };

            try
            {
                _context.DetallesPedido.Add(detalle);
                await _context.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error al agregar producto al pedido {Id}", id);
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new { mensaje = "Ocurrió un error al agregar el producto" });
            }

            // El total lo recalcula el trigger de la base de datos (trg_detalle_after_insert);
            // se vuelve a consultar el pedido para devolver el total ya actualizado.
            var pedidoActualizado = await _context.Pedidos
                .AsNoTracking()
                .Include(p => p.Cliente)
                .Include(p => p.Empleado)
                .Include(p => p.Repostero)
                .Include(p => p.Detalles).ThenInclude(d => d.Producto)
                .FirstAsync(p => p.IdPedido == id);

            return Ok(MapToDto(pedidoActualizado));
        }

        // DELETE: api/pedidos/5/detalles/3  (quita una línea de producto del pedido)
        [HttpDelete("{id:int}/detalles/{detalleId:int}")]
        [ProducesResponseType(typeof(PedidoDto), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<PedidoDto>> EliminarDetalle(int id, int detalleId)
        {
            var pedido = await _context.Pedidos.FindAsync(id);
            if (pedido == null)
                return NotFound(new { mensaje = $"No se encontró el pedido con id {id}" });

            if (pedido.Estado != EstadoPedido.PENDIENTE)
                return BadRequest(new { mensaje = "Solo se pueden quitar productos de un pedido en estado PENDIENTE" });

            var detalle = await _context.DetallesPedido
                .FirstOrDefaultAsync(d => d.IdDetalle == detalleId && d.IdPedido == id);

            if (detalle == null)
                return NotFound(new { mensaje = $"No se encontró el detalle {detalleId} en este pedido" });

            try
            {
                _context.DetallesPedido.Remove(detalle);
                await _context.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error al quitar el detalle {DetalleId} del pedido {Id}", detalleId, id);
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new { mensaje = "Ocurrió un error al quitar el producto del pedido" });
            }

            // El total lo recalcula el trigger de la base de datos (trg_detalle_after_delete).
            var pedidoActualizado = await _context.Pedidos
                .AsNoTracking()
                .Include(p => p.Cliente)
                .Include(p => p.Empleado)
                .Include(p => p.Repostero)
                .Include(p => p.Detalles).ThenInclude(d => d.Producto)
                .FirstAsync(p => p.IdPedido == id);

            return Ok(MapToDto(pedidoActualizado));
        }

        // PATCH: api/pedidos/5/estado
        [HttpPatch("{id:int}/estado")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> ActualizarEstado(int id, ActualizarEstadoPedidoDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var pedido = await _context.Pedidos.FindAsync(id);
            if (pedido == null)
                return NotFound(new { mensaje = $"No se encontró el pedido con id {id}" });

            if (dto.IdUsuarioRepostero.HasValue)
            {
                bool reposteroValido = await _context.Usuarios.AnyAsync(
                    u => u.IdUsuario == dto.IdUsuarioRepostero.Value && u.Rol == RolUsuario.REPOSTERO);

                if (!reposteroValido)
                    return BadRequest(new { mensaje = "El repostero indicado no existe" });

                pedido.IdUsuarioRepostero = dto.IdUsuarioRepostero.Value;
            }

            pedido.Estado = dto.Estado;

            if (dto.Estado == EstadoPedido.ENTREGADO)
                pedido.FechaEntrega = DateTime.Now;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error al actualizar estado del pedido {Id}", id);
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new { mensaje = "Ocurrió un error al actualizar el estado del pedido" });
            }

            return NoContent();
        }

        // DELETE: api/pedidos/5  (cancela el pedido, no lo elimina físicamente)
        [HttpDelete("{id:int}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status409Conflict)]
        public async Task<IActionResult> CancelarPedido(int id)
        {
            var pedido = await _context.Pedidos.FindAsync(id);
            if (pedido == null)
                return NotFound(new { mensaje = $"No se encontró el pedido con id {id}" });

            if (pedido.Estado == EstadoPedido.ENTREGADO)
                return Conflict(new { mensaje = "No se puede cancelar un pedido ya entregado" });

            pedido.Estado = EstadoPedido.CANCELADO;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error al cancelar pedido {Id}", id);
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new { mensaje = "Ocurrió un error al cancelar el pedido" });
            }

            return NoContent();
        }

        private static PedidoDto MapToDto(Pedido p) => new()
        {
            IdPedido = p.IdPedido,
            IdCliente = p.IdCliente,
            ClienteNombre = p.Cliente?.Nombre ?? string.Empty,
            EmpleadoNombre = p.Empleado?.Nombre,
            ReposteroNombre = p.Repostero?.Nombre,
            FechaPedido = p.FechaPedido,
            FechaEntrega = p.FechaEntrega,
            Estado = p.Estado,
            Total = p.Total,
            Detalles = p.Detalles?.Select(d => new DetallePedidoDto
            {
                IdDetalle = d.IdDetalle,
                IdProducto = d.IdProducto,
                ProductoNombre = d.Producto?.Nombre ?? string.Empty,
                Cantidad = d.Cantidad,
                PrecioUnitario = d.PrecioUnitario,
                Subtotal = d.Subtotal
            }).ToList() ?? new List<DetallePedidoDto>()
        };
    }
}