package com.example.mobileapptechnobit.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.mobileapptechnobit.R
import com.example.mobileapptechnobit.Screen
import com.example.mobileapptechnobit.ViewModel.AuthViewModel
import com.example.mobileapptechnobit.ViewModel.AuthViewModelFactory
import com.example.mobileapptechnobit.ViewModel.CameraPresViewModel
import com.example.mobileapptechnobit.ViewModel.ProfileViewModel
import com.example.mobileapptechnobit.ViewModel.ProfileViewModelFactory
import com.example.mobileapptechnobit.ViewModel.SalaryViewModel
import com.example.mobileapptechnobit.ViewModel.ScheduleViewModel
import com.example.mobileapptechnobit.data.API.UserProfileResponse
import com.example.mobileapptechnobit.data.remote.PatrolScheduleResponse
import com.example.mobileapptechnobit.data.remote.formatCurrency
import com.example.mobileapptechnobit.data.repository.AuthRepository
import com.example.mobileapptechnobit.data.repository.ProfileRepository
import com.example.mobileapptechnobit.ui.component.BottomNavigationBar
import com.example.mobileapptechnobit.ui.component.TopAppBar
import com.example.mobileapptechnobit.ui.theme.primary100
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
                    SalaryCard(navCtrl = navCtrl, token = token)
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(navCtrl = navCtrl, index = 0)
        },
    ){ padding ->
        Column (Modifier
            .padding(padding)
            .fillMaxSize()
            .zIndex(1f))
        {
            MainMenu(navCtrl = navCtrl)
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

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    LaunchedEffect(token) {
        viewModel.fetchEmployeesProfile(token)
        Log.d("PS", "Token yang diterima: $token")
        userProfile = repository.getUserProfile(token)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = screenHeight * 0.08f, start = 20.dp, bottom = screenHeight * 0.008f),
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
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .clickable { // Klik seluruh card
                    navCtrl.navigate("schedule_screen")
                }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = schedule.tanggal ?: "",
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 17.sp
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "Lihat Jadwal",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            navCtrl.navigate("schedule_screen")
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.frame_27),
                        contentDescription = null,
                        modifier = Modifier.size(45.dp),
                        tint = Color.Unspecified
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = "Mulai",
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                        Text(
                            text = schedule.jam_mulai ?: "",
                            fontFamily = robotoFontFamily
                        )
                    }

                    Spacer(Modifier.weight(1f))
                    VerticalDivider(
                        thickness = 1.dp,
                        color = Color.LightGray,
                        modifier = Modifier.height(50.dp)
                    )
                    Spacer(Modifier.weight(1f))

                    Icon(
                        painter = painterResource(R.drawable.frame_28),
                        contentDescription = null,
                        modifier = Modifier.size(45.dp),
                        tint = Color.Unspecified
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = "Selesai",
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                        Text(
                            text = schedule.jam_selesai ?: "",
                            fontFamily = robotoFontFamily
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SalaryCard(
    navCtrl: NavController,
    token: String,
    viewModel: SalaryViewModel = viewModel()
) {
    val salary by viewModel.salaryData.collectAsState()
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchSalary(token)
    }

    val salaryAmount = formatCurrency(salary?.detail?.total_gaji)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Informasi Gaji", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        Text(
                            if (isVisible) salaryAmount else "••••••••••",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = painterResource(
                                if (isVisible) R.drawable.eyes_open else R.drawable.eyes_closed
                            ),
                            contentDescription = "Toggle Visibility",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { isVisible = !isVisible }
                        )
                    }

                }

            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navCtrl.navigate("detail_gaji") },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Lihat selengkapnya", color = Color.Gray, fontSize = 14.sp)
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
fun MainMenu(modifier: Modifier = Modifier, navCtrl: NavController, viewModel: CameraPresViewModel = remember { CameraPresViewModel() }) {

    var showPresentDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 20.dp)
            .padding(bottom = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Menu Utama", fontWeight = FontWeight.Bold, fontSize = 25.sp, fontFamily = robotoFontFamily)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MenuItem(
                painter = painterResource(R.drawable.presensi),
                label = "Presensi",

                onClick = { showPresentDialog = true },

//                onClick = {
//                    // Cek apakah pengguna sudah melakukan Clock In
//                    val clockInTime = viewModel.getClockInTime(context)
//                    if (clockInTime > 0L) {
//                        // Jika sudah Clock In tetapi belum Clock Out, navigasi ke ClockOutScreen
//                        navCtrl.navigate(Screen.ClockOut.route)
//                    } else {
//                        // Jika belum Clock In, navigasi ke CameraPresensiScreen
//                        navCtrl.navigate(Screen.CameraPresensi.route)
//                    }
//                }
            )
            PresentMenuDialog(
                showDialog = showPresentDialog,
                navCtrl = navCtrl,
                onDismiss = { showPresentDialog = false }
            )
            MenuItem(
                painter = painterResource(R.drawable.patroli),
                label = "Patroli",
                onClick = { navCtrl.navigate(Screen.PatroliNfc.route) }
            )
        }
    }
}

@Composable
fun MenuItem(modifier: Modifier = Modifier, painter: Painter, label: String, onClick: () -> Unit) {
    Card (
        modifier = Modifier
//            .size(160.dp)
            .width(160.dp)
            .padding(bottom = 20.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(painter = painter, contentDescription = label, modifier = Modifier.size(100.dp), tint = Color.Unspecified)
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontFamily = robotoFontFamily, fontWeight = FontWeight(400), fontSize = 17.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresentMenuDialog(modifier: Modifier = Modifier, showDialog: Boolean, navCtrl: NavController, onDismiss: () -> Unit, viewModel: CameraPresViewModel = remember { CameraPresViewModel() }) {

    val context = LocalContext.current
    if(showDialog){

        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(),
            content = {
                Box(
                    modifier.fillMaxWidth().background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                ){
                    Column (modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Pilih aksi", fontFamily = robotoFontFamily, fontWeight = FontWeight(500), fontSize = 20.sp, modifier = Modifier.padding(vertical = 20.dp))
                        Column (
                            Modifier
                                .border(width = 1.dp, shape = RoundedCornerShape(1.dp), color = Color.LightGray)
                                .fillMaxWidth()
                                .clickable {
                                    // Cek apakah pengguna sudah melakukan Clock In
                                    val clockInTime = viewModel.getClockInTime(context)
                                    if (clockInTime > 0L) {
                                    // Jika sudah Clock In tetapi belum Clock Out, navigasi ke ClockOutScreen
                                         navCtrl.navigate(Screen.ClockOut.route)
                                    } else {
                                     // Jika belum Clock In, navigasi ke CameraPresensiScreen
                                         navCtrl.navigate(Screen.CameraPresensi.route)
                                    }
                                }
                        )
                        {
                            Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                                Image(painter = painterResource(R.drawable.frame_8748), contentDescription = null, Modifier.size(40.dp))
                                Spacer(Modifier.padding(horizontal = 5.dp))
                                Column {
                                    Text(text = "Presensi", fontFamily = robotoFontFamily, fontWeight = FontWeight(500), fontSize = 15.sp)
                                    Text(text = "Lakukan presensi hari ini", fontFamily = robotoFontFamily, fontWeight = FontWeight(400), fontSize = 13.sp)
                                }
                            }
                        }
                        Spacer(Modifier.padding(5.dp))
                        Column (
                            Modifier
                                .border(width = 1.dp, shape = RoundedCornerShape(1.dp), color = Color.LightGray)
                                .fillMaxWidth()
                                .clickable {
                                    navCtrl.navigate("permission_screen")
                                }
                        )
                        {
                            Row (verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                                Image(painter = painterResource(R.drawable.kuning), contentDescription = null, Modifier.size(40.dp))
                                Spacer(Modifier.padding(horizontal = 5.dp))
                                Column {
                                    Text(text = "Perizinan", fontFamily = robotoFontFamily, fontWeight = FontWeight(500), fontSize = 15.sp)
                                    Text(text = "Ajukan izin tidak masuk kerja", fontFamily = robotoFontFamily, fontWeight = FontWeight(400), fontSize = 13.sp)
                                }
                            }
                        }
                        HorizontalDivider(Modifier.padding(vertical = 20.dp))

                        Text(text = "Tutup", fontFamily = robotoFontFamily, fontWeight = FontWeight(400), fontSize = 17.sp, color = primary100, modifier = Modifier.padding(bottom = 20.dp).clickable { onDismiss() })
                    }
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePrev() {
    HomeScreen(navCtrl = rememberNavController())
}