package com.example.mobileapptechnobit

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NfcReaderViewModel: ViewModel() {
    private val _uidHex = MutableStateFlow<String?>(null)
    val uidHex = _uidHex.asStateFlow()

    fun onUidDetected(bytes: ByteArray) {
        //convert to hexadecimal
        _uidHex.value = bytes.joinToString(":") { "%02X".format(it) }
    }

    fun clearUid() {
        _uidHex.value = null
    }

    fun reversedDecimalToHex(uidDecimal: String): String {
        // 1️⃣ Ubah string desimal ke integer (Long biar aman)
        val uidValue = uidDecimal.toLong()

        // 2️⃣ Ubah ke 4 byte (karena UID NFC 4 byte)
        val bytes = ByteArray(4)
        for (i in 0 until 4) {
            bytes[i] = ((uidValue shr (8 * i)) and 0xFF).toByte()
        }

        // 3️⃣ Ubah ke format heksadesimal dengan separator ":"
        return bytes.joinToString(":") { "%02X".format(it) }
    }
}