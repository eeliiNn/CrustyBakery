package com.crustybakery.mobile.data.remote

import com.crustybakery.mobile.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/clientes/login")
    suspend fun loginCliente(
        @Body request: ClienteLoginRequest
    ): Response<ClienteLoginResponse>

    @POST("api/clientes")
    suspend fun registrarCliente(
        @Body request: ClienteCreateRequest
    ): Response<Cliente>

    @GET("api/clientes/{id}")
    suspend fun obtenerCliente(
        @Path("id") idCliente: Int
    ): Response<Cliente>

    @PUT("api/clientes/{id}")
    suspend fun actualizarCliente(
        @Path("id") idCliente: Int,
        @Body request: ClienteUpdateRequest
    ): Response<Cliente>

    @GET("api/categorias")
    suspend fun listarCategorias(): Response<List<Categoria>>

    @GET("api/productos")
    suspend fun listarProductos(
        @Query("idCategoria") idCategoria: Int? = null,
        @Query("soloDisponibles") soloDisponibles: Boolean = true
    ): Response<List<Producto>>

    @GET("api/productos/{id}")
    suspend fun obtenerProducto(
        @Path("id") idProducto: Int
    ): Response<Producto>

    @GET("api/pedidos")
    suspend fun listarPedidosCliente(
        @Query("idCliente") idCliente: Int
    ): Response<List<Pedido>>

    @GET("api/pedidos/{id}")
    suspend fun obtenerPedido(
        @Path("id") idPedido: Int
    ): Response<Pedido>

    @POST("api/pedidos")
    suspend fun crearPedido(
        @Body request: PedidoCreateRequest
    ): Response<Pedido>

    @POST("api/pedidos/{id}/productos")
    suspend fun agregarProductoPedido(
        @Path("id") idPedido: Int,
        @Body request: AgregarProductoPedidoRequest
    ): Response<Unit>

    @DELETE("api/pedidos/{id}/detalles/{detalleId}")
    suspend fun eliminarDetallePedido(
        @Path("id") idPedido: Int,
        @Path("detalleId") detalleId: Int
    ): Response<Unit>

    @DELETE("api/pedidos/{id}")
    suspend fun cancelarPedido(
        @Path("id") idPedido: Int
    ): Response<Unit>

    @GET("api/pagos/pedido/{idPedido}")
    suspend fun obtenerPago(
        @Path("idPedido") idPedido: Int
    ): Response<Pago>

    @POST("api/pagos/pedido/{idPedido}")
    suspend fun registrarPago(
        @Path("idPedido") idPedido: Int,
        @Body request: PagoCreateRequest
    ): Response<Pago>
}
