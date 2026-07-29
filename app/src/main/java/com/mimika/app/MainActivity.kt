package com.mimika.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mimika.app.ui.nav.Route
import com.mimika.app.ui.nav.ShiftNavHost
import com.mimika.app.ui.nav.rememberShiftNavController
import com.mimika.app.ui.screens.DiaryScreen
import com.mimika.app.ui.screens.HabitDetailScreen
import com.mimika.app.ui.screens.HomeScreen
import com.mimika.app.ui.theme.Mimika
import com.mimika.app.ui.theme.MimikaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Edge-to-edge with a fully transparent status bar — the app paints
        // its own bg color underneath, matching the concept's phone frame
        // where content visibly scrolls up under the status-bar icons.
        enableEdgeToEdge()
        setContent {
            MimikaTheme {
                MimikaApp()
            }
        }
    }
}

@Composable
private fun MimikaApp() {
    val nav = rememberShiftNavController(Route.Home)

    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Mimika.colors.bg),
    ) {
        ShiftNavHost(controller = nav) { route, onBack ->
            when (route) {
                Route.Home -> HomeScreen(
                    onOpenHabit = { id -> nav.push(Route.HabitDetail(id)) },
                    onOpenDiary = { nav.push(Route.Diary) },
                )
                is Route.HabitDetail -> HabitDetailScreen(
                    habitId = route.habitId,
                    onBack = onBack,
                    onSwitchHabit = { id -> nav.push(Route.HabitDetail(id)) },
                )
                Route.Diary -> DiaryScreen(onBack = onBack)
            }
        }
    }
}
