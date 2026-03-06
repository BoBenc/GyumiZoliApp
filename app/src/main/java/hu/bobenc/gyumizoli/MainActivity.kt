package hu.bobenc.gyumizoli

import android.os.Bundle
import androidx.activity.SystemBarStyle
import android.graphics.Color as AndroidColor
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import barion.sdk.shared.BarionGatewayPlugin
import dagger.hilt.android.AndroidEntryPoint
import hu.bobenc.gyumizoli.navigation.Screen
import hu.bobenc.gyumizoli.ui.about.AboutUsView
import hu.bobenc.gyumizoli.ui.basket.BasketView
import hu.bobenc.gyumizoli.ui.basket.BasketViewModel
import hu.bobenc.gyumizoli.ui.basket.PaymentView
import hu.bobenc.gyumizoli.ui.basket.ShippingDetailsView
import hu.bobenc.gyumizoli.ui.categories.CategoriesView
import hu.bobenc.gyumizoli.ui.common.NavigationBar
import hu.bobenc.gyumizoli.ui.home.HomeView
import hu.bobenc.gyumizoli.ui.product.ProductDetailView
import hu.bobenc.gyumizoli.ui.productlist.ProductListView
import hu.bobenc.gyumizoli.ui.search.SearchView
import hu.bobenc.gyumizoli.ui.theme.GyumiZoliTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var barionPlugin: BarionGatewayPlugin
    private val gyumiZoliGreen = AndroidColor.parseColor("#4CAF50")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.dark(gyumiZoliGreen)
        )
        barionPlugin = BarionGatewayPlugin(this, null)
        setContent {
            GyumiZoliTheme {
                val navController = rememberNavController()
                val sharedBasketViewModel: BasketViewModel = hiltViewModel()
                val basketItems by sharedBasketViewModel.basketItems.collectAsState()
                val uniqueItemCount = basketItems.size

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            navController = navController,
                            uniqueItemCount = uniqueItemCount
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route
                        ) {
                            composable(Screen.Home.route) {
                                HomeView(
                                    onProductClick = { category, id ->
                                        navController.navigate("detail/$category/$id")
                                    },
                                    onNavigateToSearch = {
                                        navController.navigate(Screen.Search.route)
                                    }
                                )
                            }

                            composable(Screen.Categories.route) {
                                CategoriesView(
                                    onCategoryClick = { slug ->
                                        navController.navigate("productlist/$slug")
                                    }
                                )
                            }

                            composable(
                                route = "productlist/{categorySlug}",
                                arguments = listOf(
                                    navArgument("categorySlug") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val slug = backStackEntry.arguments?.getString("categorySlug") ?: "all"
                                ProductListView(
                                    categorySlug = slug,
                                    onBackClick = { navController.popBackStack() },
                                    onProductClick = { category, id ->
                                        navController.navigate("detail/$category/$id")
                                    }
                                )
                            }

                            composable(
                                route = "detail/{category}/{id}",
                                arguments = listOf(
                                    navArgument("category") { type = NavType.StringType },
                                    navArgument("id") { type = NavType.IntType }
                                )
                            ) { backStackEntry ->
                                val cat = backStackEntry.arguments?.getString("category") ?: ""
                                val id = backStackEntry.arguments?.getInt("id") ?: 0
                                ProductDetailView(
                                    category = cat,
                                    productId = id,
                                    onBackClick = { navController.popBackStack() },
                                    onItemAddedToCart = { sharedBasketViewModel.loadBasket() }
                                )
                            }

                            composable(Screen.Basket.route) {
                                BasketView(
                                    viewModel = sharedBasketViewModel,
                                    onNavigateToCheckout = { navController.navigate("shipping_details") }
                                )
                            }

                            composable("shipping_details") {
                                ShippingDetailsView(
                                    viewModel = sharedBasketViewModel,
                                    onBackClick = { navController.popBackStack() },
                                    onNavigateToPayment = { navController.navigate("payment") },
                                    onOrderSuccess = {
                                        navController.navigate("order_success") {
                                            popUpTo(Screen.Basket.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable("payment") {
                                val context = androidx.compose.ui.platform.LocalContext.current
                                PaymentView(
                                    viewModel = sharedBasketViewModel,
                                    onStartBarionPayment = { secret ->
                                        barionPlugin.present(secret, null) { paymentResult ->
                                            val resultStr = paymentResult.toString()
                                            android.util.Log.d("BarionResult", "Nyers válasz: $resultStr")

                                            if (!resultStr.contains("Canceled", ignoreCase = true) &&
                                                !resultStr.contains("Failed", ignoreCase = true) &&
                                                !resultStr.contains("Exception", ignoreCase = true)) {
                                                sharedBasketViewModel.clearBasketAfterPayment()
                                                navController.navigate("order_success") {
                                                    popUpTo(Screen.Basket.route) { inclusive = true }
                                                }
                                            } else {
                                                android.widget.Toast.makeText(context, "Fizetés megszakítva vagy sikertelen.", android.widget.Toast.LENGTH_LONG).show()
                                                navController.popBackStack()
                                            }
                                        }
                                    },
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable("order_success") {
                                hu.bobenc.gyumizoli.ui.basket.OrderSuccessView(
                                    viewModel = sharedBasketViewModel,
                                    onNavigateHome = {
                                        sharedBasketViewModel.resetOrderStatus()
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(0)
                                        }
                                    }
                                )
                            }

                            composable(Screen.AboutUs.route) {
                                AboutUsView(
                                    onBackClick = { navController.popBackStack() },
                                    onNavigateHome = {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(0)
                                        }
                                    }
                                )
                            }

                            composable(Screen.Search.route) {
                                SearchView(
                                    onBackClick = { navController.popBackStack() },
                                    onProductClick = { category, id -> navController.navigate("detail/$category/$id") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}