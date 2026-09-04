package com.crustybakery.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
import com.crustybakery.mobile.ui.viewmodel.CartViewModel
import com.crustybakery.mobile.ui.viewmodel.OrdersViewModel
import java.util.Locale

private fun moneyCart(value: Double) = String.format(Locale.US, "$%.2f", value)

@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    onCheckout: () -> Unit
) {
    val cartItems by cartViewModel.items.collectAsState()

    Column(Modifier.fillMaxSize().background(CrustyBackground).padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CrustyLogo(compact = true)
            Spacer(Modifier.width(10.dp))
            ScreenTitle("Tu carrito", "Revisa tus productos antes de continuar")
        }
        Spacer(Modifier.height(14.dp))

        if (cartItems.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.ShoppingCart, null, tint = CrustyPink, modifier = Modifier.size(74.dp))
                    Spacer(Modifier.height(10.dp))
                    Text("Tu carrito está vacío", fontWeight = FontWeight.Bold, color = CrustyBrown)
                    Text("Agrega algo rico desde el catálogo", color = CrustyMuted)
                }
            }
            return@Column
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(cartItems, key = { it.producto.idProducto }) { item ->
                SoftCard(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(58.dp).background(CrustyBlush, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Rounded.Cake, null, tint = CrustyBrown) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(item.producto.nombre, fontWeight = FontWeight.Bold, color = CrustyBrown)
                            Text("${moneyCart(item.producto.precio)} c/u", color = CrustyMuted)
                        }
                        IconButton(onClick = { cartViewModel.eliminar(item.producto.idProducto) }) {
                            Icon(Icons.Rounded.DeleteOutline, "Eliminar", tint = CrustyMuted)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledTonalIconButton(onClick = {
                            cartViewModel.cambiarCantidad(item.producto.idProducto, item.cantidad - 1)
                        }) { Icon(Icons.Rounded.Remove, null) }
                        Text("${item.cantidad}", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold)
                        FilledTonalIconButton(onClick = {
                            cartViewModel.cambiarCantidad(item.producto.idProducto, item.cantidad + 1)
                        }) { Icon(Icons.Rounded.Add, null) }
                        Spacer(Modifier.weight(1f))
                        Text(moneyCart(item.producto.precio * item.cantidad), fontWeight = FontWeight.ExtraBold, color = CrustyBrown)
                    }
                }
            }
        }

        Surface(shape = RoundedCornerShape(22.dp), color = CrustyCream, modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CrustyBrown)
                Spacer(Modifier.weight(1f))
                Text(moneyCart(cartViewModel.total()), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = CrustyBrown)
            }
        }
        Spacer(Modifier.height(8.dp))
        AppButton("Continuar con el pedido", onClick = onCheckout)
    }
}

@Composable
fun CheckoutScreen(
    cliente: Cliente,
    cartViewModel: CartViewModel,
    ordersViewModel: OrdersViewModel,
    onSuccess: (Int) -> Unit
) {
    val items by cartViewModel.items.collectAsState()
    val checkout by ordersViewModel.checkout.collectAsState()
    var metodoPago by remember { mutableStateOf("EFECTIVO") }

    LaunchedEffect(checkout) {
        if (checkout is UiState.Success) {
            val pedido = (checkout as UiState.Success).data
            cartViewModel.limpiar()
            ordersViewModel.limpiarCheckout()
            onSuccess(pedido.idPedido)
        }
    }

    Column(Modifier.fillMaxSize().background(CrustyBackground).padding(20.dp)) {
        ScreenTitle("Finalizar pedido", "Confirma los datos de tu compra")
        Spacer(Modifier.height(18.dp))

        SoftCard(Modifier.fillMaxWidth()) {
            Text("Resumen", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CrustyBrown)
            Spacer(Modifier.height(8.dp))
            items.forEach {
                Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                    Text("${it.cantidad} × ${it.producto.nombre}", modifier = Modifier.weight(1f), color = CrustyMuted)
                    Text(moneyCart(it.producto.precio * it.cantidad), fontWeight = FontWeight.SemiBold)
                }
            }
            HorizontalDivider(Modifier.padding(vertical = 10.dp), color = CrustySoftBrown)
            Row(Modifier.fillMaxWidth()) {
                Text("Total", fontWeight = FontWeight.Bold, color = CrustyBrown)
                Spacer(Modifier.weight(1f))
                Text(moneyCart(cartViewModel.total()), fontWeight = FontWeight.ExtraBold, color = CrustyBrown)
            }
        }

        Spacer(Modifier.height(14.dp))
        SoftCard(Modifier.fillMaxWidth()) {
            Text("Entrega", fontWeight = FontWeight.Bold, color = CrustyBrown)
            Spacer(Modifier.height(4.dp))
            Text(cliente.direccion ?: "Sin dirección guardada", color = CrustyMuted)
            Spacer(Modifier.height(12.dp))
            AppTextField(
                value = metodoPago,
                onValueChange = { metodoPago = it },
                label = "Método de pago",
                leadingIcon = Icons.Rounded.Payments
            )
        }

        if (checkout is UiState.Error) ErrorMessage((checkout as UiState.Error).message)
        Spacer(Modifier.weight(1f))
        AppButton(
            text = if (checkout is UiState.Loading) "Procesando..." else "Confirmar pedido · ${moneyCart(cartViewModel.total())}",
            enabled = checkout !is UiState.Loading
        ) {
            ordersViewModel.finalizarCompra(cliente.idCliente, items, metodoPago)
        }
    }
}
