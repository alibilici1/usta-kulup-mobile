package com.ustakulup.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ustakulup.data.model.UserRole
import com.ustakulup.ui.admin.AdminProfessionalsScreen
import com.ustakulup.ui.admin.AdminQuotasScreen
import com.ustakulup.ui.admin.AdminRequestsScreen
import com.ustakulup.ui.auth.LoginScreen
import com.ustakulup.ui.auth.RegisterScreen
import com.ustakulup.ui.professional.ProfessionalApplyScreen
import com.ustakulup.ui.professional.ProfessionalDashboardScreen
import com.ustakulup.ui.professional.ProfessionalOfferScreen
import com.ustakulup.ui.user.NewRequestScreen
import com.ustakulup.ui.user.RequestDetailScreen
import com.ustakulup.ui.user.UserDashboardScreen

sealed class Screen(val route: String) {
    // Auth
    object Login              : Screen("login")
    object Register           : Screen("register")

    // User
    object UserDashboard      : Screen("user/dashboard")
    object NewRequest         : Screen("user/requests/new")
    object RequestDetail      : Screen("user/requests/{requestId}") {
        fun createRoute(id: String) = "user/requests/$id"
    }

    // Professional
    object ProfessionalApply  : Screen("professional/apply")
    object ProfessionalDash   : Screen("professional/dashboard")
    object ProfessionalOffer  : Screen("professional/offer/{requestId}") {
        fun createRoute(id: String) = "professional/offer/$id"
    }

    // Admin
    object AdminProfessionals : Screen("admin/professionals")
    object AdminQuotas        : Screen("admin/quotas")
    object AdminRequests      : Screen("admin/requests")
}

@Composable
fun UstaKulupNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ─── Auth ───────────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { user ->
                    val dest = when (user.role) {
                        UserRole.ADMIN        -> Screen.AdminProfessionals.route
                        UserRole.PROFESSIONAL -> Screen.ProfessionalDash.route
                        else                  -> Screen.UserDashboard.route
                    }
                    navController.navigate(dest) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToApply    = { navController.navigate(Screen.ProfessionalApply.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.UserDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ─── User ────────────────────────────────────────────────────────────
        composable(Screen.UserDashboard.route) {
            UserDashboardScreen(
                onNewRequest    = { navController.navigate(Screen.NewRequest.route) },
                onRequestDetail = { id -> navController.navigate(Screen.RequestDetail.createRoute(id)) },
                onLogout        = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.NewRequest.route) {
            NewRequestScreen(
                onSuccess = { navController.popBackStack() },
                onBack    = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.RequestDetail.route,
            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
        ) { backStackEntry ->
            RequestDetailScreen(
                requestId = backStackEntry.arguments?.getString("requestId") ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        // ─── Professional ─────────────────────────────────────────────────
        composable(Screen.ProfessionalApply.route) {
            ProfessionalApplyScreen(
                onSuccess = { navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } },
                onBack    = { navController.popBackStack() }
            )
        }

        composable(Screen.ProfessionalDash.route) {
            ProfessionalDashboardScreen(
                onOfferScreen = { id -> navController.navigate(Screen.ProfessionalOffer.createRoute(id)) },
                onLogout      = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.ProfessionalOffer.route,
            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
        ) { backStackEntry ->
            ProfessionalOfferScreen(
                requestId = backStackEntry.arguments?.getString("requestId") ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        // ─── Admin ──────────────────────────────────────────────────────────
        composable(Screen.AdminProfessionals.route) {
            AdminProfessionalsScreen(
                onNavigateToQuotas   = { navController.navigate(Screen.AdminQuotas.route) },
                onNavigateToRequests = { navController.navigate(Screen.AdminRequests.route) },
                onLogout             = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AdminQuotas.route) {
            AdminQuotasScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.AdminRequests.route) {
            AdminRequestsScreen(onBack = { navController.popBackStack() })
        }
    }
}
