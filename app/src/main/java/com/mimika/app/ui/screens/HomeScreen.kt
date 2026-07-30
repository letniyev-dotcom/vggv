package com.mimika.app.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mimika.app.data.MockRepo
import com.mimika.app.data.TaskState
import com.mimika.app.ui.components.DayHero
import com.mimika.app.ui.components.DiaryLinkRow
import com.mimika.app.ui.components.HabitCard
import com.mimika.app.ui.components.Hairline
import com.mimika.app.ui.components.MimikaScroll
import com.mimika.app.ui.components.NowTaskCard
import com.mimika.app.ui.components.PlainTaskRow
import com.mimika.app.ui.components.PlanHabitsTabs
import com.mimika.app.ui.components.SectionLabel
import com.mimika.app.ui.theme.Mimika
import kotlinx.coroutines.launch

/**
 * Главный экран — a two-page swipe pager, matching the v2 concept:
 *
 * 1. "План" — day counter, the ONE current/live task in a rounded card,
 *    the rest of today's tasks as plain rows, a quiet link to the Diary.
 *    Habits do NOT live here anymore.
 * 2. "Привычки" — a dedicated page, one swipe away, with its own nicer
 *    card treatment per habit.
 *
 * A small tab row up top mirrors the pager (tap OR swipe both work), so the
 * "swipe to habits" affordance is always discoverable, not just a hidden
 * gesture.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onOpenHabit: (String) -> Unit,
    onOpenDiary: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Spacer(Modifier.height(8.dp))
        PlanHabitsTabs(
            selectedIndex = pagerState.currentPage,
            onSelect = { i -> scope.launch { pagerState.animateScrollToPage(i) } },
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(4.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            when (page) {
                0 -> PlanPage(onOpenDiary = onOpenDiary)
                else -> HabitsPage(onOpenHabit = onOpenHabit)
            }
        }
    }
}

@Composable
private fun PlanPage(onOpenDiary: () -> Unit) {
    var tasks by remember { mutableStateOf(MockRepo.timeline) }
    val liveIndex = tasks.indexOfFirst { it.state == TaskState.LIVE }

    MimikaScroll {
        DayHero(
            day = MockRepo.dayNumber,
            dayLabel = MockRepo.dayLabel,
            weekDots = MockRepo.weekDots,
        )

        if (liveIndex >= 0) {
            val live = tasks[liveIndex]
            SectionLabel("Сейчас")
            Spacer(Modifier.height(2.dp))
            NowTaskCard(
                timeRange = live.time,
                name = live.name,
                subTasks = live.subTasks,
                onToggleSub = { subIndex ->
                    tasks = tasks.toMutableList().also { list ->
                        val updatedSubs = live.subTasks.toMutableList()
                        updatedSubs[subIndex] = live.subTasks[subIndex].copy(done = !live.subTasks[subIndex].done)
                        list[liveIndex] = live.copy(subTasks = updatedSubs)
                    }
                },
                onComplete = {
                    tasks = tasks.toMutableList().also { it[liveIndex] = live.copy(state = TaskState.DONE) }
                },
            )
            Spacer(Modifier.height(6.dp))
        }

        SectionLabel("Дальше", modifier = Modifier.padding(top = 20.dp, bottom = 2.dp))
        tasks.forEachIndexed { index, task ->
            if (index == liveIndex) return@forEachIndexed
            PlainTaskRow(
                time = task.time,
                name = task.name,
                done = task.state == TaskState.DONE,
                onToggle = {
                    tasks = tasks.toMutableList().also {
                        it[index] = task.copy(
                            state = if (task.state == TaskState.DONE) TaskState.UPCOMING else TaskState.DONE,
                        )
                    }
                },
            )
        }

        Spacer(Modifier.height(12.dp))
        Hairline()
        DiaryLinkRow(onClick = onOpenDiary)
        Hairline()

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun HabitsPage(onOpenHabit: (String) -> Unit) {
    MimikaScroll {
        Text(
            "Привычки",
            style = Mimika.typography.headlineMedium,
            color = Mimika.colors.text,
            modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
        )
        Spacer(Modifier.height(18.dp))

        MockRepo.habits.forEachIndexed { i, habit ->
            HabitCard(
                name = habit.name,
                streakLabel = habit.streakLabel,
                week = habit.week,
                onClick = { onOpenHabit(habit.id) },
            )
            if (i != MockRepo.habits.lastIndex) Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(32.dp))
    }
}
