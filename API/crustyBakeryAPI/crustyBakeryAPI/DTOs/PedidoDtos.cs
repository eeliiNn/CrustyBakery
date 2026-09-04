using System.ComponentModel.DataAnnotations;
using crustyBakeryAPI.Models.Enums;

namespace crustyBakeryAPI.DTOs
{
    public class DetallePedidoDto
    {
        public int IdDetalle { get; set; }
        public int IdProducto { get; set; }
        public string ProductoNombre { get; set; } = string.Empty;
        public int Cantidad { get; set; }
        public decimal PrecioUnitario { get; set; }
        public decimal Subtotal { get; set; }
    }

    public class PedidoDto
    {
        public int IdPedido { get; set; }
        public int IdCliente { get; set; }
        public string ClienteNombre { get; set; } = string.Empty;
        public string? EmpleadoNombre { get; set; }
        public string? ReposteroNombre { get; set; }
        public DateTime FechaPedido { get; set; }
        public DateTime? FechaEntrega { get; set; }
        public EstadoPedido Estado { get; set; }
        public decimal Total { get; set; }
        public List<DetallePedidoDto> Detalles { get; set; } = new();
    }

    public class PedidoCreateDto
    {
        [Required(ErrorMessage = "El cliente es obligatorio")]
        public int IdCliente { get; set; }
    }

    public class AgregarProductoPedidoDto
    {
        [Required(ErrorMessage = "El producto es obligatorio")]
        public int IdProducto { get; set; }

        [Required]
        [Range(1, int.MaxValue, ErrorMessage = "La cantidad debe ser mayor a 0")]
        public int Cantidad { get; set; }
    }


    public class ActualizarEstadoPedidoDto
    {
        [Required(ErrorMessage = "El estado es obligatorio")]
        public EstadoPedido Estado { get; set; }

        public int? IdUsuarioRepostero { get; set; }
    }
}
