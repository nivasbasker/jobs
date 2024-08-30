package com.zio.jobs.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "jobs")
data class Job(
    @PrimaryKey
    val id: Int,
    val title: String,
    val companyName: String,
    val jobRole: String,
    val jobHours: String,
    val otherDetails: String,

    val jobCategory: String,
    val openingsCount: Int,
    val numApplications: Int,
    val salaryMax: Int,
    val salaryMin: Int,

    val createdOn: String,
    val place: String,
    val salary: String,
    val experience: String,
    val qualification: String,
    val file: String,
    val thumbUrl: String
) : Parcelable {

}

