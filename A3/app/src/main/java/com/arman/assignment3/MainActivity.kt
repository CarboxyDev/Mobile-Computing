package com.arman.assignment3

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arman.assignment3.ui.theme.Assignment3Theme
import com.arman.assignment3.ui.theme.Colors



public class MainActivity : ComponentActivity() {
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
fun PhoneOrientation() {
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
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Colors.zinc900)
        .padding(top = 64.dp, bottom = 18.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        PhoneOrientation()
        Column(modifier = Modifier.padding(top = 20.dp)) {
            Button(onClick = {
                val intent = Intent(context, GraphActivity::class.java)
                context.startActivity(intent)
            }) {
                Text(text = "Graph Activity", color =  Colors.white);
            }
        }

    }
}



@Preview(showBackground = true, device = Devices.DEFAULT)
@Composable
fun DefaultPreview() {
    Assignment3Theme {
        MainApplication()
    }
}

