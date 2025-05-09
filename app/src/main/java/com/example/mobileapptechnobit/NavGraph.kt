package com.example.mobileapptechnobit

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.mobileapptechnobit.ui.DetailScheduleScreen
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobileapptechnobit.ui.*
import com.example.mobileapptechnobit.ui.ForgotPasswordScreen
import com.example.mobileapptechnobit.ui.ResetPasswordScreen
import com.example.mobileapptechnobit.ViewModel.AuthViewModel
import com.example.mobileapptechnobit.ViewModel.AuthViewModelFactory
import com.example.mobileapptechnobit.data.repository.AuthRepository
import com.example.mobileapptechnobit.ui.component.SuccessScreen

@Composable
fun NavGraph(navController: NavHostController, authViewModel: AuthViewModel) { // Tambahkan parameter authViewModel
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val token = remember { sharedPref.getString("AUTH_TOKEN", null) }
    val authRepository = AuthRepository()
    val authViewModel: AuthViewModel = viewModel( // Hapus deklarasi ulang ini
        factory = AuthViewModelFactory(authRepository, context)
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Walkthrough.route) {
            WalkthroughScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                navCtrl = navController
            )
        }
        composable(Screen.ForgotPassword.route){
            ForgotPasswordScreen(navCtrl = navController)
        }
        composable(Screen.ResetPassword.route){ backStackEntry ->
            ResetPasswordScreen(
                navCtrl = navController,
                email = backStackEntry.arguments?.getString("email") ?: ""
            )
        }
        composable(Screen.ChangePassword.route){
            ChangePasswordScreen(navCtrl = navController, token = token ?: "")
        }
        composable(Screen.Success.route){ backStackEntry ->
            SuccessScreen(
                navCtrl = navController,
                message = backStackEntry.arguments?.getString("message") ?: ""
            )

        }
        composable(Screen.Home.route){
            com.example.mobileapptechnobit.ui.HomeScreen(navCtrl = navController)
        }
        composable(Screen.History.route){
            HistoryScreen(navCtrl = navController)
        }
        composable(Screen.Schedule.route) {
            DetailScheduleScreen(navCtrl = navController, token = token ?: "")
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController, token = token ?: "")
        }
        composable(Screen.DetailProfile.route) {
            DetailProfileScreen(navController = navController, token = token ?: "")
        }
        composable(Screen.EditProfile.route) {
            EditProfile(navController, token = token ?: "")
        }
        composable(Screen.EditSukses.route) {
            EditSuksesScreen(navController, token = token ?: "")
        }
        composable("detail_gaji") {
            DetailGajiScreen(navCtrl = navController)
        }
        composable(Screen.InfoPerusahaan.route) {
            InformasiPerusahaan(navController = navController, token = token ?: "")
        }
    }
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Walkthrough : Screen("walkthrough_screen")
    object Login : Screen("login_screen")
    object Home : Screen("home_screen")
    object ForgotPassword : Screen("forgot_password_screen")
    object ResetPassword : Screen("reset_password_screen/{email}")
    object ChangePassword : Screen("change_password")
    object Profile : Screen("profile_screen")
    object DetailProfile : Screen("detailprofile_screen")
    object EditProfile : Screen("edit_profile_screen")
    object EditSukses : Screen("edit_sukses_screen")
    object InfoPerusahaan : Screen("info_perusahaan_screen")
    object Success : Screen("success_screen/{message}")
    object Schedule: Screen("schedule_screen")
    object History : Screen("history_screen")
}