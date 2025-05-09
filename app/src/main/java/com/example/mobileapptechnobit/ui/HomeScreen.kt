package com.example.mobileapptechnobit.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.example.mobileapptechnobit.ViewModel.ScheduleViewModel
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.mobileapptechnobit.R
import com.example.mobileapptechnobit.ViewModel.AuthViewModel
import com.example.mobileapptechnobit.ViewModel.AuthViewModelFactory
import com.example.mobileapptechnobit.ViewModel.ProfileViewModel
import com.example.mobileapptechnobit.ViewModel.ProfileViewModelFactory
import com.example.mobileapptechnobit.data.API.UserProfileResponse
import com.example.mobileapptechnobit.data.remote.PatrolScheduleResponse
import com.example.mobileapptechnobit.data.repository.AuthRepository
import com.example.mobileapptechnobit.data.repository.ProfileRepository
import com.example.mobileapptechnobit.ui.component.BottomNavigationBar
import com.example.mobileapptechnobit.ui.component.TopAppBar
import com.example.mobileapptechnobit.ui.theme.robotoFontFamily

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(modifier: Modifier = Modifier, navCtrl: NavController) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val token = sharedPref.getString("AUTH_TOKEN", null) ?: ""

    val viewModel: ScheduleViewModel = viewModel()
    val schedules = viewModel.scheduleList
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage

    LaunchedEffect(Unit) {
        viewModel.fetchSchedules(token = token)
    }

    Scaffold(
        topBar = {
            Box(
                contentAlignment = Alignment.TopStart
            ){
                TopAppBar()
                Column {
                    ProfileSection()
                    ScheduleCard(navCtrl = navCtrl, schedules = schedules, isLoading = isLoading, error = error)
                    SalaryCard(navCtrl = navCtrl)
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(navCtrl = navCtrl, index = 0)
        },
    ){ padding ->
        Column (Modifier.padding(padding).fillMaxSize().zIndex(1f))
        {
            MainMenu()
        }
    }
}

@Composable
fun ProfileSection(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val repository = ProfileRepository(context)
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(repository))
    val authRepository = AuthRepository()

    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(authRepository, context))
    val profileState = viewModel.employeesProfile.collectAsState()
    val profile = profileState.value
    val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val token = sharedPref.getString("AUTH_TOKEN", null) ?: ""

    var userProfile by remember { mutableStateOf<UserProfileResponse?>(null) }

    LaunchedEffect(token) {
        viewModel.fetchEmployeesProfile(token)
        Log.d("PS", "Token yang diterima: $token")
        userProfile = repository.getUserProfile(token)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp, start = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        userProfile?.data?.photo.let { photo ->
            Image(
                painter = rememberImagePainter(data = photo), // Ganti dengan gambar profil
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(profile?.fullname ?: "", fontSize = 25.sp, color = Color.White, fontFamily = robotoFontFamily, fontWeight = FontWeight(500))
            Text(profile?.position ?: "", fontSize = 15.sp, color = Color.White, fontFamily = robotoFontFamily, fontWeight = FontWeight(400))
        }
    }
}

@Composable
fun ScheduleCard(
    modifier: Modifier = Modifier,
    navCtrl: NavController,
    schedules: List<PatrolScheduleResponse>?,
    isLoading: Boolean,
    error: String?
) {
        val nearestSchedule = schedules?.firstOrNull()
        nearestSchedule?.let { schedule ->
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        schedule.tanggal,
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight(500),
                        fontSize = 17.sp
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        "Lihat Jadwal",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            navCtrl.navigate("schedule_screen")
                        }
                    )
                    Spacer(Modifier.padding(start = 10.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                navCtrl.navigate("schedule_screen")
                            }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    Modifier.padding(horizontal = 25.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.frame_27),
                        contentDescription = null,
                        modifier = Modifier.size(45.dp),
                        tint = Color.Unspecified
                    )
                    Column(Modifier.padding(start = 8.dp)) {
                        Text(
                            text = "Mulai",
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight(500),
                            fontSize = 16.sp
                        )
                        Text(text = schedule.jam_mulai, fontFamily = robotoFontFamily)
                    }

                    Spacer(Modifier.weight(1f))
                    VerticalDivider(thickness = 1.dp, color = Color.LightGray, modifier = Modifier.height(50.dp))
                    Spacer(Modifier.weight(1f))

                    Icon(
                        painter = painterResource(R.drawable.frame_28),
                        contentDescription = null,
                        modifier = Modifier.size(45.dp),
                        tint = Color.Unspecified
                    )
                    Column(Modifier.padding(start = 8.dp)) {
                        Text(
                            text = "Selesai",
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight(500),
                            fontSize = 16.sp
                        )
                        Text(text = schedule.jam_selesai, fontFamily = robotoFontFamily)
                    }
                }
            }
        }
    }

@Composable
fun SalaryCard(
    navCtrl: NavController,
    salaryAmount: String = "Rp3.000.000,00"
) {
    var isVisible by remember { mutableStateOf(true) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Informasi Gaji", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        if (isVisible) salaryAmount else "••••••••••",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(
                        if (isVisible) R.drawable.eyes_open else R.drawable.eyes_closed
                    ),
                    contentDescription = "Toggle Visibility",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            isVisible = !isVisible
                        }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navCtrl.navigate("detail_gaji")
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lihat selengkapnya",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(16.dp)
                )
            }
        }
    }
}



@Composable
fun MainMenu(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp).padding(top = 20.dp).padding(bottom = 10.dp)
    ) {
        Text("Menu Utama", fontWeight = FontWeight.Bold, fontSize = 25.sp, fontFamily = robotoFontFamily)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MenuItem(painter = painterResource(R.drawable.presensi), label = "Presensi")
            MenuItem(painter = painterResource(R.drawable.patroli), label = "Patroli")
        }
    }
}

@Composable
fun MenuItem(modifier: Modifier = Modifier, painter: Painter, label: String) {
    Card (
        modifier = Modifier
        .size(160.dp)
        .clickable { },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(painter = painter, contentDescription = label, modifier = Modifier.size(100.dp), tint = Color.Unspecified)
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontFamily = robotoFontFamily, fontWeight = FontWeight(400), fontSize = 17.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePrev() {
    HomeScreen(navCtrl = rememberNavController())
}