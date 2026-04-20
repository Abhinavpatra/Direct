package com.nexus.app.ui.screen.biometrics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexus.app.domain.model.WeightEntry
import com.nexus.app.ui.component.NexusMessageEffect
import com.nexus.app.ui.component.SectionCard
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun BiometricsRoute(
    viewModel: BiometricsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    NexusMessageEffect(uiState.message, snackbarHostState, viewModel::clearMessage)
    BiometricsScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onWeightChange = viewModel::onWeightChange,
        onNoteChange = viewModel::onNoteChange,
        onSave = viewModel::save,
        onEdit = viewModel::edit,
    )
}

@Composable
private fun BiometricsScreen(
    uiState: com.nexus.app.ui.state.BiometricsUiState,
    snackbarHostState: SnackbarHostState,
    onWeightChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onEdit: (Long) -> Unit,
) {
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text("Weight and biometrics", style = MaterialTheme.typography.headlineLarge)
            }
            item {
                SectionCard(title = "Log weight") {
                    if (uiState.editingId != 0L) {
                        Text(
                            text = "Editing existing entry",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }
                    OutlinedTextField(
                        value = uiState.weightText,
                        onValueChange = onWeightChange,
                        label = { Text("Weight in kg") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = uiState.note,
                        onValueChange = onNoteChange,
                        label = { Text("Note") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                    )
                    Button(
                        onClick = onSave,
                        modifier = Modifier.padding(top = 16.dp),
                    ) {
                        Text("Save entry")
                    }
                }
            }
            item {
                SectionCard(title = "Trend") {
                    WeightChart(
                        entries = uiState.entries,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                    )
                }
            }
            items(uiState.entries.reversed(), key = { it.id }) { entry ->
                SectionCard(title = formatDate(entry.measuredAtEpochMillis)) {
                    Text("${entry.weightKg} kg")
                    if (entry.note.isNotBlank()) {
                        Text(entry.note, modifier = Modifier.padding(top = 4.dp))
                    }
                    OutlinedButton(
                        onClick = { onEdit(entry.id) },
                        modifier = Modifier.padding(top = 12.dp),
                    ) {
                        Text("Edit")
                    }
                }
            }
        }
    }
}

@Composable
private fun WeightChart(
    entries: List<WeightEntry>,
    modifier: Modifier = Modifier,
) {
    if (entries.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("No weight history yet")
        }
        return
    }
    val minWeight = entries.minOf { it.weightKg }
    val maxWeight = entries.maxOf { it.weightKg }.let { if (it == minWeight) it + 1f else it }
    val pointColor = MaterialTheme.colorScheme.tertiary
    val lineColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier.padding(16.dp)) {
        val path = Path()
        val points = mutableListOf<Offset>()
        entries.forEachIndexed { index, entry ->
            val x = size.width * (index.toFloat() / (entries.lastIndex.coerceAtLeast(1)))
            val yRatio = (entry.weightKg - minWeight) / (maxWeight - minWeight)
            val y = size.height - (yRatio * size.height)
            points.add(Offset(x, y))
        }

        if (points.isNotEmpty()) {
            path.moveTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val cp1 = Offset((p0.x + p1.x) / 2f, p0.y)
                val cp2 = Offset((p0.x + p1.x) / 2f, p1.y)
                path.cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, p1.x, p1.y)
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 6f, cap = StrokeCap.Round),
        )

        points.forEach { point ->
            drawCircle(
                color = pointColor,
                radius = 7f,
                center = point,
            )
        }
    }
}

private fun formatDate(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"))
