package com.ustakulup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.ustakulup.data.local.TokenDataStore
import com.ustakulup.data.model.UserRole
import com.ustakulup.ui.navigation.Screen
import com.ustakulup.ui.navigation.UstaKulupNavGraph
import com.ustakulup.ui.theme.Background
import com.ustakulup.ui.theme.UstaKulupTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenDataStore: TokenDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var startDestination = Screen.Login.route

        lifecycleScope.launch {
            val token = tokenDataStore.token.firstOrNull()
            val user  = tokenDataStore.currentUser.firstOrNull()
            if (!token.isNullOrBlank() && user != null) {
                startDestination = when (user.role) {
                    UserRole.ADMIN        -> Screen.AdminProfessionals.route
                    UserRole.PROFESSIONAL -> Screen.ProfessionalDash.route
                    else                  -> Screen.UserDashboard.route
                }
            }

            setContent {
                UstaKulupTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Background
                    ) {
                        val navController = rememberNavController()
                        UstaKulupNavGraph(
                            navController = navController,
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }
}
