package com.crustybakery.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crustybakery.mobile.CrustyBakeryApp
import com.crustybakery.mobile.data.model.Cliente
import com.crustybakery.mobile.data.model.ClienteCreateRequest
import com.crustybakery.mobile.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = CrustyBakeryApp.repository
    private val session = CrustyBakeryApp.sessionManager

    private val _cliente = MutableStateFlow(session.obtenerCliente())
    val cliente: StateFlow<Cliente?> = _cliente.asStateFlow()

    private val _authState = MutableStateFlow<UiState<Cliente>>(UiState.Idle)
    val authState: StateFlow<UiState<Cliente>> = _authState.asStateFlow()

    fun login(correo: String, contrasena: String) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _authState.value = UiState.Error("Ingresa correo y contraseña.")
            return
        }

        viewModelScope.launch {
            _authState.value = UiState.Loading
            runCatching {
                repository.login(correo.trim(), contrasena)
            }.onSuccess {
                session.guardarCliente(it)
                _cliente.value = it
                _authState.value = UiState.Success(it)
            }.onFailure {
                _authState.value = UiState.Error(it.message ?: "No se pudo iniciar sesión.")
            }
        }
    }

    fun registrar(
        nombre: String,
        correo: String,
        contrasena: String,
        telefono: String,
        direccion: String
    ) {
        if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            _authState.value =
                UiState.Error("Nombre, correo y contraseña son obligatorios.")
            return
        }

        viewModelScope.launch {
            _authState.value = UiState.Loading

            runCatching {
                repository.registrar(
                    ClienteCreateRequest(
                        nombre = nombre.trim(),
                        correo = correo.trim(),
                        contrasena = contrasena,
                        telefono = telefono.trim().ifBlank { null },
                        direccion = direccion.trim().ifBlank { null }
                    )
                )

                repository.login(correo.trim(), contrasena)
            }.onSuccess {
                session.guardarCliente(it)
                _cliente.value = it
                _authState.value = UiState.Success(it)
            }.onFailure {
                _authState.value =
                    UiState.Error(it.message ?: "No se pudo registrar el cliente.")
            }
        }
    }

    fun actualizarSesion(cliente: Cliente) {
        session.guardarCliente(cliente)
        _cliente.value = cliente
    }

    fun cerrarSesion() {
        session.cerrarSesion()
        _cliente.value = null
        _authState.value = UiState.Idle
    }

    fun limpiarEstado() {
        _authState.value = UiState.Idle
    }
}
