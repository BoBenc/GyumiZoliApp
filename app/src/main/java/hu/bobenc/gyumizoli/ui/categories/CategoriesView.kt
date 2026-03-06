package hu.bobenc.gyumizoli.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CategoryItem(
    val name: String,
    val slug: String,
    val bgColor: Color,
    val iconColor: Color,
    val icon: ImageVector
)

@Composable
fun CategoriesView(
    onCategoryClick: (String) -> Unit
) {
    val categories = listOf(
        CategoryItem(
            name = "Összes termék",
            slug = "all",
            bgColor = Color(0xFFE3F2FD),
            iconColor = Color(0xFF1976D2),
            icon = Icons.Rounded.Dashboard
        ),
        CategoryItem(
            name = "Gyümölcsök",
            slug = "gyumolcs",
            bgColor = Color(0xFFFFF3E0),
            iconColor = Color(0xFFF57C00),
            icon = Icons.Rounded.Spa
        ),
        CategoryItem(
            name = "Zöldségek",
            slug = "zoldseg",
            bgColor = Color(0xFFE8F5E9),
            iconColor = Color(0xFF388E3C),
            icon = Icons.Rounded.Eco
        ),
        CategoryItem(
            name = "Akciós termékek",
            slug = "akcios",
            bgColor = Color(0xFFFFEBEE),
            iconColor = Color(0xFFD32F2F),
            icon = Icons.Rounded.LocalOffer
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        Text(
            text = "Kategóriák",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF2E7D32),
            modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(categories) { category ->
                CategoryGridCard(category) {
                    onCategoryClick(category.slug)
                }
            }
        }
    }
}

@Composable
fun CategoryGridCard(category: CategoryItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = category.bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = category.iconColor,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = category.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = category.iconColor,
                textAlign = TextAlign.Center
            )
        }
    }
}