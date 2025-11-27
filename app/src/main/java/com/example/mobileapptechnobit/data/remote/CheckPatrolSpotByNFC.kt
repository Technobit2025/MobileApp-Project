package com.example.mobileapptechnobit.data.remote

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class CheckNfcRequest(
    val nfcTagUid: String
)

data class CheckPatrolSpotResponse(
    val status:String,
    val message:String,
    val data: PatrolSpot
)
@Parcelize
data class PatrolSpot(
    val companyId: Int? = 0,
    val id: Int,
    val title: String,
    val address: String,
    val latitude: String?,
    val longitude:String?,
    val description:String?,
    var nfcTagUid: String?
): Parcelable

sealed class Resource<T> {
    class Idle<T> : Resource<T>()
    class Loading<T> : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String) : Resource<T>()
}