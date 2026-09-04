using System.ComponentModel.DataAnnotations;
using crustyBakeryAPI.Models.Enums;

namespace crustyBakeryAPI.DTOs
{

    public class PagoDto
    {
        public int IdPago { get; set; }
        public int IdPedido { get; set; }
        public decimal Monto { get; set; }
        public DateTime FechaPago { get; set; }
        public MetodoPago MetodoPago { get; set; }
        public EstadoPago Estado { get; set; }
    }

    public class PagoCreateDto
    {
        [Required]
        [Range(0.01, double.MaxValue, ErrorMessage = "El monto debe ser mayor a 0")]
        public decimal Monto { get; set; }

        [Required(ErrorMessage = "El método de pago es obligatorio")]
        public MetodoPago MetodoPago { get; set; }
    }
}
