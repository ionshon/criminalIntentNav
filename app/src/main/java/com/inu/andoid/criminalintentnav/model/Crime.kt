package com.inu.andoid.criminalintentnav.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "crime_table")
data class Crime(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    val title: String = "",
    val date: Date = Date(),
    val isSolved: Boolean = false,
    var suspect: String = ""
): Parcelable
