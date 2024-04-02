package com.arman.assignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.arman.assignment2.ui.theme.Assignment2Theme
import com.arman.assignment2.ui.theme.Colors
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
fun MainApplication() {

    val pickedDate = remember {
        mutableStateOf(LocalDate.now())
    }
    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("dd MMM yyyy")
                .format(
                    pickedDate.value
                )
        }
    }

    val maxTemp = remember { mutableFloatStateOf(-1000f) }
    val minTemp = remember { mutableFloatStateOf(-1000f) }

    val openDialog = remember { mutableStateOf(false) }
    val dialogState = rememberMaterialDialogState()

    val apiUrl = "https://archive-api.open-meteo.com/v1/archive?latitude=52.55&longitude=13.41&hourly=temperature_2m&start_date={start_date}&end_date={end_date}"

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
        Column(modifier = Modifier.padding(top = 64.dp)) {
            Text(text = "Date: $formattedDate", color = Colors.white)
            if (
                maxTemp.floatValue == -1000f ||
                minTemp.floatValue == -1000f
            ) {
                Text("Max temp: Pending", color = Colors.white)
                Text("Min temp: Pending", color = Colors.white)
            } else {
                Text("Max temp: ${maxTemp.floatValue} °c", color = Colors.white)
                Text("Min temp: ${minTemp.floatValue} °c", color = Colors.white)
            }
        }
        Column(modifier = Modifier.padding(top = 64.dp)) {
            AtomicButton(content = "Get Temps", size = Pair(200.dp, 48.dp)) {
                // Call the API here
                maxTemp.floatValue = 25.0f
                minTemp.floatValue = 15.0f
            }
        }
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
