package com.arman.assignment3;

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.arman.assignment3.ui.theme.Assignment3Theme


public class GraphActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = ThreadPolicy.Builder().permitAll().build()
        setThreadPolicy(policy)
        setContent {
            Assignment3Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PhoneOrientation()
                }
            }
        }
    }

}