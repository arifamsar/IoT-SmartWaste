package com.arfsar.smarttrash

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SensorRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun fetchSensorData(onDataFetched: (SensorModel?) -> Unit) {
        database.child("sensor").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sensorData = snapshot.getValue(SensorModel::class.java)
                onDataFetched(sensorData)
            }

            override fun onCancelled(error: DatabaseError) {
                onDataFetched(null)
            }
        })
    }
}