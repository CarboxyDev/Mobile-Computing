package com.arman.assignment3

import android.app.Application
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import androidx.lifecycle.AndroidViewModel
import com.arman.assignment3.data.DatabaseRepository
import com.arman.assignment3.data.db.OrientationAnglesModel
import com.arman.assignment3.data.db.OrientationEntity

private val SMOOTH_ALPHA = 0.7f;

fun smoothen(curr: Float, prev: Float): Float {
    return (prev * SMOOTH_ALPHA) + (curr * (1 - SMOOTH_ALPHA));
}

fun roundFloat(num: Float): Float {
    val decimalFormatter = DecimalFormat("#.##")
    return decimalFormatter.format(num).toFloat();
}

/**
 * ViewModel for the sensor data
 */
class SensorViewModel(application: Application) : AndroidViewModel(application) {
    private val sensorManager: SensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager;
    private val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    private var angles = MutableStateFlow(OrientationAnglesModel(0f, 0f, 0f));
    val orientationAngles: StateFlow<OrientationAnglesModel> = angles.asStateFlow();

    private val databaseRepoStore = DatabaseRepository(
        context = getApplication(),
    )
    private val orientationDao = databaseRepoStore.getOrientationDao();

    private var lastOrientationAngles = OrientationAnglesModel(0f, 0f, 0f)


    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                calculateOrientation(event.values)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            Log.d(
                "Sensor Accuracy",
                "Sensor: ${sensor}, Accuracy: $accuracy"
            )
        }
    }

    init {
        startListening()
    }

    private fun calculateOrientation(values: FloatArray) {
        if (values.size < 3) {
            Log.d(
                "Size Error",
                "Insufficient size of values array"
            )
            return
        }

        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, values)

        val orientationAngles = FloatArray(3)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)


        var yaw = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
        yaw = smoothen(yaw, lastOrientationAngles.yaw);

        var pitch = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()
        pitch = smoothen(pitch, lastOrientationAngles.pitch);

        var roll = Math.toDegrees(orientationAngles[1].toDouble()).toFloat();
        roll = smoothen(roll, lastOrientationAngles.roll);




        if (roll.isNaN()) {
            roll = 0F
        }

        roll = roundFloat(roll);
        pitch = roundFloat(pitch);
        yaw = roundFloat(yaw);

        angles.value = OrientationAnglesModel(roll, pitch, yaw)

        saveOrientation(roll, pitch, yaw)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveOrientation(roll: Float, pitch: Float, yaw: Float) {
        GlobalScope.launch(Dispatchers.IO) {
            orientationDao.insertOrientationData(OrientationEntity(roll = roll, pitch = pitch, yaw = yaw))
        }
    }

    private fun startListening() {
        sensorManager.registerListener(
            sensorListener,
            rotationSensor,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(sensorListener)
    }
}