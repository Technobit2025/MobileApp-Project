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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sisteminformasimenejemensatpam.ViewModel.AuthViewModel
import com.example.sisteminformasimenejemensatpam.ViewModel.AuthViewModelFactory
import com.example.sisteminformasimenejemensatpam.data.repository.AuthRepository
import com.example.sisteminformasimenejemensatpam.ui.theme.roboto

@Composable
fun HalamanResetPassword(modifier: Modifier = Modifier, navCtrl: NavController, email: String) {

    var newPass by remember { mutableStateOf("") }
    var RepeatNewPass by remember { mutableStateOf("") }
    var newPassIsVisible by remember { mutableStateOf(false) }
    var repeatNewPassIsVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val repository = remember { AuthRepository() }
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(repository = repository))
    val resetPasswordMessage by viewModel.resetPasswordMessage.observeAsState()

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = { navCtrl.navigate("lupa password") }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                Modifier.align(Alignment.Start).padding(top = 20.dp, start = 20.dp)
            )
        }

        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "Perbarui Password",
            fontFamily = roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )

        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Silakan perbarui password anda",
            fontFamily = roboto,
            fontWeight = FontWeight(500),
        )

        Spacer(modifier = Modifier.height(70.dp))
        Text(
            text = "Password Baru",
            textAlign = TextAlign.Left,
            fontFamily = roboto,
            fontWeight = FontWeight(500),
            fontSize = 17.sp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = newPass,
            onValueChange = {newPass = it},
            placeholder = { Text(text = "Masukkan password baru", color = Color.Gray)},
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            trailingIcon = {
                val visibilityIcon = if (newPassIsVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(
                    onClick = {
                        newPassIsVisible = !newPassIsVisible
                    }
                ) {
                    Icon(imageVector = visibilityIcon, contentDescription = null)
                }
            },
            supportingText = {
                if(newPass == ""){ }
                else if(newPass.length < 8){
                    Text(text = "Password harus terdiri dari minimal 8 karakter", color =  Color.Red)
                }
            },
            visualTransformation = if (newPassIsVisible) VisualTransformation.None else PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Konfirmasi Password",
            textAlign = TextAlign.Left,
            fontFamily = roboto,
            fontWeight = FontWeight(500),
            fontSize = 17.sp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = RepeatNewPass,
            onValueChange = {RepeatNewPass = it},
            placeholder = { Text(text = "Masukkan password baru", color = Color.Gray)},
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            trailingIcon = {
                val visibilityIcon = if (repeatNewPassIsVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(
                    onClick = {
                        repeatNewPassIsVisible = !repeatNewPassIsVisible
                    }
                ) {
                    Icon(imageVector = visibilityIcon, contentDescription = null)
                }
            },
            supportingText = {
                if(RepeatNewPass == ""){ }
                else if(RepeatNewPass != newPass){
                    Text(text = "Password tidak sesuai", color =  Color.Red)
                }
            },
            visualTransformation = if (repeatNewPassIsVisible) VisualTransformation.None else PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(40.dp))
        Text(text = "Email anda adalah $email")
        Button(
            onClick = {
                if (newPass == RepeatNewPass) {
                    viewModel.resetPassword(email, newPass)
                    resetPasswordMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Pengulangan password salah!", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(Color(0xFF2752E7)),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(50.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(
                text = "Perbarui Password",
                fontFamily = roboto,
                fontWeight = FontWeight(500),
                fontSize = 17.sp,
            )
        }
    }
}