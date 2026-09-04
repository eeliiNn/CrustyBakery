using System.ComponentModel.DataAnnotations;
using  crustyBakeryAPI.Models.Enums;

namespace crustyBakeryAPI.DTOs
{
    public class UsuarioDto
    {
        public int IdUsuario { get; set; }
        public string Nombre { get; set; } = string.Empty;
        public string Correo { get; set; } = string.Empty;
        public string? Telefono { get; set; }
        public DateTime FechaRegistro { get; set; }
        public RolUsuario Rol { get; set; }
        public bool Activo { get; set; }
    }
    public class UsuarioCreateDto
    {
        [Required(ErrorMessage = "El nombre es obligatorio")]
        [MaxLength(100)]
        public string Nombre { get; set; } = string.Empty;

        [Required(ErrorMessage = "El correo es obligatorio")]
        [EmailAddress(ErrorMessage = "El correo no tiene un formato válido")]
        [MaxLength(150)]
        public string Correo { get; set; } = string.Empty;

        [Required(ErrorMessage = "La contraseña es obligatoria")]
        [MinLength(8, ErrorMessage = "La contraseña debe tener al menos 8 caracteres")]
        public string Contrasena { get; set; } = string.Empty;

        [MaxLength(20)]
        public string? Telefono { get; set; }

        [Required(ErrorMessage = "El rol es obligatorio")]
        public RolUsuario Rol { get; set; }
    }

    // Lo que se recibe al actualizar (PUT /api/usuarios/{id})
    // No incluye Correo ni Contrasena: para esos casos conviene un endpoint aparte
    // (ej. /api/usuarios/{id}/cambiar-contrasena) por seguridad.
    public class UsuarioUpdateDto
    {
        [Required(ErrorMessage = "El nombre es obligatorio")]
        [MaxLength(100)]
        public string Nombre { get; set; } = string.Empty;

        [MaxLength(20)]
        public string? Telefono { get; set; }

        [Required(ErrorMessage = "El rol es obligatorio")]
        public RolUsuario Rol { get; set; }

        public bool Activo { get; set; }
    }

    public class LoginUsuarioDto
    {
        [Required]
        [EmailAddress]
        public string Correo { get; set; } = string.Empty;

        [Required]
        public string Contrasena { get; set; } = string.Empty;
    }
}
