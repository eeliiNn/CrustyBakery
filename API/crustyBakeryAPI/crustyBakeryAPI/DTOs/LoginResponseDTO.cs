using crustyBakeryAPI.DTOs;

public class LoginResponseDto
{
    public bool Success { get; set; }
    public string Message { get; set; } = string.Empty;
    public UsuarioDto? Usuario { get; set; }
}