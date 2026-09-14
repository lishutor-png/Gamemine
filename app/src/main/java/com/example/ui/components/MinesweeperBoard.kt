package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Cell
import com.example.domain.model.GameStatus
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberRed
import com.example.ui.theme.HazardAmber
import com.example.ui.theme.MineNum1
import com.example.ui.theme.MineNum2
import com.example.ui.theme.MineNum3
import com.example.ui.theme.MineNum4
import com.example.ui.theme.MineNum5
import com.example.ui.theme.MineNum6
import com.example.ui.theme.MineNum7
import com.example.ui.theme.MineNum8
import com.example.ui.theme.NeonEmerald

@Composable
fun MinesweeperBoard(
    board: List<List<Cell>>,
    rows: Int,
    cols: Int,
    gameStatus: GameStatus,
    zoomLevel: Float,
    onCellClick: (row: Int, col: Int) -> Unit,
    onCellLongClick: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        val availableWidth = maxWidth
        val availableHeight = maxHeight

        // Compute adaptive cell size
        val baseCellSize = if (cols <= 10) {
            ((availableWidth - 32.dp) / cols).coerceIn(36.dp, 48.dp)
        } else if (cols <= 16) {
            36.dp
        } else {
            34.dp
        }

        val cellSize = (baseCellSize.value * zoomLevel).dp

        val horizontalScrollState = rememberScrollState()
        val verticalScrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(horizontalScrollState)
                .verticalScroll(verticalScrollState)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(4.dp)
            ) {
                for (r in 0 until rows) {
                    Row {
                        for (c in 0 until cols) {
                            val cell = if (r < board.size && c < board[r].size) board[r][c] else null
                            if (cell != null) {
                                MineCellView(
                                    cell = cell,
                                    size = cellSize,
                                    gameStatus = gameStatus,
                                    onClick = { onCellClick(r, c) },
                                    onLongClick = { onCellLongClick(r, c) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MineCellView(
    cell: Cell,
    size: Dp,
    gameStatus: GameStatus,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val isRevealed = cell.isRevealed
    val isFlagged = cell.isFlagged
    val isMine = cell.isMine
    val isExploded = cell.isExploded
    val isFalseFlag = cell.isFalseFlag

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    Box(
        modifier = Modifier
            .size(size)
            .padding(1.dp)
            .clip(RoundedCornerShape(4.dp))
            .then(
                if (isExploded) {
                    Modifier
                        .background(CyberRed)
                        .border(1.5.dp, Color.White, RoundedCornerShape(4.dp))
                } else if (!isRevealed) {
                    // Closed Tactical Tile
                    val closedTileGradient = if (isDark) {
                        Brush.verticalGradient(
                            listOf(Color(0xFF2E3D52), Color(0xFF1B2535))
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                        )
                    }
                    Modifier
                        .shadow(1.dp, RoundedCornerShape(4.dp))
                        .background(closedTileGradient)
                        .border(
                            1.dp,
                            if (isDark) Color(0xFF475569) else Color(0xFF94A3B8),
                            RoundedCornerShape(4.dp)
                        )
                } else {
                    // Revealed Safe Panel
                    val openBackground = if (isDark) Color(0xFF0B111D) else Color(0xFFF8FAFC)
                    Modifier
                        .background(openBackground)
                        .border(
                            0.5.dp,
                            if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                            RoundedCornerShape(4.dp)
                        )
                }
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .testTag("cell_${cell.row}_${cell.col}"),
        contentAlignment = Alignment.Center
    ) {
        if (isExploded) {
            // DETONATED BOMB
            Icon(
                imageVector = Icons.Default.Dangerous,
                contentDescription = "Detonated Bomb",
                tint = Color.White,
                modifier = Modifier.size((size.value * 0.65f).dp)
            )
        } else if (isFalseFlag) {
            // MISPLACED FLAG
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = "False Flag",
                    tint = CyberRed.copy(alpha = 0.6f),
                    modifier = Modifier.size((size.value * 0.6f).dp)
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Crossed Out",
                    tint = Color.White,
                    modifier = Modifier.size((size.value * 0.7f).dp)
                )
            }
        } else if (isFlagged) {
            // FLAGGED CELL
            Box(
                modifier = Modifier
                    .size((size.value * 0.75f).dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(CyberRed.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = "Flagged Bomb",
                    tint = CyberRed,
                    modifier = Modifier.size((size.value * 0.55f).dp)
                )
            }
        } else if (isRevealed) {
            if (isMine) {
                // DEFUSED OR REVEALED MINE
                Icon(
                    imageVector = if (gameStatus == GameStatus.WON) Icons.Default.Shield else Icons.Default.Dangerous,
                    contentDescription = "Mine",
                    tint = if (gameStatus == GameStatus.WON) NeonEmerald else CyberRed,
                    modifier = Modifier.size((size.value * 0.6f).dp)
                )
            } else if (cell.adjacentMines > 0) {
                // ADJACENT NUMBER (High-Contrast Cyber Palette)
                val numberColor = when (cell.adjacentMines) {
                    1 -> MineNum1
                    2 -> MineNum2
                    3 -> MineNum3
                    4 -> MineNum4
                    5 -> MineNum5
                    6 -> MineNum6
                    7 -> MineNum7
                    8 -> MineNum8
                    else -> MaterialTheme.colorScheme.onSurface
                }

                val fontSize = (size.value * 0.52f).coerceIn(12f, 22f).sp

                Text(
                    text = cell.adjacentMines.toString(),
                    color = numberColor,
                    fontSize = fontSize,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
