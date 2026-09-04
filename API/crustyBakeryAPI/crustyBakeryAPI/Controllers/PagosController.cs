using crustyBakeryAPI.Data;
using crustyBakeryAPI.DTOs;
using crustyBakeryAPI.Models;
using crustyBakeryAPI.Models.Enums;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace crustyBakeryAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class PagosController : ControllerBase
    {
        private readonly CrustyBakeryContext _context;
        private readonly ILogger<PagosController> _logger;

        public PagosController(CrustyBakeryContext context, ILogger<PagosController> logger)
        {
            _context = context;
            _logger = logger;
        }

        // GET: api/pagos/pedido/5
        [HttpGet("pedido/{idPedido:int}")]
        [ProducesResponseType(typeof(PagoDto), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<PagoDto>> GetPagoPorPedido(int idPedido)
        {
            var pago = await _context.Pagos
                .AsNoTracking()
                .FirstOrDefaultAsync(p => p.IdPedido == idPedido);

            if (pago == null)
                return NotFound(new { mensaje = $"El pedido {idPedido} no tiene un pago registrado" });

            return Ok(MapToDto(pago));
        }

        // POST: api/pagos/pedido/5
        [HttpPost("pedido/{idPedido:int}")]
        [ProducesResponseType(typeof(PagoDto), StatusCodes.Status201Created)]
        [ProducesResponseType(typeof(PagoDto), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<PagoDto>> RegistrarPago(int idPedido, PagoCreateDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var pedido = await _context.Pedidos.FindAsync(idPedido);
            if (pedido == null)
                return NotFound(new { mensaje = $"No se encontró el pedido con id {idPedido}" });

            var estadoPago = dto.Monto >= pedido.Total ? EstadoPago.COMPLETADO : EstadoPago.PENDIENTE;

            try
            {
                var pagoExistente = await _context.Pagos.FirstOrDefaultAsync(p => p.IdPedido == idPedido);

                if (pagoExistente != null)
                {
                    pagoExistente.Monto = dto.Monto;
                    pagoExistente.MetodoPago = dto.MetodoPago;
                    pagoExistente.Estado = estadoPago;
                    pagoExistente.FechaPago = DateTime.Now;

                    await _context.SaveChangesAsync();
                    return Ok(MapToDto(pagoExistente));
                }

                var pago = new Pago
                {
                    IdPedido = idPedido,
                    Monto = dto.Monto,
                    MetodoPago = dto.MetodoPago,
                    Estado = estadoPago
                };

                _context.Pagos.Add(pago);
                await _context.SaveChangesAsync();

                return CreatedAtAction(nameof(GetPagoPorPedido), new { idPedido }, MapToDto(pago));
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error al registrar pago del pedido {Id}", idPedido);
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new { mensaje = "Ocurrió un error al registrar el pago" });
            }
        }

        private static PagoDto MapToDto(Pago p) => new()
        {
            IdPago = p.IdPago,
            IdPedido = p.IdPedido,
            Monto = p.Monto,
            FechaPago = p.FechaPago,
            MetodoPago = p.MetodoPago,
            Estado = p.Estado
        };
    }
}
