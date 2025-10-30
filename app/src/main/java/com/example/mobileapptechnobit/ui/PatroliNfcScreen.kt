package com.example.mobileapptechnobit.ui

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.request.Disposable
import com.example.mobileapptechnobit.NfcReaderViewModel
import com.example.mobileapptechnobit.R
import com.example.mobileapptechnobit.Screen
import com.example.mobileapptechnobit.data.remote.PatroliQrInfo
import com.example.mobileapptechnobit.ui.theme.robotoFontFamily
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatroliNfcScreen(
    modifier: Modifier = Modifier,
    navCtrl: NavController,
    nfcViewModel: NfcReaderViewModel,
    onEnableNfcReader: () ->Unit
) {
    val dummyPatroliNfcInfo = PatroliQrInfo(
        id = 12,
        company_id = 5,
        code = "$2y$12\$Ml0DzyWNvkaW5p\\/VF391VOYaLH\\/LKGgFz33lj7HTwy7SltDbsjpim",
        name = "shift 1",
        address = "Jalan Depan Rektorat, Ketawanggede, Kota Malang, Jawa Timur, Jawa, 65145, Indonesia",
        latitude = -7.9532978,
        longitude = 112.6137203,
        description = "null",
    )
    var error by remember { mutableStateOf(false) }
    var readNfcTagUid by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val uidHex by nfcViewModel.uidHex.collectAsState()

    LaunchedEffect(uidHex) {
        if (uidHex != null) {
            //call api to check uid, if success, encode json response and navigate to next screen
            Log.d("uid", uidHex!!)
            val encodedDummyNfcInfo =
                Uri.encode(Gson().toJson(dummyPatroliNfcInfo))
            navCtrl.navigate(
                Screen.CameraPatroli.route.replace(
                    "{qrToken}",
                    encodedDummyNfcInfo
                )
            )
            nfcViewModel.clearUid()
        }
    }
//    LaunchedEffect(Unit) {
//        onEnableNfcReader()
//    }

    Scaffold(
        topBar = {
            Box {
                TopAppBar(
                    title = {},
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = colorResource(id = R.color.primary100)
                    ),
                    modifier = Modifier.height(112.dp)
                )
                Column(
                    Modifier
                        .fillMaxWidth()
                ) {
                    PatroliNfcTitle(navCtrl = navCtrl)
                }
            }
        }
    ) { innerPadding ->
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .focusRequester(focusRequester)
                .focusable()
                .onKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown && event.utf16CodePoint.toChar().isLetterOrDigit()) {
                        val char = event.utf16CodePoint.toChar()
                        readNfcTagUid += char
                        true
                    } else if (event.type == KeyEventType.KeyDown && event.utf16CodePoint.toChar() == '\n') {
                        val reversedUid = nfcViewModel.reversedDecimalToHex(readNfcTagUid)
                        Log.d("uid", reversedUid)
                        val encodedDummyNfcInfo =
                            Uri.encode(Gson().toJson(dummyPatroliNfcInfo))
                        navCtrl.navigate(
                            Screen.CameraPatroli.route.replace(
                                "{qrToken}",
                                encodedDummyNfcInfo
                            )
                        )
                        readNfcTagUid = ""
                        true
                    } else {
                        false // biar event lain (termasuk tombol back) diteruskan
                    }
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (error) {
                ResponseNfc(
                    imageId = R.drawable.selesai,
                    title = "Gagal Melakukan Scanning",
                    subTitle = "Terjadi kesalahan dalam melakukan verifikasi Kartu NFC"
                )
            } else {
                ResponseNfc(
                    imageId = R.drawable.walkthrough4,
                    title = "Tempelkan Kartu",
                    subTitle = "Tempelkan kartu NFC untuk akses menu Patroli"
                )
            }
        }
    }
}

@Composable
fun PatroliNfcTitle(modifier: Modifier = Modifier, navCtrl: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 30.dp)
    ) {
        IconButton(
            onClick = {
                navCtrl.navigate(Screen.Home.route)
            },
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
            text = "Scan Kartu NFC",
            textAlign = TextAlign.Center,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight(500),
            color = Color.White,
            fontSize = 25.sp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        )
    }
}

@Composable
fun ResponseNfc(
    modifier: Modifier = Modifier,
    imageId: Int,
    title: String,
    subTitle: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = "NFC Patrol Response",
            modifier = Modifier
                .size(250.dp)
        )
        Text(
            text = title,
            fontSize = 22.sp,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subTitle,
            fontSize = 14.sp,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            color = Color.Gray
        )
    }
}
