package com.crustybakery.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.crustybakery.mobile.data.model.CartItem
import com.crustybakery.mobile.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartViewModel : ViewModel() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    fun agregar(producto: Producto, cantidad: Int = 1) {
        if (!producto.disponible || cantidad <= 0) return

        val current = _items.value.toMutableList()
        val index = current.indexOfFirst {
            it.producto.idProducto == producto.idProducto
        }

        if (index >= 0) {
            val item = current[index]
            current[index] = item.copy(cantidad = item.cantidad + cantidad)
        } else {
            current += CartItem(producto, cantidad)
        }

        _items.value = current
    }

    fun cambiarCantidad(idProducto: Int, cantidad: Int) {
        if (cantidad <= 0) {
            eliminar(idProducto)
            return
        }

        _items.value = _items.value.map {
            if (it.producto.idProducto == idProducto)
                it.copy(cantidad = cantidad)
            else it
        }
    }

    fun eliminar(idProducto: Int) {
        _items.value = _items.value.filterNot {
            it.producto.idProducto == idProducto
        }
    }

    fun limpiar() {
        _items.value = emptyList()
    }

    fun total(): Double =
        _items.value.sumOf { it.producto.precio * it.cantidad }
}
