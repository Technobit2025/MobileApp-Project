package com.example.mobileapptechnobit.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailGajiScreen(navCtrl: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Gaji", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable { navCtrl.popBackStack() }
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {
            Text("Detail Gaji Kamu", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Total Gaji: Rp3.000.000,00", fontSize = 18.sp)
            Text("Tunjangan: Rp500.000,00", fontSize = 18.sp)
            Text("Potongan: Rp200.000,00", fontSize = 18.sp)
            Text("Gaji Bersih: Rp3.300.000,00", fontSize = 18.sp)
        }
    }
}
