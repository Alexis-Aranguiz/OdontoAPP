package com.example.odontoapp.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.odontoapp.view.screens.*

object Routes {
    const val SHELL = "shell"
    const val EXPLORE = "explore"
    const val AGENDA = "agenda"
    const val PROFILE = "profile"
    const val BOOKING = "booking/{dentistId}"
}

@Composable
fun AppNavHost() {
    val outerNav = rememberNavController()

    NavHost(
        navController = outerNav,
        startDestination = Routes.SHELL
    ) {
        // Contenedor con BottomBar
        composable(Routes.SHELL) {
            val innerNav = rememberNavController()

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        val navBackStackEntry by innerNav.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination

                        fun isSelected(route: String, dest: NavDestination?) =
                            dest?.hierarchy?.any { it.route == route } == true

                        NavigationBarItem(
                            selected = isSelected(Routes.EXPLORE, currentDestination),
                            onClick = {
                                innerNav.navigate(Routes.EXPLORE) {
                                    popUpTo(innerNav.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {}, label = { Text("Explorar") }
                        )
                        NavigationBarItem(
                            selected = isSelected(Routes.AGENDA, currentDestination),
                            onClick = {
                                innerNav.navigate(Routes.AGENDA) {
                                    popUpTo(innerNav.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {}, label = { Text("Agenda") }
                        )
                        NavigationBarItem(
                            selected = isSelected(Routes.PROFILE, currentDestination),
                            onClick = {
                                innerNav.navigate(Routes.PROFILE) {
                                    popUpTo(innerNav.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {}, label = { Text("Perfil") }
                        )
                    }
                }
            ) { padding ->
                NavHost(
                    navController = innerNav,
                    startDestination = Routes.EXPLORE,
                    modifier = Modifier.padding(padding)
                ) {
                    composable(Routes.EXPLORE) {
                        ExploreScreen(onReserve = { id -> outerNav.navigate("booking/$id") })
                    }
                    composable(Routes.AGENDA) { AgendaScreen() }
                    composable(Routes.PROFILE) { ProfileScreen() }
                }
            }
        }

        // Flujo de reserva
        composable(Routes.BOOKING) { backStack ->
            val dentistId = backStack.arguments?.getString("dentistId")!!
            BookingScreen(dentistId = dentistId, onBooked = { outerNav.popBackStack() })
        }
    }
}
