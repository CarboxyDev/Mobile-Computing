package com.arman.thefirstandroidapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arman.thefirstandroidapp.Location
import com.arman.thefirstandroidapp.convertDistance
import com.arman.thefirstandroidapp.formatDistance
import com.arman.thefirstandroidapp.ui.theme.Colors


@Composable
fun LocationEntry(location: Location, index: Number, atLocationIndex: Number, distanceInMiles: Number){
    Row(modifier = Modifier
        .padding(vertical = 6.dp)
        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val activeColor = if (atLocationIndex == index) {
                Colors.sky500} else {
                Color.Transparent}
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(shape = CircleShape)
                    .size(8.dp)
                    .background(activeColor)
            )
            Text(text = location.name, color = Colors.zinc200);
        }
        var distanceUnit = " Km";
        var distance = location.distanceToNext;
        if (distanceInMiles == 1) {
            distanceUnit = " mi"
            distance = convertDistance(distance, true);
        }
        Text(text = formatDistance(distance) + distanceUnit, color = Colors.zinc400);
    }



}