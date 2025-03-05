package com.example.sisteminformasimenejemensatpam.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sisteminformasimenejemensatpam.ViewModel.AuthViewModel
import com.example.sisteminformasimenejemensatpam.ViewModel.AuthViewModelFactory
import com.example.sisteminformasimenejemensatpam.data.repository.AuthRepository
import com.example.sisteminformasimenejemensatpam.ui.theme.roboto

@Composable
fun HalamanLupaPassword(modifier: Modifier = Modifier, navCtrl: NavController) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current
    var inputOTP by remember { mutableStateOf("") }
    val repository = remember { AuthRepository() }
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(repository = repository))
    val forgotPasswordMessage by viewModel.requestOtpMessage.observeAsState()
    val verifyOtpMessage by viewModel.verifyOtpMessage.observeAsState()
    val isVerified by viewModel.isOtpVerified.observeAsState()

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = { navCtrl.navigate("ubah password") }, modifier = Modifier.align(Alignment.Start).padding(top = 20.dp, start = 20.dp)) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                Modifier
//                    .align(Alignment.Start)
//                    .padding(top = 20.dp, start = 20.dp)
            )
        }

        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "Lupa Password",
            fontFamily = roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )

        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Silakan masukkan email anda",
            fontFamily = roboto,
            fontWeight = FontWeight(500),
        )

        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "Email",
            textAlign = TextAlign.Left,
            fontFamily = roboto,
            fontWeight = FontWeight(500),
            fontSize = 17.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text(text = "Masukkan email anda", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )

        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = {
                if (email.isBlank()) {
                    Toast.makeText(context, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                viewModel.requestOtp(email)
                forgotPasswordMessage?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                }
            },
            colors = ButtonDefaults.buttonColors(Color(0xFF2752E7)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(50.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(
                text = "Kirim Kode OTP",
                fontFamily = roboto,
                fontWeight = FontWeight(500),
                fontSize = 17.sp,
            )
        }
        LaunchedEffect (isVerified){
            if(isVerified == true){
                navCtrl.navigate("reset password/$email")
            }
        }
        forgotPasswordMessage?.let { 
            Text(text = it)
        }
        verifyOtpMessage?.let {
            Text(text = it)
        }

        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Kode OTP",
            textAlign = TextAlign.Left,
            fontFamily = roboto,
            fontWeight = FontWeight(500),
            fontSize = 17.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = inputOTP,
            onValueChange = { inputOTP = it},
            placeholder = { Text(text = "Masukkan Kode OTP", color = Color.Gray)},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )

        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = {
                if (email.isBlank()) {
                    Toast.makeText(context, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                viewModel.verifyOtp(email, inputOTP)
                verifyOtpMessage?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                }
            },
            colors = ButtonDefaults.buttonColors(Color(0xFF2752E7)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(50.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(
                text = "Lanjut",
                fontFamily = roboto,
                fontWeight = FontWeight(500),
                fontSize = 17.sp,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LupaPassPrev() {
    HalamanLupaPassword(navCtrl = rememberNavController())
}
