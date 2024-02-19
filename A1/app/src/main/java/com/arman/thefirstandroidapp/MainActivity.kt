
package com.arman.thefirstandroidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arman.thefirstandroidapp.ui.components.AtomicButton
import com.arman.thefirstandroidapp.ui.components.AtomicProgressBar
import com.arman.thefirstandroidapp.ui.components.LocationColumn
import com.arman.thefirstandroidapp.ui.theme.Colors
import com.arman.thefirstandroidapp.ui.theme.TheFirstAndroidAppTheme
import java.math.RoundingMode
import java.text.DecimalFormat


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TheFirstAndroidAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainApplication()
                }
            }
        }
    }
}

data class Location(var name: String, var distanceToNext: Float)


@Composable
fun MainApplication() {
    val locationsMinimum = listOf(
        Location("New York", 0.0f),
        Location("Los Angeles", 8.0f),
        Location("Chicago", 6.0f),
        Location("Houston", 4.0f),
        Location("Phoenix", 7.0f),
        Location("Philadelphia", 3.0f),
        Location("San Antonio", 9.0f),
        Location("San Diego", 2.0f),
        Location("Dallas", 5.0f),
        Location("San Jose", 6.0f)
    )

    /** For Lazy Column */
    val locationsExtra = listOf(
        Location("Austin", 3.0f),
        Location("Seattle", 10.0f),
        Location("Miami", 7.0f),
        Location("Atlanta", 5.0f),
        Location("Denver", 6.0f)
    );

    /** Change this during the demo to demonstrate lazy column */
    val isLazyModeOn = true;
    val locations = if (!isLazyModeOn) {
        locationsMinimum;
    } else {
        locationsMinimum + locationsExtra;
    }


    /** States to be tracked */
    var atLocationIndex by remember {
        mutableIntStateOf(0)
    };
    var distanceInMiles by remember {
        mutableIntStateOf(0);
    }



    Column(modifier = Modifier
        .fillMaxSize()
        .background(Colors.zinc900)
        .padding(18.dp, 16.dp)
        ) {
        LocationColumn(isLazy = isLazyModeOn, locations = locations, atLocationIndex = atLocationIndex, distanceInMiles = distanceInMiles);

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            val totalDistance = locations.sumOf { it.distanceToNext.toDouble() }.toFloat();
            val coveredDistance = locations.subList(0, atLocationIndex + 1)
                .sumOf { it.distanceToNext.toDouble() }.toFloat();
            val progress = (coveredDistance / totalDistance) * 100;
            AtomicProgressBar(progress);
        }
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 8.dp)) {
            var coveredDistance = locations.subList(0, atLocationIndex + 1)
                .sumOf { it.distanceToNext.toDouble() }.toFloat();
            var totalDistance = locations.sumOf { it.distanceToNext.toDouble() }.toFloat();
            var leftDistance = totalDistance - coveredDistance;
            var distanceUnit = "Km";
            if (distanceInMiles == 1) {
                distanceUnit = "mi";
                coveredDistance = convertDistance(coveredDistance, true);
                totalDistance = convertDistance(totalDistance, true);
                leftDistance = convertDistance(leftDistance, true);
            }


            Text(text = "Distance covered: ${formatDistance(coveredDistance)} of ${formatDistance(totalDistance)} $distanceUnit", color = Colors.zinc300, fontSize = 14.sp);
            Text(text = "Distance left: ${formatDistance(leftDistance)} $distanceUnit", color = Colors.zinc300, fontSize = 14.sp);
            Text(text = "Stops: ${atLocationIndex + 1}/${locations.size}", color = Colors.zinc300, fontSize = 14.sp);

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AtomicButton(content = "Reset", size = Pair(174.dp, 48.dp)) {
                atLocationIndex = 0;
            }
            AtomicButton(content = "Next stop", size = Pair(174.dp, 48.dp)) {
                if (atLocationIndex < locations.size - 1) {
                    atLocationIndex++;

                }
            }

        }
        var conversionButtonText = "Km to miles";
        if (distanceInMiles == 1) {
            conversionButtonText = "Miles to Km";
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)) {
            AtomicButton(content = conversionButtonText, size = Pair(160.dp, 48.dp), fullWidth = true, outline = true) {
                distanceInMiles = if (distanceInMiles == 0) {
                    1;
                } else {
                    0;
                }
            }
        }
    }
}


@Preview(showBackground = true, device = Devices.DEFAULT)
@Composable
fun DefaultPreview() {
    TheFirstAndroidAppTheme {
        MainApplication()
    }
}

/** Utility methods go here **/

fun convertDistance(distance: Float, toMiles: Boolean ): Float {
    return if (toMiles) {
        distance * 0.621371f // Convert kilometers to miles
    } else {
        distance * 1.60934f // Convert miles to kilometers
    }
}

fun formatDistance(distance: Float): String {
    return DecimalFormat("#.##")
        .apply { roundingMode = RoundingMode.FLOOR }
        .format(distance).toString();
}
