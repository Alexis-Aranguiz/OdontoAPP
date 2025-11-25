package com.example.odontoapp.view

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.odontoapp.view.screens.AgendaScreen
import com.example.odontoapp.view.screens.BookingScreen
import com.example.odontoapp.view.screens.ExploreScreen
import com.example.odontoapp.view.screens.ProfileScreen

object Routes {
    const val SHELL = "shell"
    const val EXPLORE = "explore"
    const val AGENDA = "agenda"
    const val PROFILE = "profile"
    const val BOOKING = "booking"
}

@Composable
fun AppNavHost() {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = Routes.SHELL
    ) {
        // -------- Shell con bottom bar --------
        composable(Routes.SHELL) {
            val innerNav = rememberNavController()

            Scaffold(
                bottomBar = {
                    val entry by innerNav.currentBackStackEntryAsState()
                    val currentRoute = entry?.destination?.route

                    NavigationBar {
                        // ExplorAR
                        NavigationBarItem(
                            selected = currentRoute == Routes.EXPLORE,
                            onClick = {
                                innerNav.navigate(Routes.EXPLORE) {
                                    popUpTo(innerNav.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {},
                            label = { Text("Explorar") }
                        )

                        // AGENDA
                        NavigationBarItem(
                            selected = currentRoute == Routes.AGENDA,
                            onClick = {
                                innerNav.navigate(Routes.AGENDA) {
                                    popUpTo(innerNav.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {},
                            label = { Text("Agenda") }
                        )

                        // PERFIL
                        NavigationBarItem(
                            selected = currentRoute == Routes.PROFILE,
                            onClick = {
                                innerNav.navigate(Routes.PROFILE) {
                                    popUpTo(innerNav.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {},
                            label = { Text("Perfil") }
                        )
                    }
                }
            ) { paddingValues ->
                NavHost(
                    navController = innerNav,
                    startDestination = Routes.EXPLORE,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable(Routes.EXPLORE) {
                        ExploreScreen(
                            onReserve = { id, name ->
                                val encodedName = Uri.encode(name)
                                nav.navigate("${Routes.BOOKING}/$id/$encodedName")
                            }
                        )
                    }
                    composable(Routes.AGENDA) {
                        AgendaScreen()
                    }
                    composable(Routes.PROFILE) {
                        ProfileScreen()
                    }
                }
            }
        }

        // -------- Pantalla de Booking, recibe id y nombre --------
        composable(
            route = "${Routes.BOOKING}/{dentistId}/{dentistName}",
            arguments = listOf(
                navArgument("dentistId") { type = NavType.StringType },
                navArgument("dentistName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val dentistId = backStackEntry.arguments?.getString("dentistId") ?: ""
            val dentistNameEncoded =
                backStackEntry.arguments?.getString("dentistName") ?: ""
            val dentistName = Uri.decode(dentistNameEncoded)

            BookingScreen(
                dentistId = dentistId,
                dentistName = dentistName,
                onBack = { nav.popBackStack() },
                onBooked = { nav.popBackStack() }
            )
        }
    }
}
