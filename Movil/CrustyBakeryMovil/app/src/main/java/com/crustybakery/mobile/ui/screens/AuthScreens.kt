package com.crustybakery.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crustybakery.mobile.ui.components.*
import com.crustybakery.mobile.ui.state.UiState
import com.crustybakery.mobile.ui.theme.*
import com.crustybakery.mobile.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onRegister: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val state by viewModel.authState.collectAsState()

    LaunchedEffect(state) {
        if (state is UiState.Success) {
            onLoginSuccess()
            viewModel.limpiarEstado()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CrustyBackground)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CrustyLogo()
            Spacer(Modifier.height(14.dp))
            Text(
                "Bienvenida a Crusty",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = CrustyBrown
            )
            Text(
                "Pasteles, detalles y antojos en un solo lugar",
                style = MaterialTheme.typography.bodyMedium,
                color = CrustyMuted
            )
            Spacer(Modifier.height(24.dp))

            Surface(
                shape = RoundedCornerShape(28.dp),
                color = CrustySurface,
                tonalElevation = 1.dp,
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {
                    AppTextField(correo, { correo = it }, "Correo", leadingIcon = Icons.Rounded.Email)
                    AppTextField(contrasena, { contrasena = it }, "Contraseña", password = true, leadingIcon = Icons.Rounded.Lock)

                    if (state is UiState.Error) ErrorMessage((state as UiState.Error).message)

                    if (state is UiState.Loading) {
                        Loading()
                    } else {
                        AppButton("Iniciar sesión") { viewModel.login(correo, contrasena) }
                    }
                    SecondaryButton("Crear una cuenta", onRegister)
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onSuccess: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    val state by viewModel.authState.collectAsState()

    LaunchedEffect(state) {
        if (state is UiState.Success) {
            onSuccess()
            viewModel.limpiarEstado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CrustyBackground)
            .verticalScroll(rememberScrollState())
            .padding(22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CrustyLogo(compact = true)
            Spacer(Modifier.width(12.dp))
            ScreenTitle("Crear cuenta", "Guarda tus datos para pedir más rápido")
        }
        Spacer(Modifier.height(18.dp))

        SoftCard(Modifier.fillMaxWidth()) {
            AppTextField(nombre, { nombre = it }, "Nombre", leadingIcon = Icons.Rounded.Person)
            AppTextField(correo, { correo = it }, "Correo", leadingIcon = Icons.Rounded.Email)
            AppTextField(contrasena, { contrasena = it }, "Contraseña", password = true, leadingIcon = Icons.Rounded.Lock)
            AppTextField(telefono, { telefono = it }, "Teléfono", leadingIcon = Icons.Rounded.Phone)
            AppTextField(direccion, { direccion = it }, "Dirección", leadingIcon = Icons.Rounded.Home)

            if (state is UiState.Error) ErrorMessage((state as UiState.Error).message)

            AppButton(
                text = if (state is UiState.Loading) "Registrando..." else "Registrarme",
                enabled = state !is UiState.Loading
            ) {
                viewModel.registrar(nombre, correo, contrasena, telefono, direccion)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
