package com.example.eventflow.navigation

import androidx.navigation.NavController
import com.example.eventflow.constants.Constants
import com.example.eventflow.model.Location
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

class INavigationRouteImpl(private val navController: NavController): INavigationRouter{

    override fun navigateToHomeScreen() {
        navController.navigate(Destination.HomeScreen.route)
    }

    override fun navigateToFavouriteScreen() {
        navController.navigate(Destination.FavouriteScreen.route)
    }

    override fun navigateToForgotPasswordScreen() {
        navController.navigate(Destination.ForgotPasswordScreen.route)
    }

    override fun navigateToAndClearBackStack(route: String) {
        navController.navigate(route) {
            popUpTo("splash") { inclusive = true }
            launchSingleTop = true
        }
    }

    override fun navigateToEventDetailScreen(id: String?) {
        if (id != null) {
            navController.navigate("${Destination.EventDetailScreen.route}/${id}")
        } else {
            navController.navigate(Destination.EventDetailScreen.route)
        }

    }

    override fun navigateToSettingsScreen() {
        navController.navigate(Destination.SettingsScreen.route)
    }

    override fun navigateToAddEditEventScreen(eventId: String?) {
        val route = if (eventId != null) {
            "add_edit_event_screen/$eventId"
        } else {
            "add_edit_event_screen/null"
        }
        navController.navigate(route)
    }
    override fun navigateToFilterScreen() {
        navController.navigate(Destination.SearchFilterScreen.route)
    }

    override fun navigateToLogInScreen() {
        navController.navigate(Destination.LogInScreen.route) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    override fun navigateToSignUpScreen() {
        navController.navigate(Destination.SignUpScreen.route)
    }

    override fun returnBack(){
        navController.popBackStack()
    }

    override fun navigateToMap(latitude: Double?, longitude: Double?) {
        navController.navigate(route = MapScreenDestination(latitude, longitude))
    }

    override fun returnFromMap(latitude: Double, longitude: Double) {
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Location> = moshi.adapter(Location::class.java)

        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set(Constants.LOCATION, jsonAdapter.toJson(Location(latitude, longitude)))
        returnBack()
    }

    override fun getNavController(): NavController {
        return navController
    }

    override fun navigateTo(route: String) {
        navController.navigate(route)
    }

    override fun getCurrentRoute(): String? {
        return navController.currentBackStackEntry?.destination?.route
    }
}