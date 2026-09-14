package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.DifficultyLevel
import com.example.ui.components.ControlBar
import com.example.ui.components.CustomGridDialog
import com.example.ui.components.DefeatDialog
import com.example.ui.components.GlobalStatsDialog
import com.example.ui.components.LeaderboardDialog
import com.example.ui.components.MinesweeperBoard
import com.example.ui.components.TacticalHud
import com.example.ui.components.VictoryDialog
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.GameViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels {
        GameViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val allRecords by viewModel.allRecords.collectAsStateWithLifecycle()
            val globalStats by viewModel.globalStats.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = uiState.isDarkMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(innerPadding)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Top Tactical HUD
                            TacticalHud(
                                status = uiState.status,
                                operatorMood = uiState.operatorMood,
                                totalMines = uiState.totalMines,
                                flagsPlaced = uiState.flagsPlaced,
                                elapsedSeconds = uiState.elapsedSeconds,
                                isDarkMode = uiState.isDarkMode,
                                isAutosaved = uiState.isAutosaved,
                                onResetClick = {
                                    viewModel.startNewGame(uiState.difficulty, uiState.customConfig)
                                },
                                onToggleThemeClick = { viewModel.toggleTheme() },
                                onLeaderboardClick = { viewModel.setShowLeaderboard(true) },
                                onStatsClick = { viewModel.setShowStats(true) }
                            )

                            // Control and Sizing Bar
                            ControlBar(
                                controlMode = uiState.controlMode,
                                currentDifficulty = uiState.difficulty,
                                onModeChange = { viewModel.setControlMode(it) },
                                onDifficultySelect = { viewModel.startNewGame(it) },
                                onCustomConfigClick = { viewModel.setShowCustomGrid(true) },
                                onZoomIn = { viewModel.zoomIn() },
                                onZoomOut = { viewModel.zoomOut() },
                                onResetZoom = { viewModel.resetZoom() }
                            )

                            // Interactive Minesweeper Grid
                            MinesweeperBoard(
                                board = uiState.board,
                                rows = uiState.rows,
                                cols = uiState.cols,
                                gameStatus = uiState.status,
                                zoomLevel = uiState.zoomLevel,
                                onCellClick = { r, c -> viewModel.onCellClick(r, c) },
                                onCellLongClick = { r, c -> viewModel.onCellLongClick(r, c) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Dialogs
                        if (uiState.showLeaderboardDialog) {
                            LeaderboardDialog(
                                records = allRecords,
                                onDismiss = { viewModel.setShowLeaderboard(false) }
                            )
                        }

                        if (uiState.showStatsDialog) {
                            GlobalStatsDialog(
                                stats = globalStats,
                                onClearHistory = { viewModel.clearStatsAndHistory() },
                                onDismiss = { viewModel.setShowStats(false) }
                            )
                        }

                        if (uiState.showCustomGridDialog) {
                            CustomGridDialog(
                                initialConfig = uiState.customConfig,
                                onApply = { rows, cols, mines ->
                                    viewModel.updateCustomConfig(rows, cols, mines)
                                    viewModel.setShowCustomGrid(false)
                                },
                                onDismiss = { viewModel.setShowCustomGrid(false) }
                            )
                        }

                        if (uiState.showVictoryDialog) {
                            VictoryDialog(
                                timeSeconds = uiState.lastCompletedTime,
                                difficulty = uiState.difficulty,
                                mines = uiState.totalMines,
                                onPlayAgain = {
                                    viewModel.startNewGame(uiState.difficulty, uiState.customConfig)
                                },
                                onReviewBoard = { viewModel.dismissVictoryDialog() }
                            )
                        }

                        if (uiState.showDefeatDialog) {
                            DefeatDialog(
                                timeSeconds = uiState.lastCompletedTime,
                                difficulty = uiState.difficulty,
                                onPlayAgain = {
                                    viewModel.startNewGame(uiState.difficulty, uiState.customConfig)
                                },
                                onReviewBoard = { viewModel.dismissDefeatDialog() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Bomb Disposal Operator - $name", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}
