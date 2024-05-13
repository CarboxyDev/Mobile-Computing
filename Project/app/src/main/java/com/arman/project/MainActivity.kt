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
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Welcome to OpenConference", style = Typography.headlineMedium, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Just enter a username and your friend\'s room name to join them without any hassle!", style = Typography.labelLarge, textAlign = TextAlign.Center, color = Colors.zinc400);
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = {
            val intent = Intent(context, ProfileActivity::class.java);
            context.startActivity(intent);
        }, colors = ButtonDefaults.buttonColors(
            containerColor = Colors.sky500,
            contentColor = Colors.white
        ), modifier = Modifier.shadow(4.dp), shape = RoundedCornerShape(4.dp)
        ) {
            Text("Get started", color = Colors.white, style = Typography.bodyLarge)
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