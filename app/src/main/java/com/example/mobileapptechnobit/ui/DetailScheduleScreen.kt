package com.example.mobileapptechnobit.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobileapptechnobit.R
import com.example.mobileapptechnobit.ViewModel.ScheduleViewModel
import com.example.mobileapptechnobit.data.remote.PatrolScheduleResponse
import com.example.mobileapptechnobit.ui.component.TopAppBar
import com.example.mobileapptechnobit.ui.theme.robotoFontFamily

@Composable
fun DetailScheduleScreen(
    navCtrl: NavController,
    token: String,
    viewModel: ScheduleViewModel = viewModel()
) {
    val context = LocalContext.current
    val schedules = viewModel.scheduleList
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage
    val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val validToken = if (token.isNotEmpty()) token else sharedPref.getString("AUTH_TOKEN", null) ?: ""
    val currentToken = sharedPref.getString("AUTH_TOKEN", null) ?: token

    LaunchedEffect(currentToken) {
        viewModel.fetchSchedules(currentToken)
        Log.d("detailschedules", "Token yang diterima: $currentToken")
    }

    Scaffold(
        topBar = {
            Box {
                TopAppBar()
                Column(
                    Modifier
                        .fillMaxWidth()
                ) {
                    ScheduleTitle(navCtrl = navCtrl)
                    Column(Modifier.background(Color.White)) {
                        Spacer(Modifier.height(40.dp))
                        when {
                            isLoading -> Text(
                                "Loading...",
                                modifier = Modifier.padding(16.dp)
                            )
                            error != null -> Text(
                                "Error: $error",
                                modifier = Modifier.padding(16.dp),
                                color = Color.Red
                            )
                            else -> {
                                schedules.forEach { schedule ->
                                    ScheduleCardFromApi(schedule = schedule, navCtrl = navCtrl)
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->

        Box(modifier = Modifier.padding(paddingValues))
    }
}




@Composable
fun ScheduleTitle(modifier: Modifier = Modifier, navCtrl: NavController) {
    Box (
        modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp)
    ) {
        IconButton(
            onClick = { navCtrl.navigate("home_screen") },
            Modifier.padding(start = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "back button",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = "Jadwal Patroli",
            textAlign = TextAlign.Center,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            fontSize = 22.sp,
            modifier = Modifier.fillMaxWidth().align(Alignment.Center))
    }
}
@Composable
fun ScheduleCardFromApi(schedule: PatrolScheduleResponse, navCtrl: NavController? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Shift dan Tanggal
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(
                            color = when (schedule.waktu?.lowercase()) {
                                "senin" -> Color(0xFFFFE082)   // Kuning lembut
                                "selasa" -> Color(0xFFFFCC80)  // Oranye muda
                                "rabu" -> Color(0xFFFFAB91)   // Oranye merah muda
                                "kamis" -> Color(0xFFFF8A65)   // Oranye terang
                                "jumat" -> Color(0xFF81D4FA)   // Biru muda
                                "sabtu" -> Color(0xFF4FC3F7)   // Biru langit
                                "minggu" -> Color(0xFFB39DDB)   // Ungu pastel
                                else -> Color.Gray
                            },
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = schedule.waktu?.replaceFirstChar { it.uppercase() } ?: "Tidak diketahui",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = schedule.tanggal,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Lokasi patrol
            Text(
                text = "Lokasi: ${schedule.lokasi ?: "-"}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Baris Jam Mulai - Selesai
            Row(
                Modifier.padding(horizontal = 8.dp),
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
                        fontWeight = FontWeight(500),
                        fontSize = 16.sp
                    )
                    Text(
                        text = schedule.jam_mulai ?: "-",
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
                        fontWeight = FontWeight(500),
                        fontSize = 16.sp
                    )
                    Text(
                        text = schedule.jam_selesai ?: "-",
                        fontFamily = robotoFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info rekan tugas
            Text(
                text = "Rekan tugas: ${schedule.rekan_tugas ?: "-"}",
                fontSize = 14.sp,
                fontFamily = robotoFontFamily
            )
        }
    }
}





@Preview(showBackground = true)
@Composable
private fun DetailScheduleScreenPrev() {
    DetailScheduleScreen(navCtrl = rememberNavController(), token = "dummy_token")
}

