package pt.ipca.n26432.news

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem(
    var title: String,
    var icon: ImageVector,
    var route: String
) {
    data object Home :
        BottomNavItem(
            "Home",
            Icons.Filled.Home,
            "home"
        )

    data object Search :
        BottomNavItem(
            "Search",
            Icons.Filled.Search,
            "search"
        )

    data object List :
        BottomNavItem(
            "Favorites",
            Icons.Filled.Favorite,
            "favorites"
        )

    data object Analytics :
        BottomNavItem(
            "Me",
            Icons.Filled.Face,
            "me"
        )
}

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.List,
        BottomNavItem.Analytics,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            AddItem(
                screen = item,
                isSelected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        label = { Text(text = screen.title) },
        icon = { Icon(screen.icon, contentDescription = screen.title) },
        selected = isSelected,
        alwaysShowLabel = true,
        onClick = onClick,
        colors = NavigationBarItemDefaults.colors()
    )
}