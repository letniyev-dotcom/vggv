package com.mimika.app.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mimika.app.data.MockRepo
import com.mimika.app.ui.components.BackRow
import com.mimika.app.ui.components.HabitMiniRow
import com.mimika.app.ui.components.MimikaScroll
import com.mimika.app.ui.components.MonthGrid
import com.mimika.app.ui.components.SectionLabel
import com.mimika.app.ui.theme.Mimika

/**
 * Привычка · детально — a month of history instead of an empty ring. Tap a
 * habit on the home screen to land here; tap a different habit in the "other
 * habits" mini-list to switch context without leaving the screen.
 */
@Composable
fun HabitDetailScreen(
    habitId: String,
    onBack: () -> Unit,
    onSwitchHabit: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val habit = MockRepo.habit(habitId)
    val todayIndex = habit.month.size - 1

    MimikaScroll(modifier = modifier) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        BackRow(onClick = onBack)

        Text(habit.name, style = Mimika.typography.headlineMedium, color = Mimika.colors.text)
        Spacer(Modifier.height(4.dp))
        Text(habit.monthLabel, style = Mimika.typography.tiny, color = Mimika.colors.accent)

        Spacer(Modifier.height(16.dp))
        MonthGrid(cells = habit.month, todayIndex = todayIndex)

        SectionLabel("Другие привычки", modifier = Modifier.padding(top = 22.dp, bottom = 2.dp))
        MockRepo.habits.forEach { other ->
            HabitMiniRow(
                name = other.name,
                week = other.week,
                active = other.id == habit.id,
                onClick = { if (other.id != habit.id) onSwitchHabit(other.id) },
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}
