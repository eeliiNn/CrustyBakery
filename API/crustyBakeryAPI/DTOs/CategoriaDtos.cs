using System.ComponentModel.DataAnnotations;

namespace crustyBakeryAPI.DTOs
{

    public class CategoriaDto
    {
        public int IdCategoria { get; set; }
        public string Nombre { get; set; } = string.Empty;
        public string? Descripcion { get; set; }
    }

    public class CategoriaCreateDto
    {
        [Required(ErrorMessage = "El nombre de la categoría es obligatorio")]
        [MaxLength(80)]
        public string Nombre { get; set; } = string.Empty;

        [MaxLength(255)]
        public string? Descripcion { get; set; }
    }

    public class CategoriaUpdateDto
    {
        [Required(ErrorMessage = "El nombre de la categoría es obligatorio")]
        [MaxLength(80)]
        public string Nombre { get; set; } = string.Empty;

        [MaxLength(255)]
        public string? Descripcion { get; set; }
    }
}
