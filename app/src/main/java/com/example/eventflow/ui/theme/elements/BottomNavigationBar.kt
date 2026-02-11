package com.example.eventflow.ui.theme.elements

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.eventflow.navigation.Destination
import com.example.eventflow.R
import com.example.eventflow.navigation.INavigationRouter

@Composable
fun BottomNavigationBar(router: INavigationRouter) {
    val items = listOf(
        BottomNavItem(
            title = stringResource(id = R.string.nav_home),
            icon = Icons.Default.Home,
            route = Destination.HomeScreen.route
        ),
        BottomNavItem(
            title = stringResource(id = R.string.nav_add),
            icon = Icons.Default.Add,
            route = Destination.AddEditEventScreen.route
        ),
        BottomNavItem(
            title = stringResource(id = R.string.nav_favorites),
            icon = Icons.Default.Favorite,
            route = Destination.FavouriteScreen.route
        ),
        BottomNavItem(
            title = stringResource(id = R.string.nav_settings),
            icon = Icons.Default.Settings,
            route = Destination.SettingsScreen.route
        )
    )

    val currentRoute = router.getCurrentRoute()

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        router.navigateTo(item.route)
                    }
                }
            )
        }
    }
}