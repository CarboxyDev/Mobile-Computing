package com.arman.thefirstandroidapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arman.thefirstandroidapp.ui.theme.Colors



@Composable
fun AtomicButton(content: String, size: Pair<Dp, Dp>, fullWidth: Boolean? = false, onClick: () -> Unit) {
    var modifier = if (fullWidth == true) {
        Modifier.fillMaxWidth().height(size.second);
    } else {
        Modifier.size(size.first, size.second)
    }

    Button(onClick = onClick, colors = ButtonDefaults.buttonColors(
        containerColor = Colors.sky500,
        contentColor = Colors.white
    ), modifier = modifier.shadow(4.dp), shape = RoundedCornerShape(4.dp)) {
        Text(text = content)
    }
}