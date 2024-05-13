package com.arman.project

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.arman.project.ui.theme.Colors
import com.arman.project.ui.theme.ProjectTheme
import com.arman.project.ui.theme.Typography
import org.jitsi.meet.sdk.BroadcastEvent
import org.jitsi.meet.sdk.BroadcastIntentHelper
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import timber.log.Timber
import java.net.MalformedURLException
import java.net.URL

/**
 * Some secret info:
 * AppId: vpaas-magic-cookie-a4a17f5348dc4ac099eb24c42a83bc7a
 *
 */

class ConferenceActivity : ComponentActivity() {
    val appId = "vpaas-magic-cookie-a4a17f5348dc4ac099eb24c42a83bc7a";
    private var token: String? = null;
    val context = this;

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onBroadcastReceived(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);

        token = intent.getStringExtra("token") ?: run {
            finish()
            return
        }


        val serverURL: URL;

        serverURL = try {
            URL("https://8x8.vc")
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw RuntimeException("Invalid server URL!")
        }
        println("ConferenceActivity -> Token: $token")
        // Default options for meets
        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            // When using JaaS, set the obtained JWT here
            .setToken("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InZwYWFzLW1hZ2ljLWNvb2tpZS1hNGExN2Y1MzQ4ZGM0YWMwOTllYjI0YzQyYTgzYmM3YS80MmQ1ZjUifQ.eyJhdWQiOiJqaXRzaSIsImNvbnRleHQiOnsidXNlciI6eyJpZCI6IjdmY2Q1YjBmLWVmNjQtNDE3Yi1iZDMyLWYyMzNjNDJjN2VjZiIsIm5hbWUiOiJkb2ciLCJtb2RlcmF0b3IiOnRydWV9fSwiaXNzIjoidnBhYXMtbWFnaWMtY29va2llLWE0YTE3ZjUzNDhkYzRhYzA5OWViMjRjNDJhODNiYzdhIiwic3ViIjoiaHR0cHM6Ly84eDgudmMiLCJleHAiOjE3MTU2MTU0MDgsIm5iZiI6MTcxNTYxMTUwOCwiaWF0IjoxNzE1NjExODA4fQ.SUhO9ie4qHvTdACHzxXFf3kddM7bJldvWwOcYariUVXh93B-bU8ARvRdPXPn-hskk2W8o2jIpoOwN_bXhniQ_MwvfGlBQCkZTy9yXNqABL6Ebri1FqPZRDiLB4MHkUW7KF8KBtA2yiSBWJWxZjV2J9L5HhTwrs8s9KxsVyoOzKZrygW9ngzekKOvYD9rSz88PZZsWBe38rx8oR2b0M_NPC7is6RSRAFJdLYDFNMnd5cDeudJ4nAlcArNc8qmLhHyC8Pyw4LJ10eV-OTC5WibjY3eQ04M5VVZR1c5CbYKUVzM1HtRWuN1IBn3lpMRnOZ53P3DABqxaEg4R9GZK7p9Eg")
            .setFeatureFlag("welcomepage.enabled", false)
            .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        registerForBroadcastMessages()
        setContent {
            ProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ConferenceScreen(
                        appId = appId
                    )
                }
            }
        }
    }
    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }



    private fun registerForBroadcastMessages() {
        val intentFilter = IntentFilter()

        /* This registers for every possible event sent from JitsiMeetSDK
           If only some of the events are needed, the for loop can be replaced
           with individual statements:
           ex:  intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.action);
                intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.action);
                ... other events
         */
        for (type in BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.action)
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter)
    }

    // Example for handling different JitsiMeetSDK events
    private fun onBroadcastReceived(intent: Intent?) {
        if (intent != null) {
            val event = BroadcastEvent(intent)
            when (event.type) {
                BroadcastEvent.Type.CONFERENCE_JOINED -> Timber.i("Conference Joined with url%s", event.getData().get("url"))
                BroadcastEvent.Type.PARTICIPANT_JOINED -> Timber.i("Participant joined%s", event.getData().get("name"))
                else -> Timber.i("Received event: %s", event.type)
            }
        }
    }

    private fun hangUp() {
        val hangupBroadcastIntent: Intent = BroadcastIntentHelper.buildHangUpIntent()
        LocalBroadcastManager.getInstance(this.applicationContext).sendBroadcast(hangupBroadcastIntent)

    }

}

@Composable
fun ConferenceScreen(appId: String) {
    var conferenceName by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Colors.zinc900)
        .padding(top = 64.dp, bottom = 18.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Text("Join Conference", style = Typography.headlineSmall, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 8.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Enter the same conference name as your friend to join their room", style = Typography.labelLarge, textAlign = TextAlign.Center, color = Colors.zinc400);
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = conferenceName,
            onValueChange = { conferenceName = it },
            label = { Text("Conference name") },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Colors.white,
                unfocusedContainerColor = Colors.zinc800,
                focusedIndicatorColor = Colors.sky500,
                unfocusedIndicatorColor = Colors.sky500
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Colors.sky500,
                contentColor = Colors.white
            ), modifier = Modifier.shadow(4.dp).align(Alignment.CenterHorizontally), shape = RoundedCornerShape(4.dp),
            onClick = {
                /** Launch the JitsiMeetActivity with the conference name */
                if (conferenceName.isNotEmpty()) {
                    val options = JitsiMeetConferenceOptions.Builder()
                        .setRoom("$appId/$conferenceName")
                        .setVideoMuted(true)
                        .setFeatureFlag("invite.enabled", false)
                        .setFeatureFlag("breakout-rooms.enabled", false)
                        .setFeatureFlag("calendar.enabled", false)
                        .setFeatureFlag("lobby-mode.enabled", false)
                        .build()
                    JitsiMeetActivity.launch(context, options)
                } else {
                    Toast.makeText(context, "Please enter a conference name", Toast.LENGTH_SHORT).show()
                }
            },

        ) {
            Text("Join Conference")
        }

    }
}