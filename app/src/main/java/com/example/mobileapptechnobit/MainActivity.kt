package com.example.sisteminformasimenejemensatpam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sisteminformasimenejemensatpam.data.API.ApiClient
import com.example.sisteminformasimenejemensatpam.ui.HalamanLupaPassword
import com.example.sisteminformasimenejemensatpam.ui.HalamanResetPassword
import com.example.sisteminformasimenejemensatpam.ui.HalamanUbahPassword
import com.example.sisteminformasimenejemensatpam.ui.theme.SistemInformasiMenejemenSatpamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ApiClient.init(this)

        setContent {
            SistemInformasiMenejemenSatpamTheme {
                val navController = rememberNavController()
                val startDestination = "lupa password"

                NavHost(navController = navController, startDestination = startDestination) {
                    composable("ubah password"){ HalamanUbahPassword(navCtrl = navController) }
                    composable("lupa password"){ HalamanLupaPassword(navCtrl = navController)}
                    composable("reset password/{email}"){ backStackEntry ->
                        HalamanResetPassword(
                            navCtrl = navController,
                            email = backStackEntry.arguments?.getString("email") ?: "",
                        )
                    }
                }
            }
        }
    }
}
