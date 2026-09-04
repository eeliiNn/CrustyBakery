package com.crustybakery.mobile.data.model

data class Cliente(
    val idCliente: Int = 0,
    val nombre: String = "",
    val correo: String = "",
    val telefono: String? = null,
    val direccion: String? = null,
    val fechaRegistro: String? = null,
    val activo: Boolean = true
)

data class ClienteLoginRequest(
    val correo: String,
    val contrasena: String
)

data class ClienteLoginResponse(
    val success: Boolean = false,
    val message: String = "",
    val cliente: Cliente? = null
)

data class ClienteCreateRequest(
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val telefono: String? = null,
    val direccion: String? = null
)

data class ClienteUpdateRequest(
    val nombre: String,
    val telefono: String? = null,
    val direccion: String? = null
)

data class Categoria(
    val idCategoria: Int = 0,
    val nombre: String = "",
    val descripcion: String? = null
)

data class Producto(
    val idProducto: Int = 0,
    val idCategoria: Int? = null,
    val nombre: String = "",
    val descripcion: String? = null,
    val precio: Double = 0.0,
    val imagen: String? = null,
    val disponible: Boolean = true,
    val categoria: Categoria? = null,
    val nombreCategoria: String? = null
)

data class PedidoCreateRequest(
    val idCliente: Int
)

data class AgregarProductoPedidoRequest(
    val idProducto: Int,
    val cantidad: Int
)

data class DetallePedido(
    val idDetallePedido: Int? = null,
    val idDetalle: Int? = null,
    val idProducto: Int = 0,
    val nombreProducto: String? = null,
    val producto: Producto? = null,
    val cantidad: Int = 0,
    val precioUnitario: Double = 0.0,
    val subtotal: Double = 0.0
)

data class Pedido(
    val idPedido: Int = 0,
    val idCliente: Int = 0,
    val cliente: Cliente? = null,
    val fechaPedido: String? = null,
    val fechaEntrega: String? = null,
    val estado: String = "",
    val total: Double = 0.0,
    val detalles: List<DetallePedido> = emptyList(),
    val nombreEmpleado: String? = null,
    val nombreRepostero: String? = null
)

data class PagoCreateRequest(
    val monto: Double,
    val metodoPago: String
)

data class Pago(
    val idPago: Int? = null,
    val idPedido: Int = 0,
    val monto: Double = 0.0,
    val metodoPago: String = "",
    val estado: String? = null,
    val estadoPago: String? = null
)

data class CartItem(
    val producto: Producto,
    val cantidad: Int
)
