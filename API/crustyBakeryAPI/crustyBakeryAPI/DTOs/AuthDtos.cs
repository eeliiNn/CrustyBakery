using crustyBakeryAPI.DTOs;

namespace crustyBakeryAPI.DTOs
{
    // Respuesta del login. SIEMPRE se devuelve 200 OK (éxito o fallo),
    // tal como lo espera el AuthApiClient del proyecto web en Java.
    public class LoginResponseDto
    {
        public bool Success { get; set; }
        public string? Message { get; set; }
        public UsuarioDto? Usuario { get; set; }
    }
}
