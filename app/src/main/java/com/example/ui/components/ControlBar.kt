package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ControlMode
import com.example.domain.model.DifficultyLevel
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberRed
import com.example.ui.theme.HazardAmber

@Composable
fun ControlBar(
    controlMode: ControlMode,
    currentDifficulty: DifficultyLevel,
    totalMines: Int = 10,
    onModeChange: (ControlMode) -> Unit,
    onDifficultySelect: (DifficultyLevel) -> Unit,
    onQuickBombClick: () -> Unit = {},
    onCustomConfigClick: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onResetZoom: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Row 1: Tactical Primary Action Switcher (Dig vs Flag) & Zoom Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // DIG / FLAG Toggle Segment
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // DIG BUTTON
                    val isDig = controlMode == ControlMode.DIG
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDig) CyberCyan else Color.Transparent)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onModeChange(ControlMode.DIG) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("dig_mode_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = "Reveal Mode",
                                tint = if (isDig) Color(0xFF00363D) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "REVEAL",
                                fontSize = 12.sp,
                                fontWeight = if (isDig) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isDig) Color(0xFF00363D) else MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // FLAG BUTTON
                    val isFlag = controlMode == ControlMode.FLAG
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isFlag) CyberRed else Color.Transparent)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onModeChange(ControlMode.FLAG) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("flag_mode_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = "Flag Mode",
                                tint = if (isFlag) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FLAG",
                                fontSize = 12.sp,
                                fontWeight = if (isFlag) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isFlag) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                // ZOOM CONTROL CLUSTER
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onZoomOut() }
                            .testTag("zoom_out_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Zoom Out",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onResetZoom() }
                            .testTag("zoom_reset_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CropFree,
                            contentDescription = "Fit View",
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onZoomIn() }
                            .testTag("zoom_in_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Zoom In",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Difficulty & Custom Grid Sizer Chips (Scrollable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DifficultyChip(
                    title = "ROOKIE",
                    subtitle = "9x9 • 10💣",
                    isSelected = currentDifficulty == DifficultyLevel.EASY,
                    onClick = { onDifficultySelect(DifficultyLevel.EASY) }
                )

                DifficultyChip(
                    title = "SPECIALIST",
                    subtitle = "14x14 • 30💣",
                    isSelected = currentDifficulty == DifficultyLevel.MEDIUM,
                    onClick = { onDifficultySelect(DifficultyLevel.MEDIUM) }
                )

                DifficultyChip(
                    title = "MASTER",
                    subtitle = "18x14 • 48💣",
                    isSelected = currentDifficulty == DifficultyLevel.HARD,
                    onClick = { onDifficultySelect(DifficultyLevel.HARD) }
                )

                // Quick Bomb Adjuster Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberRed.copy(alpha = 0.15f))
                        .border(
                            1.dp,
                            CyberRed.copy(alpha = 0.5f),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onQuickBombClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("quick_bomb_bar_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Atur Jumlah Bom",
                            tint = CyberRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "ATUR BOM",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberRed
                            )
                            Text(
                                text = "$totalMines Bom • Ubah",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Custom Pro Grid Button
                val isCustom = currentDifficulty == DifficultyLevel.CUSTOM
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isCustom) HazardAmber.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            1.dp,
                            if (isCustom) HazardAmber else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onCustomConfigClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("custom_grid_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Custom Grid Setup",
                            tint = if (isCustom) HazardAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "CUSTOM PRO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCustom) HazardAmber else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Ubah Ukuran Kotak",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DifficultyChip(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) CyberCyan.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                1.dp,
                if (isSelected) CyberCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("difficulty_chip_${title.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                color = if (isSelected) CyberCyan else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
