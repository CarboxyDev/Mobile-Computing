
package com.arman.thefirstandroidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arman.thefirstandroidapp.ui.components.AtomicButton
import com.arman.thefirstandroidapp.ui.components.AtomicProgressBar
import com.arman.thefirstandroidapp.ui.theme.Colors
import com.arman.thefirstandroidapp.ui.theme.TheFirstAndroidAppTheme


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

data class Location(var name: String, var distanceToNext: Double)


@Composable
fun MainApplication() {
    var locations = listOf(
        Location("New York", 5.0),
        Location("Los Angeles", 8.0),
        Location("Chicago", 6.0),
        Location("Houston", 4.0),
        Location("Phoenix", 7.0),
        Location("Philadelphia", 3.0),
        Location("San Antonio", 9.0),
        Location("San Diego", 2.0),
        Location("Dallas", 5.0),
        Location("San Jose", 6.0)
    )

    /** States to be tracked */

    var atLocationIndex by remember {
        mutableIntStateOf(0)
    };



    Column(modifier = Modifier
        .fillMaxSize()
        .background(Colors.zinc900)
        .padding(18.dp, 16.dp)
        .verticalScroll(rememberScrollState())) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(Colors.zinc800)
                .shadow(4.dp)
                .padding(vertical = 16.dp, horizontal = 18.dp)
        ) {
            for ((index, location) in locations.withIndex()) {
                Row(modifier = Modifier
                    .padding(vertical = 6.dp)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val activeColor = if (atLocationIndex == index) {Colors.sky500} else {
                            Color.Transparent}
                        Box(
                            modifier = Modifier.padding(end = 6.dp).clip(shape = CircleShape).size(8.dp).background(activeColor)
                        )
                        Text(text = location.name, color = Colors.zinc200);
                    }

                    Text(text = location.distanceToNext.toString() + " Km", color = Colors.zinc400);
                }

            }
        }
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            val progress = (atLocationIndex.toDouble() / locations.size) * 100;
            AtomicProgressBar(progress);
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
                if (atLocationIndex < locations.size) {
                    atLocationIndex++;
                }

            }
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)) {
            AtomicButton(content = "Km to miles", size = Pair(160.dp, 48.dp), fullWidth = true) { }
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