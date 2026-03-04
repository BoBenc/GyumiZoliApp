package hu.bobenc.gyumizoli.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Kezdőlap", Icons.Default.Home)
    object Basket : Screen("basket", "Kosár", Icons.Default.ShoppingCart)
    object Profile : Screen("profile", "Profil", Icons.Default.Person)
    object Orders : Screen("orders", "Rendeléseim", Icons.Default.ShoppingCart)
    object Categories : Screen("categories", "Kategóriák", Icons.Default.List)
    object AboutUs : Screen("about_us", "Rólunk", Icons.Default.Info)
    object Search : Screen("search", "Keresés", Icons.Default.Search)
    object ProductList : Screen("productlist/{categorySlug}", "Termékek", Icons.Default.List)
    object Detail : Screen("detail/{category}/{id}", "Részletek", Icons.Default.Home)
}