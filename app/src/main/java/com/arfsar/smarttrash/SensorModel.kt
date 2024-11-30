package com.arfsar.smarttrash

import com.google.firebase.database.PropertyName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SensorModel(
    val jarak: Int = 0,
    val kapasitas: String = "",
    val timestamp: String = "",

    @get:PropertyName("NH3")
    @set:PropertyName("NH3")
    var nh3: Float = 0f,

    @get:PropertyName("CO2")
    @set:PropertyName("CO2")
    var co2: Float = 0f,

    @get:PropertyName("Acetone")
    @set:PropertyName("Acetone")
    var acetone: Float = 0f,
)