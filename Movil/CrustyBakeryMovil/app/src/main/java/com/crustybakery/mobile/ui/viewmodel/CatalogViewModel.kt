package com.crustybakery.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crustybakery.mobile.CrustyBakeryApp
import com.crustybakery.mobile.data.model.Categoria
import com.crustybakery.mobile.data.model.Producto
import com.crustybakery.mobile.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogViewModel : ViewModel() {

    private val repository = CrustyBakeryApp.repository

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    private val _productos =
        MutableStateFlow<UiState<List<Producto>>>(UiState.Loading)
    val productos: StateFlow<UiState<List<Producto>>> = _productos.asStateFlow()

    private val _categoriaSeleccionada = MutableStateFlow<Int?>(null)
    val categoriaSeleccionada: StateFlow<Int?> = _categoriaSeleccionada.asStateFlow()

    init {
        cargarCategorias()
        cargarProductos()
    }

    fun cargarCategorias() {
        viewModelScope.launch {
            runCatching { repository.categorias() }
                .onSuccess { _categorias.value = it }
        }
    }

    fun cargarProductos(idCategoria: Int? = _categoriaSeleccionada.value) {
        viewModelScope.launch {
            _productos.value = UiState.Loading
            runCatching { repository.productos(idCategoria) }
                .onSuccess { _productos.value = UiState.Success(it) }
                .onFailure {
                    _productos.value =
                        UiState.Error(it.message ?: "No se pudo cargar el catálogo.")
                }
        }
    }

    fun seleccionarCategoria(idCategoria: Int?) {
        _categoriaSeleccionada.value = idCategoria
        cargarProductos(idCategoria)
    }
}
