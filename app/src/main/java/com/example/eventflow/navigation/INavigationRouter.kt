package com.example.eventflow.navigation

import androidx.navigation.NavController

interface INavigationRouter {
    fun navigateToHomeScreen()
    fun navigateToFavouriteScreen()
    fun navigateToEventDetailScreen(id: String?)
    fun navigateToSettingsScreen()
    fun navigateToAddEditEventScreen(eventId: String? = null)
    fun navigateToAndClearBackStack(route: String)
    fun navigateToForgotPasswordScreen()
    fun navigateToFilterScreen()
    fun navigateToLogInScreen()
    fun navigateToSignUpScreen()
    fun returnBack()
    fun navigateToMap(latitude: Double?, longitude: Double?)
    fun returnFromMap(latitude: Double, longitude: Double)
    fun getNavController(): NavController

    fun navigateTo(route: String)
    fun getCurrentRoute(): String?
}