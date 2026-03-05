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
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.bobenc.gyumizoli.navigation.Screen
import hu.bobenc.gyumizoli.ui.about.AboutUsView
import hu.bobenc.gyumizoli.ui.common.NavigationBar
import hu.bobenc.gyumizoli.ui.home.HomeView
import hu.bobenc.gyumizoli.ui.search.SearchView
import hu.bobenc.gyumizoli.ui.theme.GyumiZoliTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val gyumiZoliGreen = AndroidColor.parseColor("#4CAF50")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.dark(gyumiZoliGreen)
        )
        setContent {
            GyumiZoliTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            navController = navController,
                            uniqueItemCount = 1
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