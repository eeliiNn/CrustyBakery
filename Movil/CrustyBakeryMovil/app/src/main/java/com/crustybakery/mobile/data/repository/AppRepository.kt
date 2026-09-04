package com.crustybakery.mobile.data.repository

import com.crustybakery.mobile.data.model.*
import com.crustybakery.mobile.data.remote.ApiService
import retrofit2.Response

class AppRepository(private val api: ApiService) {

    private suspend fun <T> unwrap(response: Response<T>): T {
        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("La API respondió sin contenido.")
        }

        val apiError = response.errorBody()?.string()
        throw Exception(
            if (!apiError.isNullOrBlank())
                "Error ${response.code()}: $apiError"
            else
                "Error HTTP ${response.code()}"
        )
    }

    private suspend fun unwrapUnit(response: Response<Unit>) {
        if (!response.isSuccessful) {
            val apiError = response.errorBody()?.string()
            throw Exception(
                if (!apiError.isNullOrBlank())
                    "Error ${response.code()}: $apiError"
                else
                    "Error HTTP ${response.code()}"
            )
        }
    }

    suspend fun login(correo: String, contrasena: String): Cliente {
        val result = unwrap(
            api.loginCliente(
                ClienteLoginRequest(
                    correo = correo,
                    contrasena = contrasena
                )
            )
        )

        if (!result.success || result.cliente == null) {
            throw Exception(result.message.ifBlank { "Credenciales incorrectas." })
        }

        return result.cliente
    }

    suspend fun registrar(request: ClienteCreateRequest): Cliente =
        unwrap(api.registrarCliente(request))

    suspend fun obtenerCliente(id: Int): Cliente =
        unwrap(api.obtenerCliente(id))

    suspend fun actualizarCliente(
        id: Int,
        request: ClienteUpdateRequest
    ): Cliente = unwrap(api.actualizarCliente(id, request))

    suspend fun categorias(): List<Categoria> =
        unwrap(api.listarCategorias())

    suspend fun productos(idCategoria: Int? = null): List<Producto> =
        unwrap(api.listarProductos(idCategoria = idCategoria, soloDisponibles = true))

    suspend fun producto(id: Int): Producto =
        unwrap(api.obtenerProducto(id))

    suspend fun pedidos(idCliente: Int): List<Pedido> =
        unwrap(api.listarPedidosCliente(idCliente))

    suspend fun pedido(idPedido: Int): Pedido =
        unwrap(api.obtenerPedido(idPedido))

    suspend fun crearPedido(idCliente: Int): Pedido =
        unwrap(api.crearPedido(PedidoCreateRequest(idCliente)))

    suspend fun agregarProducto(idPedido: Int, idProducto: Int, cantidad: Int) {
        unwrapUnit(
            api.agregarProductoPedido(
                idPedido,
                AgregarProductoPedidoRequest(idProducto, cantidad)
            )
        )
    }

    suspend fun cancelarPedido(idPedido: Int) {
        unwrapUnit(api.cancelarPedido(idPedido))
    }

    suspend fun registrarPago(
        idPedido: Int,
        monto: Double,
        metodoPago: String
    ): Pago =
        unwrap(
            api.registrarPago(
                idPedido,
                PagoCreateRequest(monto, metodoPago)
            )
        )
}
