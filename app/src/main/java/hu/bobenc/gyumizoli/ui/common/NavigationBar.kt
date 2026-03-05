package hu.bobenc.gyumizoli.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import hu.bobenc.gyumizoli.navigation.Screen

@Composable
fun NavigationBar(
    navController: NavHostController,
    uniqueItemCount: Int
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val items = listOf(Screen.Home, Screen.Categories, Screen.Profile, Screen.Basket, Screen.AboutUs)

    Box(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(12.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .blur(radius = 15.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded),
            shape = RoundedCornerShape(30.dp),
            color = Color.White.copy(alpha = 0.95f),
            shadowElevation = 8.dp,
            border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    val isProfileScreen = screen == Screen.Profile

                    CustomAnimatedNavItem(
                        screen = screen,
                        isSelected = isSelected,
                        badgeCount = if (screen == Screen.Basket) uniqueItemCount else 0,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = isProfileScreen
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomAnimatedNavItem(
    screen: Screen,
    isSelected: Boolean,
    badgeCount: Int,
    onClick: () -> Unit
) {
    val background by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFE8F5E9) else Color.Transparent,
        animationSpec = tween(durationMillis = 100)
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF2E7D32) else Color.Gray,
        animationSpec = tween(durationMillis = 100)
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = if (isSelected) 12.dp else 8.dp, vertical = 8.dp)
            .animateContentSize(animationSpec = tween(100)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (badgeCount > 0) {
                BadgedBox(
                    badge = {
                        Badge(containerColor = Color.Red, contentColor = Color.White, modifier = Modifier.size(13.dp).offset(x = (-2).dp, y = 0.dp)) {
                            Text(badgeCount.toString(), fontSize = 9.sp, lineHeight = 9.sp)
                        }
                    }
                ) {
                    Icon(imageVector = screen.icon, contentDescription = screen.label, tint = contentColor)
                }
            } else {
                Icon(imageVector = screen.icon, contentDescription = screen.label, tint = contentColor)
            }
            AnimatedVisibility(visible = isSelected) {
                Text(
                    text = screen.label,
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}