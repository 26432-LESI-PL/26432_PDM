package pt.ipca.n26432.news

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource

sealed class BottomNavItem(
    var title: String,
    var icon: ImageVector
) {
    data object Home :
        BottomNavItem(
            "Home",
            Icons.Filled.Home
        )

    data object List :
        BottomNavItem(
            "Favorites",
            Icons.Filled.Favorite
        )

    data object Analytics :
        BottomNavItem(
            "Me",
            Icons.Filled.Face
        )


}

@Composable
fun BottomNavigation() {

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.List,
        BottomNavItem.Analytics,
    )

    NavigationBar {
        items.forEach { item ->
            AddItem(
                screen = item
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomNavItem
) {
    NavigationBarItem(
        // Text that shows bellow the icon
        label = {
            Text(text = screen.title)
        },

        // The icon resource
        icon = {
            Icon(
                screen.icon,
                contentDescription = screen.title
            )
        },

        // Display if the icon it is select or not
        selected = false,

        // Always show the label bellow the icon or not
        alwaysShowLabel = true,

        // Click listener for the icon
        onClick = { /*TODO*/ },

        // Control all the colors of the icon
        colors = NavigationBarItemDefaults.colors()
    )
}