package com.example.divineaarti

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.divineaarti.ui.components.BottomNavBar
import com.example.divineaarti.ui.screens.AartiDetailScreen
import com.example.divineaarti.ui.screens.FavoritesScreen
import com.example.divineaarti.ui.screens.HomeScreen
import com.example.divineaarti.ui.theme.DivineAartiTheme
import com.example.divineaarti.viewmodel.AartiViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: AartiViewModel = viewModel()
            viewModel.initializeAudioPlayer(this)

            DivineAartiTheme(darkTheme = viewModel.isDarkMode.value) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AartiApp(
                        viewModel = viewModel,
                        onShare = { aarti ->
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "${aarti.title}\n\n${aarti.lyrics.joinToString("\n")}"
                                )
                                type = "text/plain"
                            }
                            startActivity(Intent.createChooser(shareIntent, "Share Aarti"))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AartiApp(
    viewModel: AartiViewModel,
    onShare: (com.example.divineaarti.model.Aarti) -> Unit
) {
    val navController = rememberNavController()
    val currentScreen = viewModel.currentScreen.value

    Scaffold(
        bottomBar = {
            // Show bottom bar only on main screens (home and favorites)
            if (currentScreen in listOf("home", "favorites")) {
                BottomNavBar(
                    currentRoute = currentScreen,
                    onNavigate = { route ->
                        viewModel.setCurrentScreen(route)
                        navController.navigate(route) {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                viewModel.setCurrentScreen("home")
                HomeScreen(
                    viewModel = viewModel,
                    onAartiClick = { aarti ->
                        viewModel.selectAarti(aarti)
                        viewModel.setCurrentScreen("detail")
                        navController.navigate("detail")
                    }
                )
            }

            composable("favorites") {
                viewModel.setCurrentScreen("favorites")
                FavoritesScreen(
                    viewModel = viewModel,
                    onAartiClick = { aarti ->
                        viewModel.selectAarti(aarti)
                        viewModel.setCurrentScreen("detail")
                        navController.navigate("detail")
                    }
                )
            }

            composable("detail") {
                val selectedAarti = viewModel.selectedAarti.value
                if (selectedAarti != null) {
                    AartiDetailScreen(
                        aarti = selectedAarti,
                        isPlaying = viewModel.isPlaying.value,
                        onBack = {
                            viewModel.clearSelection()
                            navController.popBackStack()
                        },
                        onPlayPauseClick = { viewModel.togglePlayPause() },
                        onReplayClick = { viewModel.replayAudio() },
                        onShareClick = { onShare(selectedAarti) }
                    )
                }
            }
        }
    }
}