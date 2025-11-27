package com.example.mobileapptechnobit.ui

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobileapptechnobit.R
import com.example.mobileapptechnobit.Screen
import com.example.mobileapptechnobit.ViewModel.PatroliViewModel
import com.example.mobileapptechnobit.ViewModel.ProfileViewModel
import com.example.mobileapptechnobit.ViewModel.ProfileViewModelFactory
import com.example.mobileapptechnobit.data.remote.PatroliQrInfo
import com.example.mobileapptechnobit.data.repository.ProfileRepository
import com.example.mobileapptechnobit.saveBitmapToPublicPictures
import com.example.mobileapptechnobit.ui.theme.primary100
import com.example.mobileapptechnobit.ui.theme.robotoFontFamily
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormPatroli(
    viewModelPat: PatroliViewModel,
    navCtrl: NavController,
    qrToken: String,
    token: String
) {
    val bitmap by viewModelPat.capturedBitmap.collectAsState()
    Log.d("FormPatroli", "Bitmap diterima: $bitmap")
    val token = viewModelPat.token.collectAsState().value

    val context = LocalContext.current
    val repository = ProfileRepository(context)
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(repository))
    val employeeProfile by viewModel.employeesProfile.collectAsState()
    val fullname = employeeProfile?.fullname ?: ""

    val qrInfo = remember(qrToken) {
        Gson().fromJson(qrToken, PatroliQrInfo::class.java)
    }

    val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    var selectedShift by remember { mutableStateOf("Pagi") }
    var selectedCondition by remember { mutableStateOf("Aman") }
    var notes by remember { mutableStateOf(TextFieldValue("")) }
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchEmployeesProfile(token ?: "")
    }

    Scaffold(
        topBar = {
            Box {
                TopAppBar(
                    title = {},
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = colorResource(id = R.color.white)
                    ),
                    modifier = Modifier.height(112.dp)
                )
                Column(
                    Modifier
                        .fillMaxWidth()
                ) {
                    FormPatTitle(navCtrl = navCtrl)
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FormPatroliSubmit(
                qrToken = qrToken,
                bitmap = bitmap,
                fullname = fullname,
                date = date,
                time = time,
                selectedShift = selectedShift,
                onShiftChange = { selectedShift = it },
                selectedCondition = selectedCondition,
                onConditionChange = { selectedCondition = it },
                notes = notes,
                onNotesChange = { notes = it },
                onSubmit = {
                    if (selectedCondition == "Tidak aman" && notes.text.trim().isEmpty()) {
                        Toast.makeText(context, "Wajib mengisi kolom catatan", Toast.LENGTH_SHORT).show()
                    } else {
                        showDialog = true
                    }
                }
            )
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        text = "Kirim Laporan Patroli?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = robotoFontFamily
                    )
                },
                text = {
                    Text(
                        text = "Pastikan semua data sudah benar",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = robotoFontFamily
                    )
                },
                confirmButton = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { showDialog = false },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color.Gray),
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .weight(1f)
                            ) {
                                Text(
                                    text = "Batal",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = robotoFontFamily
                                )
                            }
                            Button(
                                onClick = {
                                    showDialog = false
                                    isLoading = true
                                    coroutineScope.launch {
                                        try {
                                            val filename = "patroli_${SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(Date())}.jpg"
                                            val shiftId = when(selectedShift) {
                                                "Pagi" -> 1
                                                "Siang" -> 2
                                                "Malam" -> 3
                                                else -> 1
                                            }
                                            val catatan = notes.text
                                            val kondisi = selectedCondition.lowercase()

                                            val photoBase64 = bitmap?.let { bitmapToBase64(it) } ?: ""

                                            viewModelPat.submitPatroli(
                                                token = token,
                                                photoBase64 = photoBase64,
                                                filename = filename,
                                                shiftId = shiftId,
                                                catatan = catatan,
                                                kondisi = kondisi,
                                                placeId = qrInfo.id,
                                                latitude = qrInfo.latitude.toString(),
                                                longitude = qrInfo.longitude.toString()
                                            )
                                            Log.d("FORMPATROLI","$qrInfo")
                                            isLoading = false
                                            navCtrl.navigate(Screen.PatroliSukses.route)
                                        } catch (e: Exception) {
                                            isLoading = false
                                            Toast.makeText(context, "Gagal kirim data: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = primary100),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .weight(1f)
                            ) {
                                Text(
                                    text = "Kirim",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    fontFamily = robotoFontFamily
                                )
                            }
                        }
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.background(Color.Transparent)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormPatroliSubmit(
    qrToken: String,
    bitmap: Bitmap?,
    fullname: String,
    date: String,
    time: String,
    selectedShift: String,
    onShiftChange: (String) -> Unit,
    selectedCondition: String,
    onConditionChange: (String) -> Unit,
    notes: TextFieldValue,
    onNotesChange: (TextFieldValue) -> Unit,
    onSubmit: () -> Unit
) {

    val qrInfo = remember(qrToken) {
        Gson().fromJson(qrToken, PatroliQrInfo::class.java)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "${qrInfo.name}",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth() .padding(bottom = 10.dp),
                color = Color.Black
            )
        }

        item {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Preview Foto",
                    modifier = Modifier
                        .size(400.dp)
                        .padding(16.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Nama",
                fontSize = 16.sp,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = fullname,
                onValueChange = {},
                readOnly = true,
                placeholder = {
                    Text(
                        text = "Masukkan nama lengkap",
                        fontSize = 16.sp,
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Black
                )
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tanggal Scan",
                fontSize = 16.sp,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = date,
                onValueChange = {}, // readonly
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Black,
                    disabledTextColor = Color.Black
                )
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Waktu Scan",
                fontSize = 16.sp,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = time,
                onValueChange = {}, // readonly
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Black,
                    disabledTextColor = Color.Black
                )
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Shift",
                fontSize = 16.sp,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                listOf("Pagi", "Siang", "Malam").forEach { shift ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedShift == shift,
                            onClick = { onShiftChange(shift) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = primary100,
                                unselectedColor = Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = shift,
                            fontSize = 16.sp,
                            fontFamily = robotoFontFamily
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Kondisi Lokasi",
                fontSize = 16.sp,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                listOf("Aman", "Tidak aman").forEach { condition ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCondition == condition,
                            onClick = { onConditionChange(condition) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = primary100,
                                unselectedColor = Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = condition,
                            fontSize = 16.sp,
                            fontFamily = robotoFontFamily
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Catatan",
                fontSize = 16.sp,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = notes.text,
                onValueChange = { onNotesChange(TextFieldValue(it)) },
                placeholder = {
                    Text(
                        text = "Tulis catatan",
                        fontSize = 16.sp,
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                },
                modifier = Modifier.fillMaxWidth() .height(145.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Black
                )
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onSubmit,
                colors = ButtonDefaults.buttonColors(containerColor = primary100),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text("Kirim", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun FormPatTitle(modifier: Modifier = Modifier, navCtrl: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 30.dp)
    ) {
        IconButton(
            onClick = { navCtrl.popBackStack() },
            Modifier.padding(start = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali",
                tint = Color.LightGray,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

fun bitmapToBase64(bitmap: Bitmap): String {
    val resizedBitmap = resizeBitmap(bitmap, maxWidth = 800, maxHeight = 800)
    val outputStream = ByteArrayOutputStream()
    resizedBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)

    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}

fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val aspectRatio = width.toFloat() / height.toFloat()
    val newWidth: Int
    val newHeight: Int

    if (width > height) {
        newWidth = maxWidth
        newHeight = (newWidth / aspectRatio).toInt()
    } else {
        newHeight = maxHeight
        newWidth = (newHeight * aspectRatio).toInt()
    }

    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}