package com.nexus.app.ui.screen.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexus.app.ui.component.NexusMessageEffect
import com.nexus.app.ui.component.SectionCard

@Composable
fun InputRoute(
    viewModel: InputViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    NexusMessageEffect(uiState.message, snackbarHostState, viewModel::clearMessage)
    InputScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onInputChange = viewModel::onInputChange,
        onSubmit = viewModel::submit,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputScreen(
    uiState: com.nexus.app.ui.state.InputUiState,
    snackbarHostState: SnackbarHostState,
    onInputChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.background,
                            Color.Transparent,
                        ),
                    ),
                )
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Capture task in plain language",
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                text = "Describe task, time, and why it matters. Nexus validates AI output before saving.",
                style = MaterialTheme.typography.bodyLarge,
            )
            SectionCard(title = "Natural-language input") {
                OutlinedTextField(
                    value = uiState.input,
                    onValueChange = onInputChange,
                    label = { Text("Example: Remind me to call mom tomorrow at 7pm because I promised") },
                    minLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = onSubmit,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 8.dp),
                            strokeWidth = 2.dp,
                        )
                    }
                    Text("Parse and save")
                }
            }
            SectionCard(title = "Validation rules") {
                Text("Reason required. Time must be future ISO-8601 after parsing. Action type restricted to reminder, call, meeting, or other.")
            }
        }
    }
}
