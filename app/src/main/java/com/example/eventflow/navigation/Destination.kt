package com.example.eventflow.navigation

sealed class Destination(val route: String) {
    object SplashScreen : Destination(route = "splash")
    object LogInScreen : Destination(route = "login")
    object SignUpScreen : Destination(route = "signup")
    object HomeScreen : Destination(route = "home")
    object AddEditEventScreen : Destination(route = "add_edit_event_screen")
    object FavouriteScreen : Destination(route = "favourite")
    object EventDetailScreen : Destination(route = "event_detail")
    object SettingsScreen : Destination(route = "settings")
    object SearchFilterScreen : Destination(route = "filter")
    object ForgotPasswordScreen : Destination(route = "password")
}