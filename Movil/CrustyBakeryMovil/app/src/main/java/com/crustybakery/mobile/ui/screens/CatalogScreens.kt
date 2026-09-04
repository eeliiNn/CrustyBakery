package com.crustybakery.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crustybakery.mobile.data.model.Producto
import com.crustybakery.mobile.ui.components.*
import com.crustybakery.mobile.ui.state.UiState
import com.crustybakery.mobile.ui.theme.*
import com.crustybakery.mobile.ui.viewmodel.CartViewModel
import com.crustybakery.mobile.ui.viewmodel.CatalogViewModel
import java.util.Locale

private fun money(value: Double) = String.format(Locale.US, "$%.2f", value)

@Composable
fun CatalogScreen(
    catalogViewModel: CatalogViewModel,
    onProduct: (Int) -> Unit,
    onCart: () -> Unit,
    onOrders: () -> Unit,
    onProfile: () -> Unit
) {
    val categorias by catalogViewModel.categorias.collectAsState()
    val productosState by catalogViewModel.productos.collectAsState()
    val selected by catalogViewModel.categoriaSeleccionada.collectAsState()

    Scaffold(
        containerColor = CrustyBackground,
        bottomBar = {
            NavigationBar(containerColor = CrustySurface, tonalElevation = 5.dp) {
                NavigationBarItem(true, {}, { Icon(Icons.Rounded.Home, null) }, label = { Text("Inicio") })
                NavigationBarItem(false, onCart, { Icon(Icons.Rounded.ShoppingCart, null) }, label = { Text("Carrito") })
                NavigationBarItem(false, onOrders, { Icon(Icons.Rounded.ReceiptLong, null) }, label = { Text("Pedidos") })
                NavigationBarItem(false, onProfile, { Icon(Icons.Rounded.Person, null) }, label = { Text("Perfil") })
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CrustyLogo(compact = true)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Crusty Bakery", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = CrustyBrown)
                        Text("¿Qué se te antoja hoy?", color = CrustyMuted)
                    }
                    RoundIconButton(Icons.Rounded.ShoppingBag, "Carrito", onCart)
                }
            }

            item { PromoCard() }

            item {
                Text("Categorías", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CrustyBrown)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selected == null,
                            onClick = { catalogViewModel.seleccionarCategoria(null) },
                            label = { Text("Todos") },
                            leadingIcon = { Icon(Icons.Rounded.Storefront, null) }
                        )
                    }
                    items(categorias) { categoria ->
                        FilterChip(
                            selected = selected == categoria.idCategoria,
                            onClick = { catalogViewModel.seleccionarCategoria(categoria.idCategoria) },
                            label = { Text(categoria.nombre) },
                            leadingIcon = { Icon(Icons.Rounded.Cake, null) }
                        )
                    }
                }
            }

            item {
                Text("Nuestros favoritos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CrustyBrown)
                Text("Elige un producto para ver más detalles", color = CrustyMuted, style = MaterialTheme.typography.bodyMedium)
            }

            when (val state = productosState) {
                UiState.Loading -> item { Loading() }
                is UiState.Error -> item { ErrorMessage(state.message) }
                is UiState.Success -> items(state.data, key = { it.idProducto }) { producto ->
                    ProductCard(producto) { onProduct(producto.idProducto) }
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun ProductCard(producto: Producto, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CrustySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(92.dp).clip(RoundedCornerShape(20.dp)).background(CrustyBlush),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Cake, null, tint = CrustyBrown, modifier = Modifier.size(46.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CrustyBrown)
                Text(producto.descripcion ?: "Preparado con el toque especial de Crusty Bakery", maxLines = 2, style = MaterialTheme.typography.bodySmall, color = CrustyMuted)
                Spacer(Modifier.height(8.dp))
                Text(money(producto.precio), fontWeight = FontWeight.ExtraBold, color = CrustyBrown)
            }
            Box(Modifier.size(38.dp).clip(CircleShape).background(CrustyCream), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.ArrowForward, null, tint = CrustyBrown)
            }
        }
    }
}

@Composable
fun ProductDetailScreen(
    idProducto: Int,
    catalogViewModel: CatalogViewModel,
    cartViewModel: CartViewModel,
    onCart: () -> Unit
) {
    val productosState by catalogViewModel.productos.collectAsState()
    var cantidad by remember { mutableIntStateOf(1) }
    val producto = (productosState as? UiState.Success)?.data?.firstOrNull { it.idProducto == idProducto }

    if (producto == null) {
        Column(Modifier.fillMaxSize().background(CrustyBackground).padding(22.dp)) {
            ErrorMessage("Producto no encontrado en el catálogo cargado.")
            AppButton("Recargar") { catalogViewModel.cargarProductos() }
        }
        return
    }

    Column(Modifier.fillMaxSize().background(CrustyBackground).padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CrustyLogo(compact = true)
            Spacer(Modifier.weight(1f))
            RoundIconButton(Icons.Rounded.ShoppingCart, "Carrito", onCart)
        }
        Spacer(Modifier.height(12.dp))

        Box(
            Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(30.dp)).background(CrustyBlush),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Cake, null, tint = CrustyBrown, modifier = Modifier.size(110.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text(producto.nombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = CrustyBrown)
        Text(money(producto.precio), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CrustyBrown)
        Spacer(Modifier.height(8.dp))
        Text(producto.descripcion ?: "Preparado artesanalmente con el sabor de Crusty Bakery.", color = CrustyMuted)
        Spacer(Modifier.height(22.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Cantidad", fontWeight = FontWeight.Bold, color = CrustyBrown)
            Spacer(Modifier.weight(1f))
            FilledTonalIconButton(onClick = { if (cantidad > 1) cantidad-- }) { Icon(Icons.Rounded.Remove, null) }
            Text("$cantidad", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold)
            FilledTonalIconButton(onClick = { cantidad++ }) { Icon(Icons.Rounded.Add, null) }
        }
        Spacer(Modifier.weight(1f))
        AppButton("Agregar al carrito · ${money(producto.precio * cantidad)}") { cartViewModel.agregar(producto, cantidad) }
        SecondaryButton("Ver carrito", onCart)
    }
}
