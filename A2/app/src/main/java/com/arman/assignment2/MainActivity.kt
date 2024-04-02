package com.arman.assignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    maxTemp: MutableState<Float> = mutableFloatStateOf(-1000f), // Default pending value
    minTemp: MutableState<Float> = mutableFloatStateOf(-1000f), // Default pending value
) {
    val viewModel = viewModel<WeatherViewModel>()
    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter.ofPattern("dd MMM yyyy").format(date.value)
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Date: $formattedDate", color = Colors.white)

        Spacer(modifier = Modifier.height(16.dp))

        // Weather data display (conditional)
        when {
            viewModel.isLoading.value == true -> {
                CircularProgressIndicator()
            }
            viewModel.errorMessage.value != null -> {
                Text(text = "Error: ${viewModel.errorMessage.value}", color = Colors.white)
            }
            else -> {
                Text("Max temp: ${maxTemp.value} °c", color = Colors.white)
                Text("Min temp: ${minTemp.value} °c", color = Colors.white)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AtomicButton(
            onClick = {
                val latitude = 52.55f
                val longitude = 13.41f
                val startDate = date.value.toString()
                val endDate = date.value.plusDays(1).toString()
                viewModel.fetchHistoricalWeather(latitude, longitude, startDate, endDate)
            },
            size = Pair(200.dp, 48.dp),
            content = "Get Temperatures"

        )
    }
}


@Composable
fun MainApplication() {

    val pickedDate = remember {
        mutableStateOf(LocalDate.now())
    }


    val maxTemp = remember { mutableFloatStateOf(-1000f) }
    val minTemp = remember { mutableFloatStateOf(-1000f) }

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
            maxTemp = maxTemp,
            minTemp = minTemp
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
