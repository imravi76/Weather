package com.revengeos.weather.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "city")
data class City (
    @PrimaryKey(autoGenerate = true) var cityId: Long?,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "currenttemp") var currentTemp: String,
    @ColumnInfo(name = "weathericon") var weatherIcon: String
) : Serializable
