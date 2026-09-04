package com.crustybakery.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crustybakery.mobile.CrustyBakeryApp
import com.crustybakery.mobile.data.model.CartItem
import com.crustybakery.mobile.data.model.Pedido
import com.crustybakery.mobile.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrdersViewModel : ViewModel() {

    private val repository = CrustyBakeryApp.repository

    private val _pedidos =
        MutableStateFlow<UiState<List<Pedido>>>(UiState.Idle)
    val pedidos: StateFlow<UiState<List<Pedido>>> = _pedidos.asStateFlow()

    private val _pedidoActual =
        MutableStateFlow<UiState<Pedido>>(UiState.Idle)
    val pedidoActual: StateFlow<UiState<Pedido>> = _pedidoActual.asStateFlow()

    private val _checkout =
        MutableStateFlow<UiState<Pedido>>(UiState.Idle)
    val checkout: StateFlow<UiState<Pedido>> = _checkout.asStateFlow()

    fun cargarPedidos(idCliente: Int) {
        viewModelScope.launch {
            _pedidos.value = UiState.Loading
            runCatching { repository.pedidos(idCliente) }
                .onSuccess { _pedidos.value = UiState.Success(it) }
                .onFailure {
                    _pedidos.value =
                        UiState.Error(it.message ?: "No se pudieron cargar los pedidos.")
                }
        }
    }

    fun cargarPedido(idPedido: Int) {
        viewModelScope.launch {
            _pedidoActual.value = UiState.Loading
            runCatching { repository.pedido(idPedido) }
                .onSuccess { _pedidoActual.value = UiState.Success(it) }
                .onFailure {
                    _pedidoActual.value =
                        UiState.Error(it.message ?: "No se pudo cargar el pedido.")
                }
        }
    }

    fun finalizarCompra(
        idCliente: Int,
        items: List<CartItem>,
        metodoPago: String
    ) {
        if (items.isEmpty()) {
            _checkout.value = UiState.Error("El carrito está vacío.")
            return
        }

        viewModelScope.launch {
            _checkout.value = UiState.Loading

            runCatching {
                val creado = repository.crearPedido(idCliente)

                items.forEach { item ->
                    repository.agregarProducto(
                        idPedido = creado.idPedido,
                        idProducto = item.producto.idProducto,
                        cantidad = item.cantidad
                    )
                }

                // Tu trigger de SQL actualiza el total al insertar detalles.
                val actualizado = repository.pedido(creado.idPedido)

                repository.registrarPago(
                    idPedido = creado.idPedido,
                    monto = actualizado.total,
                    metodoPago = metodoPago.ifBlank { "EFECTIVO" }
                )

                repository.pedido(creado.idPedido)
            }.onSuccess {
                _checkout.value = UiState.Success(it)
            }.onFailure {
                _checkout.value =
                    UiState.Error(it.message ?: "No se pudo crear el pedido.")
            }
        }
    }

    fun cancelarPedido(idPedido: Int) {
        viewModelScope.launch {
            runCatching {
                repository.cancelarPedido(idPedido)
                repository.pedido(idPedido)
            }.onSuccess {
                _pedidoActual.value = UiState.Success(it)
            }.onFailure {
                _pedidoActual.value =
                    UiState.Error(it.message ?: "No se pudo cancelar el pedido.")
            }
        }
    }

    fun limpiarCheckout() {
        _checkout.value = UiState.Idle
    }
}
