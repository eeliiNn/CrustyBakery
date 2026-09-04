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
    public class ProductosController : ControllerBase
    {
        private readonly CrustyBakeryContext _context;
        private readonly ILogger<ProductosController> _logger;

        public ProductosController(CrustyBakeryContext context, ILogger<ProductosController> logger)
        {
            _context = context;
            _logger = logger;
        }

        // GET: api/productos?idCategoria=2&soloDisponibles=true
        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<ProductoDto>), StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<ProductoDto>>> GetProductos(
            [FromQuery] int? idCategoria, [FromQuery] bool soloDisponibles = false)
        {
            var query = _context.Productos.AsNoTracking().Include(p => p.Categoria).AsQueryable();

            if (idCategoria.HasValue)
                query = query.Where(p => p.IdCategoria == idCategoria.Value);

            if (soloDisponibles)
                query = query.Where(p => p.Disponible);

            var productos = await query.ToListAsync();
            return Ok(productos.Select(MapToDto));
        }

        // GET: api/productos/5
        [HttpGet("{id:int}")]
        [ProducesResponseType(typeof(ProductoDto), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<ProductoDto>> GetProducto(int id)
        {
            var producto = await _context.Productos
                .AsNoTracking()
                .Include(p => p.Categoria)
                .FirstOrDefaultAsync(p => p.IdProducto == id);

            if (producto == null)
                return NotFound(new { mensaje = $"No se encontró el producto con id {id}" });

            return Ok(MapToDto(producto));
        }

        // POST: api/productos
        [HttpPost]
        [ProducesResponseType(typeof(ProductoDto), StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        public async Task<ActionResult<ProductoDto>> CrearProducto(ProductoCreateDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            bool categoriaExiste = await _context.Categorias.AnyAsync(c => c.IdCategoria == dto.IdCategoria);
            if (!categoriaExiste)
                return BadRequest(new { mensaje = "La categoría indicada no existe" });

            var producto = new Producto
            {
                IdCategoria = dto.IdCategoria,
                Nombre = dto.Nombre,
                Descripcion = dto.Descripcion,
                Precio = dto.Precio,
                ImagenUrl = dto.ImagenUrl,
                Disponible = dto.Disponible
            };

            try
            {
                _context.Productos.Add(producto);
                await _context.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error al crear producto");

                return StatusCode(StatusCodes.Status500InternalServerError,
                    new
                    {
                        mensaje = "Ocurrió un error al guardar el producto",
                        error = ex.Message,
                        detalle = ex.InnerException?.Message
                    });
            }

            await _context.Entry(producto).Reference(p => p.Categoria).LoadAsync();

            return CreatedAtAction(nameof(GetProducto), new { id = producto.IdProducto }, MapToDto(producto));
        }

        // PUT: api/productos/5
        [HttpPut("{id:int}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> ActualizarProducto(int id, ProductoUpdateDto dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var producto = await _context.Productos.FindAsync(id);
            if (producto == null)
                return NotFound(new { mensaje = $"No se encontró el producto con id {id}" });

            bool categoriaExiste = await _context.Categorias.AnyAsync(c => c.IdCategoria == dto.IdCategoria);
            if (!categoriaExiste)
                return BadRequest(new { mensaje = "La categoría indicada no existe" });

            producto.IdCategoria = dto.IdCategoria;
            producto.Nombre = dto.Nombre;
            producto.Descripcion = dto.Descripcion;
            producto.Precio = dto.Precio;
            producto.ImagenUrl = dto.ImagenUrl;
            producto.Disponible = dto.Disponible;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!await _context.Productos.AnyAsync(p => p.IdProducto == id))
                    return NotFound(new { mensaje = $"No se encontró el producto con id {id}" });
                throw;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error al actualizar producto {Id}", id);
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new { mensaje = "Ocurrió un error al actualizar el producto" });
            }

            return NoContent();
        }

        // DELETE: api/productos/5
        [HttpDelete("{id:int}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status409Conflict)]
        public async Task<IActionResult> EliminarProducto(int id)
        {
            var producto = await _context.Productos.FindAsync(id);
            if (producto == null)
                return NotFound(new { mensaje = $"No se encontró el producto con id {id}" });

            bool tieneDetalles = await _context.DetallesPedido.AnyAsync(d => d.IdProducto == id);
            if (tieneDetalles)
                return Conflict(new { mensaje = "No se puede eliminar: el producto ya está incluido en pedidos. Márcalo como no disponible en su lugar." });

            try
            {
                _context.Productos.Remove(producto);
                await _context.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error al eliminar producto {Id}", id);
                return StatusCode(StatusCodes.Status500InternalServerError,
                    new { mensaje = "Ocurrió un error al eliminar el producto" });
            }

            return NoContent();
        }

        private static ProductoDto MapToDto(Producto p) => new()
        {
            IdProducto = p.IdProducto,
            IdCategoria = p.IdCategoria,
            CategoriaNombre = p.Categoria?.Nombre ?? string.Empty,
            Nombre = p.Nombre,
            Descripcion = p.Descripcion,
            Precio = p.Precio,
            ImagenUrl = p.ImagenUrl,
            Disponible = p.Disponible
        };
    }
}
