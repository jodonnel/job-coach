package com.jodonnel.jobcoach.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                JobCoachScreen()
            }
        }
    }
}

@Composable
fun JobCoachScreen(viewModel: JobCoachViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Job Coach",
                style = MaterialTheme.typography.headlineLarge
            )

            // Connection status
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Glasses",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = when (uiState.glassesConnected) {
                            true -> "Connected"
                            false -> "Disconnected"
                        },
                        color = if (uiState.glassesConnected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }
            }

            // Profile
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = uiState.profileName)
                }
            }

            // Listening status
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Audio",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (uiState.listening) "Listening..." else "Idle"
                    )
                    if (uiState.lastTranscription.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.lastTranscription,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Last event sent
            if (uiState.lastEvent.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Last Event",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = uiState.lastEvent,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Connect / Listen buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.toggleConnection() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (uiState.glassesConnected) "Disconnect" else "Connect")
                }

                Button(
                    onClick = { viewModel.toggleListening() },
                    enabled = uiState.glassesConnected,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (uiState.listening) "Stop" else "Listen")
                }
            }
        }
    }
}
