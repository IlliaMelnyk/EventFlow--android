package com.example.eventflow.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.example.eventflow.AddEditEventScreen
import com.example.eventflow.EventDetailScreen
import com.example.eventflow.FavouriteScreen
import com.example.eventflow.FilterScreen
import com.example.eventflow.ForgotPasswordScreen
import com.example.eventflow.HomeScreen
import com.example.eventflow.LogInScreen
import com.example.eventflow.MapScreen
import com.example.eventflow.SettingsScreen
import com.example.eventflow.SignUpScreen
import com.example.eventflow.SplashScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(startDestination: String,
             navHostController: NavHostController = rememberNavController(),
             navRouter: INavigationRouter = remember {
                 INavigationRouteImpl(navHostController)
             }
) {
    NavHost(startDestination = startDestination, navController = navHostController){

        composable(Destination.SplashScreen.route) {
            SplashScreen(
                onTimeout = {
                    if (FirebaseAuth.getInstance().currentUser != null) {
                        navRouter.navigateToAndClearBackStack(Destination.HomeScreen.route)
                    } else {
                        navRouter.navigateToAndClearBackStack(Destination.LogInScreen.route)
                    }
                }
            )
        }

        composable(Destination.LogInScreen.route) {
            LogInScreen(navRouter)
        }

        composable(Destination.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(navRouter)
        }

        composable(Destination.SignUpScreen.route) {
            SignUpScreen(navRouter)
        }

        composable(Destination.HomeScreen.route) {
            HomeScreen(navHostController,navRouter)
        }

        composable(Destination.AddEditEventScreen.route) {
            AddEditEventScreen(navRouter, null)
        }

        composable(
            route = Destination.AddEditEventScreen.route + "/{eventId}",
            arguments = listOf(
                navArgument("eventId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            AddEditEventScreen(navRouter, eventId)
        }

        composable(Destination.FavouriteScreen.route) {
            FavouriteScreen(navRouter)
        }

        composable(
            route = "${Destination.EventDetailScreen.route}/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) {
            val id = it.arguments?.getString("id")
            EventDetailScreen(navRouter, id)
        }

        composable<MapScreenDestination> { backStackEntry ->
            val destination : MapScreenDestination = backStackEntry.toRoute()
            MapScreen(navigation = navRouter, mapScreenDestination = destination)
        }

        composable(Destination.SettingsScreen.route) {
            SettingsScreen(navRouter)
        }

        composable(Destination.SearchFilterScreen.route) {
            FilterScreen(navRouter)
        }

    }
}

