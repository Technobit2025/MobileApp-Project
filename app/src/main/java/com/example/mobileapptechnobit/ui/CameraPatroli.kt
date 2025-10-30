package com.example.mobileapptechnobit.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.mobileapptechnobit.NfcReaderViewModel
import com.example.mobileapptechnobit.R
import com.example.mobileapptechnobit.Screen
import com.example.mobileapptechnobit.ViewModel.PatroliViewModel
import com.example.mobileapptechnobit.data.remote.PatroliQrInfo
import com.example.mobileapptechnobit.ui.theme.robotoFontFamily
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraPatroli(
    navCtrl: NavController,
    token: String,
    qrToken: String,
    viewModel: PatroliViewModel,
    nfcViewModel: NfcReaderViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }

    val qrInfo = remember(qrToken) {
        Gson().fromJson(qrToken, PatroliQrInfo::class.java)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true) {
            Toast.makeText(
                context,
                "Camera permission granted",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                "Camera permission denied",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    var isProcessingPhoto by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        } else {
            cameraController.bindToLifecycle(lifecycleOwner)
        }
    }

    RequestLocationPermissions()

    Scaffold(
        topBar = {
            Box {
                TopAppBar(
                    title = {},
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = colorResource(id = R.color.black100)
                    ),
                    modifier = Modifier.height(112.dp)
                )
                Column(
                    Modifier
                        .fillMaxWidth()
                ) {
                    CameraPatTitle(navCtrl = navCtrl)
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = colorResource(R.color.black100)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f),
                    contentAlignment = Alignment.Center
                ) {
                    CameraPreview(
                        controller = cameraController,
                        modifier = Modifier.fillMaxSize()
                    )

                    Image(
                        painter = painterResource(id = R.drawable.camera_frame),
                        contentDescription = "Camera Frame",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-56).dp)
                            .background(
                                colorResource(id = R.color.black100).copy(
                                    alpha = 0.5f
                                )
                            )
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pastikan wajah terlihat dengan jelas",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = robotoFontFamily,
                            color = androidx.compose.ui.graphics.Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp, start = 32.dp, end = 32.dp)
            ) {
                IconButton(
                    onClick = {
                        triggerVibration(context)
                        isProcessingPhoto = true
                        getCurrentLocationReal(context) { location ->
                            takePhoto(
                                controller = cameraController,
                                context = context,
                                token = token,
                                location = location,
                                onPhotoTaken = { bitmap ->
                                    viewModel.onTakePhoto(bitmap, token)
                                    Log.d(
                                        "CameraPatroli",
                                        "Navigating to FormPatroli with QR Info: $qrToken"
                                    )
                                    isProcessingPhoto = false // loading selesai
                                    navCtrl.navigate(
                                        Screen.FormPatroli.route.replace(
                                            "{qrToken}",
                                            Uri.encode(Gson().toJson(qrInfo))
                                        )
                                    )
                                },
                                onProcessingDone = {
                                    isProcessingPhoto = false
                                } // loading selesai jika error
                            )
                        }
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .align(Alignment.Center)
                        .background(
                            color = androidx.compose.ui.graphics.Color.White,
                            shape = CircleShape
                        ),
                    enabled = !isProcessingPhoto
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "Ambil Foto",
                        modifier = Modifier.size(36.dp)
                    )
                }

                IconButton(
                    onClick = {
                        cameraController.cameraSelector =
                            if (cameraController.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else CameraSelector.DEFAULT_BACK_CAMERA
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.CenterEnd)
                        .background(
                            color = androidx.compose.ui.graphics.Color.White,
                            shape = CircleShape
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.camera_switch),
                        contentDescription = "Ganti Kamera",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            if (isProcessingPhoto) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(id = R.color.black).copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Foto sedang diproses...",
                            color = androidx.compose.ui.graphics.Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CameraPatTitle(modifier: Modifier = Modifier, navCtrl: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 30.dp)
    ) {
        IconButton(
            onClick = {
                navCtrl.navigateUp()
//                navCtrl.navigate(Screen.Patroli.route)
            },
            Modifier.padding(start = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "back button",
                tint = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = "Bukti Patroli",
            textAlign = TextAlign.Center,
            fontFamily = robotoFontFamily,
            fontWeight = androidx.compose.ui.text.font.FontWeight(500),
            color = androidx.compose.ui.graphics.Color.White,
            fontSize = 25.sp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        )
    }
}

private fun takePhoto(
    controller: LifecycleCameraController,
    context: Context,
    token: String,
    location: Location?,
    onPhotoTaken: (Bitmap) -> Unit,
    onProcessingDone: (() -> Unit)? = null
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                try {
                    val matrix = Matrix().apply {
                        postRotate(image.imageInfo.rotationDegrees.toFloat())
                    }

                    val rotatedBitmap = Bitmap.createBitmap(
                        image.toBitmap(),
                        0,
                        0,
                        image.width,
                        image.height,
                        matrix,
                        true
                    )

                    val address = getAddressFromLocation(context, location)

                    val bitmapWithWatermark = addWatermark(
                        context = context,
                        bitmap = rotatedBitmap,
                        location = location,
                        address = address
                    )
                    onPhotoTaken(bitmapWithWatermark)

                } catch (e: Exception) {
                    Log.e(
                        "CameraPatroli",
                        "Error processing photo: ${e.message}",
                        e
                    )
                    Toast.makeText(
                        context,
                        "Error processing photo: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    onProcessingDone?.invoke()
                    image.close()
                }
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Toast.makeText(
                    context,
                    "Ulangi mengambil gambar: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                onProcessingDone?.invoke()
            }
        }
    )
}

fun getCurrentLocationReal(
    context: Context,
    onLocationReady: (Location?) -> Unit
) {
    val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)
    if (
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }
        var called = false
        val callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (!called) {
                    called = true
                    fusedLocationClient.removeLocationUpdates(this)
                    onLocationReady(locationResult.lastLocation)
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            null
        )

        CoroutineScope(Dispatchers.Main).launch {
            delay(15000)
            if (!called) {
                fusedLocationClient.removeLocationUpdates(callback)
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { lastLocation ->
                        if (!called) {
                            called = true
                            if (lastLocation != null) {
                                onLocationReady(lastLocation)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Lokasi tidak ditemukan",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onLocationReady(null)
                            }
                        }
                    }
                    .addOnFailureListener {
                        if (!called) {
                            called = true
                            Toast.makeText(
                                context,
                                "Lokasi tidak ditemukan",
                                Toast.LENGTH_SHORT
                            ).show()
                            onLocationReady(null)
                        }
                    }
            }
        }
    } else {
        Toast.makeText(
            context,
            "Izin lokasi tidak diberikan",
            Toast.LENGTH_SHORT
        ).show()
        onLocationReady(null)
    }
}

private fun getAddressFromLocation(
    context: Context,
    location: Location?
): String {
    return if (location != null) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )
            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                "Alamat tidak ditemukan"
            }
        } catch (e: Exception) {
            Log.e("CameraPatroli", "Error getting address: ${e.message}", e)
            "Error mendapatkan alamat"
        }
    } else {
        "Lokasi tidak tersedia"
    }
}

private fun addWatermark(
    context: Context,
    bitmap: Bitmap,
    location: Location?,
    address: String
): Bitmap {
    val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(result)
    val paint = Paint().apply {
        color = Color.RED
        textSize = 85f
        isAntiAlias = true
    }

    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val dateTime = sdf.format(Date())

    val locationText =
        "Lat: ${location?.latitude ?: "Unknown"}, Lng: ${location?.longitude ?: "Unknown"}"
    val addressText = address

    val fullText = "$dateTime\n$locationText\n$addressText"

    val x = 10f
    val y = canvas.height - 500f

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val staticLayout = android.text.StaticLayout.Builder
            .obtain(
                fullText,
                0,
                fullText.length,
                android.text.TextPaint(paint),
                canvas.width - 20
            )
            .setAlignment(android.text.Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(1f, 1f)
            .setIncludePad(false)
            .build()
        canvas.save()
        canvas.translate(x, y)
        staticLayout.draw(canvas)
        canvas.restore()
    } else {
        val lines = fullText.split("\n")
        var currentY = y
        for (line in lines) {
            canvas.drawText(line, x, currentY, paint)
            currentY += paint.textSize + 10
        }
    }
    return result
}

private fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
    val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
    return Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        matrix,
        true
    )
}

private fun ImageProxy.toBitmap(): Bitmap {
    val buffer = this.planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

@Composable
fun RequestLocationPermissions() {
    val context = LocalContext.current
    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            Toast.makeText(
                context,
                "Location permission granted",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                "Location permission denied",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                    )
        ) {
            permissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}

@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        },
        modifier = modifier
    )
}

private fun triggerVibration(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                200,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } else {
        vibrator.vibrate(200)
    }
}