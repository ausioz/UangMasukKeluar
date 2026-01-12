package com.nha.uangmasukkeluar.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pelanggan(
    val nama:String,
    val telepon: String?,
    val email:String?,
    val isMember:Boolean? = false,
) : Parcelable


val dummyList = listOf(
    Pelanggan("Tatiana Rosser", "08123456789", "tatianarosser@mail.com", true),
    Pelanggan("Erin Dorwart", null, null, null),
    Pelanggan("Corey Rosser", null, null, null),
    Pelanggan("Mira Workman", null, null, true),
    Pelanggan("Emery Kenter", null, null, null),
    Pelanggan("Alena Geidt", null, null, null),
    Pelanggan("Jocelyn Gouse", "08123456789", null, true),
    Pelanggan("Maren Culhane", null, null, null),
    Pelanggan("Marley Stanton", "08123456789", null, true),
    Pelanggan("Cooper Dias", null, "johndoe@mail.com", true),
)