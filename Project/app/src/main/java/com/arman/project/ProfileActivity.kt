package com.arman.project


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arman.project.ui.theme.Colors
import com.arman.project.ui.theme.ProjectTheme
import com.arman.project.ui.theme.Typography

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProfileSetup()
                }
            }
        }
    }
}

@Composable
fun ProfileSetup() {
    val context = LocalContext.current;
    var username by remember { mutableStateOf("User") }
    var password by remember { mutableStateOf("") }
    val viewModel = viewModel<TokenViewModel>();

    val data by viewModel.data.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Handle navigation when token is received
    LaunchedEffect(data) {
        val token = data?.token;
        println("Token received: $token")
        token?.let {
            val intent = Intent(context, ConferenceActivity::class.java).apply {
                putExtra("token", it)
            }
            context.startActivity(intent)
            (context as Activity).finish()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }


    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Profile Setup", style = Typography.headlineSmall, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 8.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Only username required, password is only for moderators", style = Typography.labelLarge, textAlign = TextAlign.Center, color = Colors.zinc400);
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Colors.white,
                unfocusedContainerColor = Colors.zinc800,
                focusedIndicatorColor = Colors.sky500,
                unfocusedIndicatorColor = Colors.sky500
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Password (optional)") },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Colors.white,
                unfocusedContainerColor = Colors.zinc800,
                focusedIndicatorColor = Colors.sky500,
                unfocusedIndicatorColor = Colors.sky500
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            viewModel.fetchToken(username, password)
        }, colors = ButtonDefaults.buttonColors(
            containerColor = Colors.sky500,
            contentColor = Colors.white
        ), modifier = Modifier.shadow(4.dp), shape = RoundedCornerShape(4.dp)) {
            Text(text = "Continue", style = Typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (isLoading) {
            Text("Creating user...", style = Typography.bodyMedium)
        }
    }
}
