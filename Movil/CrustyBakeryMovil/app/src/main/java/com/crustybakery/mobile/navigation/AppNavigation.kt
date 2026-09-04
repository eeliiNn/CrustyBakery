package com.crustybakery.mobile.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.crustybakery.mobile.ui.screens.*
import com.crustybakery.mobile.ui.viewmodel.*

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val CATALOG = "catalog"
    const val PRODUCT = "product/{id}"
    const val CART = "cart"
    const val CHECKOUT = "checkout"
    const val ORDERS = "orders"
    const val ORDER = "order/{id}"
    const val PROFILE = "profile"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel()
    val catalogViewModel: CatalogViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val ordersViewModel: OrdersViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()

    val cliente by authViewModel.cliente.collectAsState()

    val startDestination =
        if (cliente == null) Routes.LOGIN
        else Routes.CATALOG

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.CATALOG) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onSuccess = {
                    navController.navigate(Routes.CATALOG) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CATALOG) {
            CatalogScreen(
                catalogViewModel = catalogViewModel,
                onProduct = { id ->
                    navController.navigate("product/$id")
                },
                onCart = { navController.navigate(Routes.CART) },
                onOrders = { navController.navigate(Routes.ORDERS) },
                onProfile = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(
            route = Routes.PRODUCT,
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            ProductDetailScreen(
                idProducto = backStackEntry.arguments?.getInt("id") ?: 0,
                catalogViewModel = catalogViewModel,
                cartViewModel = cartViewModel,
                onCart = { navController.navigate(Routes.CART) }
            )
        }

        composable(Routes.CART) {
            CartScreen(
                cartViewModel = cartViewModel,
                onCheckout = {
                    navController.navigate(Routes.CHECKOUT)
                }
            )
        }

        composable(Routes.CHECKOUT) {
            val current = cliente
            if (current != null) {
                CheckoutScreen(
                    cliente = current,
                    cartViewModel = cartViewModel,
                    ordersViewModel = ordersViewModel,
                    onSuccess = { idPedido ->
                        navController.navigate("order/$idPedido") {
                            popUpTo(Routes.CART) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Routes.ORDERS) {
            val current = cliente
            if (current != null) {
                OrdersScreen(
                    cliente = current,
                    viewModel = ordersViewModel,
                    onPedido = { id ->
                        navController.navigate("order/$id")
                    }
                )
            }
        }

        composable(
            route = Routes.ORDER,
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            OrderDetailScreen(
                idPedido = backStackEntry.arguments?.getInt("id") ?: 0,
                viewModel = ordersViewModel
            )
        }

        composable(Routes.PROFILE) {
            val current = cliente
            if (current != null) {
                ProfileScreen(
                    cliente = current,
                    viewModel = profileViewModel,
                    onSaved = { actualizado ->
                        authViewModel.actualizarSesion(actualizado)
                    },
                    onLogout = {
                        authViewModel.cerrarSesion()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0)
                        }
                    }
                )
            }
        }
    }
}
