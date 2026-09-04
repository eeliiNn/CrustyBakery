package com.crustybakery.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.crustybakery.mobile.ui.viewmodel.OrdersViewModel
import java.util.Locale

private fun moneyOrder(value: Double) = String.format(Locale.US, "$%.2f", value)

@Composable
fun OrdersScreen(cliente: Cliente, viewModel: OrdersViewModel, onPedido: (Int) -> Unit) {
    val state by viewModel.pedidos.collectAsState()
    LaunchedEffect(cliente.idCliente) { viewModel.cargarPedidos(cliente.idCliente) }

    Column(Modifier.fillMaxSize().background(CrustyBackground).padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CrustyLogo(compact = true)
            Spacer(Modifier.width(10.dp))
            ScreenTitle("Mis pedidos", "Consulta el estado de tus compras")
        }
        Spacer(Modifier.height(14.dp))

        when (val current = state) {
            UiState.Loading -> Loading()
            is UiState.Error -> ErrorMessage(current.message)
            is UiState.Success -> {
                if (current.data.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Aún no tienes pedidos", color = CrustyMuted)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(current.data, key = { it.idPedido }) { pedido ->
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onPedido(pedido.idPedido) },
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(containerColor = CrustySurface)
                            ) {
                                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(48.dp).background(CrustyCream, CircleShape), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Rounded.ReceiptLong, null, tint = CrustyBrown)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text("Pedido #${pedido.idPedido}", fontWeight = FontWeight.Bold, color = CrustyBrown)
                                        Text(pedido.estado, color = CrustyMuted, style = MaterialTheme.typography.bodySmall)
                                        Text(pedido.fechaPedido ?: "", color = CrustyMuted, style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text(moneyOrder(pedido.total), fontWeight = FontWeight.ExtraBold, color = CrustyBrown)
                                }
                            }
                        }
                    }
                }
            }
            else -> Unit
        }
    }
}

@Composable
fun OrderDetailScreen(idPedido: Int, viewModel: OrdersViewModel) {
    val state by viewModel.pedidoActual.collectAsState()
    LaunchedEffect(idPedido) { viewModel.cargarPedido(idPedido) }

    Column(Modifier.fillMaxSize().background(CrustyBackground).padding(20.dp)) {
        when (val current = state) {
            UiState.Loading -> Loading()
            is UiState.Error -> ErrorMessage(current.message)
            is UiState.Success -> {
                val pedido = current.data
                ScreenTitle("Pedido #${pedido.idPedido}", "Estado: ${pedido.estado}")
                Spacer(Modifier.height(16.dp))

                Surface(shape = RoundedCornerShape(26.dp), color = CrustyPink, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.LocalShipping, null, tint = CrustyBrown, modifier = Modifier.size(40.dp))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Seguimiento del pedido", fontWeight = FontWeight.Bold, color = CrustyBrown)
                                Text("Tu pedido está en proceso", color = CrustyMuted)
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        LinearProgressIndicator(
                            progress = { if (pedido.estado.equals("ENTREGADO", true)) 1f else 0.55f },
                            modifier = Modifier.fillMaxWidth(),
                            color = CrustyBrown,
                            trackColor = CrustyCream
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))
                SoftCard(Modifier.fillMaxWidth()) {
                    Text("Productos", fontWeight = FontWeight.Bold, color = CrustyBrown)
                    Spacer(Modifier.height(8.dp))
                    pedido.detalles.forEach { detalle ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Text("${detalle.cantidad} × ${detalle.nombreProducto ?: detalle.producto?.nombre ?: "Producto"}", modifier = Modifier.weight(1f), color = CrustyMuted)
                            Text(moneyOrder(detalle.subtotal), fontWeight = FontWeight.SemiBold)
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), color = CrustySoftBrown)
                    Row(Modifier.fillMaxWidth()) {
                        Text("Total", fontWeight = FontWeight.Bold, color = CrustyBrown)
                        Spacer(Modifier.weight(1f))
                        Text(moneyOrder(pedido.total), fontWeight = FontWeight.ExtraBold, color = CrustyBrown)
                    }
                }

                pedido.nombreRepostero?.let {
                    Spacer(Modifier.height(12.dp))
                    Surface(shape = RoundedCornerShape(18.dp), color = CrustyCream, modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.BakeryDining, null, tint = CrustyBrown)
                            Spacer(Modifier.width(8.dp))
                            Text("Repostero asignado: $it", color = CrustyBrown)
                        }
                    }
                }

                Spacer(Modifier.weight(1f))
                val cancelable = !pedido.estado.equals("ENTREGADO", true) && !pedido.estado.equals("CANCELADO", true)
                if (cancelable) SecondaryButton("Cancelar pedido") { viewModel.cancelarPedido(pedido.idPedido) }
            }
            else -> Unit
        }
    }
}
