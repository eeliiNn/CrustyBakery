package com.crustybakery.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crustybakery.mobile.CrustyBakeryApp
import com.crustybakery.mobile.data.model.Cliente
import com.crustybakery.mobile.data.model.ClienteUpdateRequest
import com.crustybakery.mobile.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = CrustyBakeryApp.repository

    private val _state =
        MutableStateFlow<UiState<Cliente>>(UiState.Idle)
    val state: StateFlow<UiState<Cliente>> = _state.asStateFlow()

    fun guardar(
        idCliente: Int,
        nombre: String,
        telefono: String,
        direccion: String
    ) {
        if (nombre.isBlank()) {
            _state.value = UiState.Error("El nombre es obligatorio.")
            return
        }

        viewModelScope.launch {
            _state.value = UiState.Loading

            runCatching {
                repository.actualizarCliente(
                    idCliente,
                    ClienteUpdateRequest(
                        nombre = nombre.trim(),
                        telefono = telefono.trim().ifBlank { null },
                        direccion = direccion.trim().ifBlank { null }
                    )
                )
            }.onSuccess {
                _state.value = UiState.Success(it)
            }.onFailure {
                _state.value =
                    UiState.Error(it.message ?: "No se pudo actualizar el perfil.")
            }
        }
    }

    fun limpiar() {
        _state.value = UiState.Idle
    }
}
