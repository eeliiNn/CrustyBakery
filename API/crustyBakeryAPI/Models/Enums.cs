namespace crustyBakeryAPI.Models.Enums
{
    // Coincide con el CHECK de usuario.rol
    public enum RolUsuario
    {
        ADMINISTRADOR,
        EMPLEADO,
        REPOSTERO
    }

    // Coincide con el CHECK de pedido.estado
    public enum EstadoPedido
    {
        PENDIENTE,
        EN_PREPARACION,
        LISTO,
        ENTREGADO,
        CANCELADO
    }

    // Coincide con el CHECK de pago.metodoPago
    public enum MetodoPago
    {
        EFECTIVO,
        TARJETA,
        TRANSFERENCIA
    }

    // Coincide con el CHECK de pago.estado
    public enum EstadoPago
    {
        PENDIENTE,
        COMPLETADO,
        RECHAZADO
    }
}