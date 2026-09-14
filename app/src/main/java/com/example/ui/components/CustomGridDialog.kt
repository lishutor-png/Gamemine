package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.model.CustomConfig
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberRed
import com.example.ui.theme.HazardAmber
import com.example.ui.theme.NeonEmerald

@Composable
fun CustomGridDialog(
    initialConfig: CustomConfig,
    onApply: (rows: Int, cols: Int, mines: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var rows by remember { mutableIntStateOf(initialConfig.rows.coerceIn(6, 24)) }
    var cols by remember { mutableIntStateOf(initialConfig.cols.coerceIn(6, 20)) }

    val maxMines = ((rows * cols) - 9).coerceAtLeast(1)
    var mines by remember { mutableIntStateOf(initialConfig.mines.coerceIn(1, maxMines)) }

    // Re-clamp mines if rows or cols change
    if (mines > maxMines) {
        mines = maxMines
    }

    val totalCells = rows * cols
    val densityPercent = if (totalCells > 0) (mines.toFloat() / totalCells) * 100f else 0f

    val dangerColor = when {
        densityPercent < 12f -> NeonEmerald
        densityPercent < 18f -> CyberCyan
        densityPercent < 25f -> HazardAmber
        else -> CyberRed
    }

    val dangerLabel = when {
        densityPercent < 12f -> "Rendah (Kasual)"
        densityPercent < 18f -> "Seimbang (Standar)"
        densityPercent < 25f -> "Tinggi (Tantangan Pro)"
        else -> "Ekstrem (Zona Mematikan)"
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("custom_grid_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, HazardAmber.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(HazardAmber.copy(alpha = 0.15f))
                                .border(1.dp, HazardAmber, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridOn,
                                contentDescription = "Custom Grid",
                                tint = HazardAmber,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "PENGATURAN KOTAK PRO",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Atur Dimensi & Jumlah Ranjau",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_custom_grid_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Presets Horizontal Row
                Text(
                    text = "PRESET CEPAT:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PresetChip("Mini 8x8 (10💣)") {
                        rows = 8; cols = 8; mines = 10
                    }
                    PresetChip("Standard 10x10 (15💣)") {
                        rows = 10; cols = 10; mines = 15
                    }
                    PresetChip("Specialist 14x14 (30💣)") {
                        rows = 14; cols = 14; mines = 30
                    }
                    PresetChip("Pro Master 16x16 (40💣)") {
                        rows = 16; cols = 16; mines = 40
                    }
                    PresetChip("Titan 20x15 (55💣)") {
                        rows = 20; cols = 15; mines = 55
                    }
                    PresetChip("Colossus 24x18 (80💣)") {
                        rows = 24; cols = 18; mines = 80
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Slider 1: Columns (Lebar)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Lebar (Kolom)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$cols Kotak",
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                    }
                    Slider(
                        value = cols.toFloat(),
                        onValueChange = { cols = it.toInt() },
                        valueRange = 6f..20f,
                        steps = 13,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberCyan,
                            activeTrackColor = CyberCyan
                        )
                    )
                }

                // Slider 2: Rows (Tinggi)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tinggi (Baris)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$rows Baris",
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                    }
                    Slider(
                        value = rows.toFloat(),
                        onValueChange = { rows = it.toInt() },
                        valueRange = 6f..24f,
                        steps = 17,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberCyan,
                            activeTrackColor = CyberCyan
                        )
                    )
                }

                // Slider 3: Mines Count
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Jumlah Ranjau",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$mines Bom",
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CyberRed
                        )
                    }
                    Slider(
                        value = mines.toFloat(),
                        onValueChange = { mines = it.toInt().coerceIn(1, maxMines) },
                        valueRange = 1f..maxMines.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = CyberRed,
                            activeTrackColor = CyberRed
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Danger / Density Analysis Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .border(1.dp, dangerColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Danger Rating",
                            tint = dangerColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "ANALISIS MEDAN RANJAU",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = String.format("%.1f%% Kepadatan", densityPercent),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = dangerColor
                                )
                            }
                            Text(
                                text = "$dangerLabel • Total $totalCells Kotak",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Button
                Button(
                    onClick = {
                        onApply(rows, cols, mines)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("apply_custom_grid_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan,
                        contentColor = Color(0xFF00363D)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TERAPKAN & MULAI MISI ($rows x $cols)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PresetChip(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
