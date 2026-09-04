using crustyBakeryAPI.Models.Enums;
using crustyBakeryAPI.Models;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace crustyBakeryAPI.Models
{
    [Table("Pago")]
    public class Pago
    {
        [Key]
        public int IdPago { get; set; }

        [Required]
        public int IdPedido { get; set; } 

        [ForeignKey(nameof(IdPedido))]
        [JsonIgnore]
        public Pedido? Pedido { get; set; }

        [Required]
        [Column(TypeName = "decimal(10,2)")]
        [Range(0, double.MaxValue, ErrorMessage = "El monto no puede ser negativo")]
        public decimal Monto { get; set; }

        public DateTime FechaPago { get; set; } = DateTime.Now;

        [Required]
        public MetodoPago MetodoPago { get; set; }

        [Required]
        public EstadoPago Estado { get; set; } = EstadoPago.PENDIENTE;
    }
}