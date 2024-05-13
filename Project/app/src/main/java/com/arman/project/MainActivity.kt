package com.arman.project

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.arman.project.ui.theme.Colors
import com.arman.project.ui.theme.ProjectTheme
import com.arman.project.ui.theme.Typography

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApplication()
                }
            }
        }
    }


}

@Composable
fun MainApplication() {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Welcome to OpenConference", style = Typography.headlineMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 8.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Click the button below to get started!", style = Typography.labelLarge, textAlign = TextAlign.Center, color = Colors.zinc400);
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {
            val intent = Intent(context, ConferenceActivity::class.java);
            context.startActivity(intent);
        }, colors = ButtonDefaults.buttonColors(
            containerColor = Colors.sky500,
            contentColor = Colors.white
        ), modifier = Modifier.shadow(4.dp), shape = RoundedCornerShape(4.dp)
        ) {
            Text("Start a Conference", color = Colors.white, style = Typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProjectTheme {
        MainApplication();
    }
}