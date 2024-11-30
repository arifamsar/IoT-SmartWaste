package com.arfsar.smarttrash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SensorViewModel(private val repository: SensorRepository): ViewModel() {

    var capacity by mutableFloatStateOf(0f)
        private set
    var timestamp by mutableStateOf("")
        private set
    var nh3 by mutableFloatStateOf(0f)
        private set
    var co2 by mutableFloatStateOf(0f)
        private set
    var acetone by mutableFloatStateOf(0f)
        private set

    private var fetchJob: Job? = null


    init {
        startFetchingData()
    }

    private fun startFetchingData() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            while (true) {
                repository.fetchSensorData { sensorData ->
                    capacity = sensorData?.kapasitas?.toFloat() ?: 0f
                    timestamp = sensorData?.timestamp ?: ""
                    nh3 = sensorData?.nh3 ?: 0f
                    co2 = sensorData?.co2 ?: 0f
                    acetone = sensorData?.acetone ?: 0f
                }
                delay(5000)
            }
        }
    }
}