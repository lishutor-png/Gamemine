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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
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
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberRed
import com.example.ui.theme.HazardAmber
import com.example.ui.theme.NeonEmerald

@Composable
fun QuickBombDialog(
    rows: Int,
    cols: Int,
    currentMines: Int,
    onApplyMines: (newMines: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val totalCells = rows * cols
    val maxMines = (totalCells - 9).coerceAtLeast(1)
    var selectedMines by remember { mutableIntStateOf(currentMines.coerceIn(1, maxMines)) }

    val density = if (totalCells > 0) (selectedMines.toFloat() / totalCells) * 100f else 0f
    val dangerColor = when {
        density < 12f -> NeonEmerald
        density < 18f -> CyberCyan
        density < 25f -> HazardAmber
        else -> CyberRed
    }

    val dangerLabel = when {
        density < 12f -> "Kasual (10-12%)"
        density < 18f -> "Standar (15-18%)"
        density < 25f -> "Menantang (20-24%)"
        else -> "Ekstrem (25%+)"
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("quick_bomb_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberRed.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
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
                                .background(CyberRed.copy(alpha = 0.15f))
                                .border(1.dp, CyberRed, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Dangerous,
                                contentDescription = "Bomb Config",
                                tint = CyberRed,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TENTUKAN JUMLAH BOM",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "Medan $rows x $cols ($totalCells Kotak)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_quick_bomb_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Large Tactical Counter with Stepper Buttons
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(1.dp, CyberRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TOTAL RANJAU AKTIF",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = CyberRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$selectedMines",
                                fontSize = 38.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                color = CyberRed,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = " / $maxMines max",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 6.dp, top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Stepper row [-10] [-5] [-1] [+1] [+5] [+10]
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StepChip("-10") {
                                selectedMines = (selectedMines - 10).coerceAtLeast(1)
                            }
                            StepChip("-5") {
                                selectedMines = (selectedMines - 5).coerceAtLeast(1)
                            }
                            StepChip("-1") {
                                selectedMines = (selectedMines - 1).coerceAtLeast(1)
                            }
                            StepChip("+1") {
                                selectedMines = (selectedMines + 1).coerceAtMost(maxMines)
                            }
                            StepChip("+5") {
                                selectedMines = (selectedMines + 5).coerceAtMost(maxMines)
                            }
                            StepChip("+10") {
                                selectedMines = (selectedMines + 10).coerceAtMost(maxMines)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Smooth Slider
                Text(
                    text = "GESER UNTUK ATUR PRESISI:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
                Slider(
                    value = selectedMines.toFloat(),
                    onValueChange = { selectedMines = it.toInt().coerceIn(1, maxMines) },
                    valueRange = 1f..maxMines.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = CyberRed,
                        activeTrackColor = CyberRed,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                // Density Presets Row
                Text(
                    text = "PRESET KEPADATAN RANJAU:",
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
                    val p10 = ((totalCells * 0.10f).toInt()).coerceIn(1, maxMines)
                    val p15 = ((totalCells * 0.15f).toInt()).coerceIn(1, maxMines)
                    val p20 = ((totalCells * 0.20f).toInt()).coerceIn(1, maxMines)
                    val p25 = ((totalCells * 0.25f).toInt()).coerceIn(1, maxMines)

                    DensityPresetChip("Kasual ($p10💣)", isSelected = selectedMines == p10) {
                        selectedMines = p10
                    }
                    DensityPresetChip("Standar ($p15💣)", isSelected = selectedMines == p15) {
                        selectedMines = p15
                    }
                    DensityPresetChip("Pro ($p20💣)", isSelected = selectedMines == p20) {
                        selectedMines = p20
                    }
                    DensityPresetChip("Ekstrem ($p25💣)", isSelected = selectedMines == p25) {
                        selectedMines = p25
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Danger Analysis Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .border(1.dp, dangerColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(dangerColor.copy(alpha = 0.15f))
                                .border(1.dp, dangerColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = String.format("%.0f%%", density),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = dangerColor
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Tingkat Bahaya: $dangerLabel",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = dangerColor
                            )
                            Text(
                                text = "1 bom setiap ${(100f / density.coerceAtLeast(1f)).toInt()} kotak aman",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Apply Button
                Button(
                    onClick = {
                        onApplyMines(selectedMines)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("apply_quick_bomb_button"),
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
                        text = "MULAI MISI DENGAN $selectedMines BOM",
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
private fun StepChip(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DensityPresetChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isSelected) CyberCyan.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                1.dp,
                if (isSelected) CyberCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) CyberCyan else MaterialTheme.colorScheme.onSurface
        )
    }
}
