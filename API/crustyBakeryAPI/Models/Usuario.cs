using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;
using crustyBakeryAPI.Models.Enums;

namespace crustyBakeryAPI.Models
{
    [Table("Usuario")]
    public class Usuario
    {
        [Key]
        public int IdUsuario { get; set; }

        [Required(ErrorMessage = "El nombre es obligatorio")]
        [MaxLength(100)]
        public string Nombre { get; set; } = string.Empty;

        [Required(ErrorMessage = "El correo es obligatorio")]
        [EmailAddress(ErrorMessage = "El correo no tiene un formato válido")]
        [MaxLength(150)]
        public string Correo { get; set; } = string.Empty;

        [Required]
        [MaxLength(255)]
        [JsonIgnore]
        public string Contrasena { get; set; } = string.Empty;

        [MaxLength(20)]
        public string? Telefono { get; set; }

        public DateTime FechaRegistro { get; set; } = DateTime.Now;

        [Required]
        public RolUsuario Rol { get; set; }

        public bool Activo { get; set; } = true;

        [JsonIgnore]
        public ICollection<Pedido>? PedidosGestionados { get; set; }

        [JsonIgnore]
        public ICollection<Pedido>? PedidosPreparados { get; set; }
    }
}