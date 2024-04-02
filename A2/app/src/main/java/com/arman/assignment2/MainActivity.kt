package com.arman.assignment2

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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arman.assignment2.ui.theme.Assignment2Theme
import com.arman.assignment2.ui.theme.Colors
import com.arman.assignment2.ui.weather.WeatherViewModel
import com.arman.thefirstandroidapp.ui.components.AtomicButton
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setContent {
            Assignment2Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainApplication()
                }
            }
        }
    }
}


@Composable
fun WeatherRecord(
    date: MutableState<LocalDate>,
) {
    val viewModel = viewModel<WeatherViewModel>()
    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter.ofPattern("dd MMM yyyy").format(date.value)
        }
    }

    val isLoading by viewModel.isLoading.collectAsState();
    val errorMessage by viewModel.errorMessage.collectAsState();
    val weatherData by viewModel.weatherData.collectAsState();

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Date: $formattedDate", color = Colors.white)

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Text(text = "Loading...", color = Colors.white)
            }
            errorMessage != null -> {
                Text(text = "$errorMessage", color = Colors.red500)
            }
            weatherData != null -> {
                val temps = weatherData?.hourly?.temps?.filterNotNull();
                println("Printing temps -> $temps")
                if (temps != null) {
                    if (temps.isNotEmpty()) {
                        Text("Max temp: ${temps.maxOrNull()} °c", color = Colors.white)
                        Text("Min temp: ${temps.minOrNull()} °c", color = Colors.white)
                    } else {
                        Text("No weather data for this date", color = Colors.white)
                    }
                }
            }
            else -> {  // Initial State
                Text("No weather data yet", color =Colors.white)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AtomicButton(
            size = Pair(200.dp, 48.dp),
            content = "Get Temperatures"

        ) {
            val latitude = 28.7041f;
            val longitude = 77.1025f;
            val startDate = date.value.plusDays(-1).toString()
            val endDate = date.value.toString()
            viewModel.fetchHistoricalWeather(latitude, longitude, startDate, endDate)
        }
    }
}


@Composable
fun MainApplication() {

    val pickedDate = remember {
        mutableStateOf(LocalDate.now())
    }


    val openDialog = remember { mutableStateOf(false) }
    val dialogState = rememberMaterialDialogState()

    // API URL: "https://archive-api.open-meteo.com/v1/archive?latitude=52.55&longitude=13.41&hourly=temperature_2m&start_date={start_date}&end_date={end_date}"

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Colors.zinc900)
        .padding(top = 64.dp, bottom = 18.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Column () {
            AtomicButton(content = "Pick a date", outline = true, size = Pair(200.dp, 48.dp)) {
                openDialog.value = true
                dialogState.show();
            }
        }
        WeatherRecord(
            date = pickedDate,
        )
        MaterialDialog(
            dialogState = dialogState,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            onCloseRequest = {
                /** Called when the dialog is closing */
            },
            buttons = {
                positiveButton(text = "OK") {

                }
                negativeButton(text = "Cancel") {

                }
            }

        ) {
            datepicker(
                initialDate = pickedDate.value,
                title = "Pick a date",
                allowedDateValidator = { it.isBefore(LocalDate.now()) },
            ) {
                pickedDate.value = it
            }
        }
    }
}



@Preview(showBackground = true, device = Devices.DEFAULT)
@Composable
fun DefaultPreview() {
    Assignment2Theme {
        MainApplication()
    }
}
