package com.arman.thefirstandroidapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.arman.thefirstandroidapp.Location
import com.arman.thefirstandroidapp.ui.theme.Colors

@Composable
fun LocationColumn(isLazy: Boolean, locations: List<Location>, atLocationIndex: Number, distanceInMiles: Number) {
    val modifiers = Modifier
        .fillMaxWidth()
        .height(395.dp)
        .clip(shape = RoundedCornerShape(8.dp))
        .background(Colors.zinc800)
        .padding(vertical = 16.dp, horizontal = 18.dp)

    if (isLazy) {
        LazyColumn(modifier = modifiers) {
            items(locations.size) { index ->
                LocationEntry(location = locations[index], index = index, atLocationIndex = atLocationIndex, distanceInMiles = distanceInMiles);
            }
        }
    } else {
        Column(
            modifier = modifiers.verticalScroll(rememberScrollState())
        ) {
            for ((index, location) in locations.withIndex()) {
                LocationEntry(location = location, index = index, atLocationIndex = atLocationIndex, distanceInMiles = distanceInMiles);
            }
        }
    }

}