package com.crustybakery.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crustybakery.mobile.data.model.Cliente
import com.crustybakery.mobile.ui.components.*
import com.crustybakery.mobile.ui.state.UiState
import com.crustybakery.mobile.ui.theme.*
import com.crustybakery.mobile.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    cliente: Cliente,
    viewModel: ProfileViewModel,
    onSaved: (Cliente) -> Unit,
    onLogout: () -> Unit
) {
    var nombre by remember(cliente) { mutableStateOf(cliente.nombre) }
    var telefono by remember(cliente) { mutableStateOf(cliente.telefono ?: "") }
    var direccion by remember(cliente) { mutableStateOf(cliente.direccion ?: "") }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is UiState.Success) {
            onSaved((state as UiState.Success).data)
            viewModel.limpiar()
        }
    }

    Column(
        Modifier.fillMaxSize().background(CrustyBackground).verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CrustyLogo(compact = true)
            Spacer(Modifier.width(10.dp))
            ScreenTitle("Mi perfil", "Administra tus datos de entrega")
        }
        Spacer(Modifier.height(16.dp))

        Surface(color = CrustyPink, shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.Person, null, tint = CrustyBrown, modifier = Modifier.size(54.dp))
                Spacer(Modifier.height(6.dp))
                Text(cliente.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CrustyBrown)
                Text(cliente.correo, color = CrustyMuted)
            }
        }

        Spacer(Modifier.height(14.dp))
        SoftCard(Modifier.fillMaxWidth()) {
            AppTextField(nombre, { nombre = it }, "Nombre", leadingIcon = Icons.Rounded.Person)
            AppTextField(telefono, { telefono = it }, "Teléfono", leadingIcon = Icons.Rounded.Phone)
            AppTextField(direccion, { direccion = it }, "Dirección", leadingIcon = Icons.Rounded.Home)
            if (state is UiState.Error) ErrorMessage((state as UiState.Error).message)
            AppButton(
                text = if (state is UiState.Loading) "Guardando..." else "Guardar cambios",
                enabled = state !is UiState.Loading
            ) { viewModel.guardar(cliente.idCliente, nombre, telefono, direccion) }
        }

        Spacer(Modifier.height(10.dp))
        SecondaryButton("Cerrar sesión", onLogout)
        Spacer(Modifier.height(24.dp))
    }
}
