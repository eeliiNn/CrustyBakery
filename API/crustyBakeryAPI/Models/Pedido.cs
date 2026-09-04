using crustyBakeryAPI.Models.Enums;
using crustyBakeryAPI.Models;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace crustyBakeryAPI.Models
{
    [Table("Pedido")]
    public class Pedido
    {
        [Key]
        public int IdPedido { get; set; }

        [Required(ErrorMessage = "El cliente es obligatorio")]
        public int IdCliente { get; set; }

        [ForeignKey(nameof(IdCliente))]
        public Cliente? Cliente { get; set; }

        public int? IdUsuarioEmpleado { get; set; }

        [ForeignKey(nameof(IdUsuarioEmpleado))]
        public Usuario? Empleado { get; set; }

        public int? IdUsuarioRepostero { get; set; }

        [ForeignKey(nameof(IdUsuarioRepostero))]
        public Usuario? Repostero { get; set; }

        public DateTime FechaPedido { get; set; } = DateTime.Now;

        public DateTime? FechaEntrega { get; set; }

        [Required]
        public EstadoPedido Estado { get; set; } = EstadoPedido.PENDIENTE;

        [Column(TypeName = "decimal(10,2)")]
        public decimal Total { get; set; } = 0;

        public ICollection<DetallePedido>? Detalles { get; set; }

        public Pago? Pago { get; set; }
    }
}