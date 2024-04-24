package com.arman.assignment3

import android.content.Context
import android.content.res.Configuration
import android.hardware.SensorManager
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arman.assignment3.ui.theme.Assignment3Theme
import com.arman.assignment3.ui.theme.Colors


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setContent {
            Assignment3Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainApplication()
                }
            }
        }
    }
}

@Composable
fun PhoneOrientationActivity() {
    val sensorViewModel = viewModel<SensorViewModel>();
    val orientation by sensorViewModel.orientationAngles.collectAsState();

    val roll = orientation.roll;
    val pitch = orientation.pitch;
    val yaw = orientation.yaw;

    Column(){
        Text("Roll : $roll", color = Colors.white);
        Text("Pitch: $pitch", color = Colors.white);
        Text("Yaw  : $yaw", color = Colors.white);
    }
}

@Composable
fun MainApplication() {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Colors.zinc900)
        .padding(top = 64.dp, bottom = 18.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        PhoneOrientationActivity()
    }
}



@Preview(showBackground = true, device = Devices.DEFAULT)
@Composable
fun DefaultPreview() {
    Assignment3Theme {
        MainApplication()
    }
}

