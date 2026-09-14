package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.GameStatus
import com.example.domain.model.OperatorMood
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberRed
import com.example.ui.theme.HazardAmber
import com.example.ui.theme.NeonEmerald

@Composable
fun TacticalHud(
    status: GameStatus,
    operatorMood: OperatorMood,
    totalMines: Int,
    flagsPlaced: Int,
    elapsedSeconds: Long,
    isDarkMode: Boolean,
    isAutosaved: Boolean,
    onResetClick: () -> Unit,
    onToggleThemeClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onStatsClick: () -> Unit,
    onBombsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val remainingMines = (totalMines - flagsPlaced).coerceAtLeast(-99)
    val formattedMines = if (remainingMines >= 0) {
        String.format("%03d", remainingMines)
    } else {
        String.format("-%02d", -remainingMines)
    }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val formattedTimer = String.format("%02d:%02d", minutes, seconds)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Header: Title & Action icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, CyberCyan, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = "Tactical Icon",
                            tint = CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "BOMB DISPOSAL",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        when (status) {
                                            GameStatus.RUNNING -> CyberCyan.copy(alpha = 0.15f)
                                            GameStatus.WON -> NeonEmerald.copy(alpha = 0.2f)
                                            GameStatus.LOST -> CyberRed.copy(alpha = 0.2f)
                                            else -> HazardAmber.copy(alpha = 0.15f)
                                        }
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = when (status) {
                                        GameStatus.RUNNING -> "ACTIVE"
                                        GameStatus.WON -> "DEFUSED"
                                        GameStatus.LOST -> "DETONATED"
                                        GameStatus.PAUSED -> "PAUSED"
                                        GameStatus.IDLE -> "READY"
                                    },
                                    color = when (status) {
                                        GameStatus.RUNNING -> CyberCyan
                                        GameStatus.WON -> NeonEmerald
                                        GameStatus.LOST -> CyberRed
                                        else -> HazardAmber
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Text(
                            text = "OPERATOR PROTOCOL // DEFUSE UNIT",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                // Action Icons (Theme, Leaderboard, Stats)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleThemeClick,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Dark Mode",
                            tint = if (isDarkMode) HazardAmber else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onLeaderboardClick,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("leaderboard_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Leaderboard,
                            contentDescription = "Leaderboard",
                            tint = CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onStatsClick,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("stats_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueryStats,
                            contentDescription = "Global Statistics",
                            tint = NeonEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tactical Instrument Cluster (Mines Counter, Operator Face/Reset, Timer)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bomb Counter Display (Tap to configure custom bomb count)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onBombsClick)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .testTag("hud_bomb_counter_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Bombs Remaining - Tap to change",
                        tint = CyberRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ORDNANCE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "• ATUR",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyberRed.copy(alpha = 0.8f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF070B12))
                                .border(1.dp, CyberRed.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = formattedMines,
                                color = CyberRed,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }

                // Operator Face / Quick Reset Button
                Box(
                    modifier = Modifier
                        .scale(if (status == GameStatus.RUNNING) 1f else pulseScale)
                        .clip(CircleShape)
                        .background(
                            when (operatorMood) {
                                OperatorMood.VICTORY -> NeonEmerald.copy(alpha = 0.2f)
                                OperatorMood.DETONATED -> CyberRed.copy(alpha = 0.2f)
                                OperatorMood.TENSE -> HazardAmber.copy(alpha = 0.2f)
                                OperatorMood.CALM -> CyberCyan.copy(alpha = 0.15f)
                            }
                        )
                        .border(
                            2.dp,
                            when (operatorMood) {
                                OperatorMood.VICTORY -> NeonEmerald
                                OperatorMood.DETONATED -> CyberRed
                                OperatorMood.TENSE -> HazardAmber
                                OperatorMood.CALM -> CyberCyan
                            },
                            CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onResetClick
                        )
                        .padding(10.dp)
                        .testTag("operator_reset_button"),
                    contentAlignment = Alignment.Center
                ) {
                    val moodEmoji = when (operatorMood) {
                        OperatorMood.CALM -> "😎"
                        OperatorMood.TENSE -> "😬"
                        OperatorMood.VICTORY -> "🛡️"
                        OperatorMood.DETONATED -> "💥"
                    }
                    Text(
                        text = moodEmoji,
                        fontSize = 26.sp
                    )
                }

                // Timer Display
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "TIME ELAPSED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF070B12))
                                .border(1.dp, CyberCyan.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = formattedTimer,
                                color = CyberCyan,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Timer",
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Autosave Tactical Indicator
            AnimatedVisibility(visible = isAutosaved && status == GameStatus.RUNNING) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(NeonEmerald)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "AUTOSAVE LIVE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonEmerald.copy(alpha = 0.9f),
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
