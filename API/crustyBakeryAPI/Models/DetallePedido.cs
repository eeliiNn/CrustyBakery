using crustyBakeryAPI.Models;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace crustyBakeryAPI.Models
{
    [Table("DetallePedido")]
    public class DetallePedido
    {
        [Key]
        public int IdDetalle { get; set; }

        [Required]
        public int IdPedido { get; set; }

        [ForeignKey(nameof(IdPedido))]
        [JsonIgnore] 
        public Pedido? Pedido { get; set; }

        [Required(ErrorMessage = "El producto es obligatorio")]
        public int IdProducto { get; set; }

        [ForeignKey(nameof(IdProducto))]
        public Producto? Producto { get; set; }

        [Required]
        [Range(1, int.MaxValue, ErrorMessage = "La cantidad debe ser mayor a 0")]
        public int Cantidad { get; set; }

        [Required]
        [Column(TypeName = "decimal(10,2)")]
        [Range(0, double.MaxValue, ErrorMessage = "El precio unitario no puede ser negativo")]
        public decimal PrecioUnitario { get; set; }


        [DatabaseGenerated(DatabaseGeneratedOption.Computed)]
        [Column(TypeName = "decimal(10,2)")]
        public decimal Subtotal { get; private set; }
    }
}