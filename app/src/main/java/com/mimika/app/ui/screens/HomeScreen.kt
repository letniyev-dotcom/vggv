package com.mimika.app.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mimika.app.data.MockRepo
import com.mimika.app.data.TaskState
import com.mimika.app.ui.components.DayHero
import com.mimika.app.ui.components.DiaryLinkRow
import com.mimika.app.ui.components.HabitRow
import com.mimika.app.ui.components.Hairline
import com.mimika.app.ui.components.MimikaScroll
import com.mimika.app.ui.components.SectionLabel
import com.mimika.app.ui.components.SubTaskRow
import com.mimika.app.ui.components.TimelineRow

/**
 * Главный экран — the ONE screen answering "what to do right now": day
 * counter, habits (compact, weekly), today's timeline. Nothing else.
 * Sleep/weight/water/nutrition live one quiet tap away in the Diary.
 */
@Composable
fun HomeScreen(
    onOpenHabit: (String) -> Unit,
    onOpenDiary: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Local, in-memory task state so tapping a check circle actually toggles
    // it — mirrors Letify's "tap only, no auto-complete on a timer" rule.
    var tasks by remember { mutableStateOf(MockRepo.timeline) }

    MimikaScroll(modifier = modifier) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Spacer(Modifier.height(8.dp))

        DayHero(
            day = MockRepo.dayNumber,
            dayLabel = MockRepo.dayLabel,
            weekDots = MockRepo.weekDots,
        )

        SectionLabel("Привычки", trailing = "все →", onTrailingClick = {})
        MockRepo.habits.forEachIndexed { i, habit ->
            if (i > 0) Hairline()
            HabitRow(
                name = habit.name,
                streakLabel = habit.streakLabel,
                week = habit.week,
                onClick = { onOpenHabit(habit.id) },
            )
        }

        Spacer(Modifier.height(6.dp))
        SectionLabel("Сегодня", modifier = Modifier.padding(top = 14.dp, bottom = 2.dp))

        tasks.forEachIndexed { index, task ->
            TimelineRow(
                task = task,
                onToggle = {
                    tasks = tasks.toMutableList().also {
                        it[index] = task.copy(
                            state = if (task.state == TaskState.DONE) TaskState.UPCOMING else TaskState.DONE,
                        )
                    }
                },
            )
            task.subTasks.forEachIndexed { subIndex, sub ->
                SubTaskRow(
                    sub = sub,
                    onToggle = {
                        tasks = tasks.toMutableList().also { list ->
                            val updatedSubs = task.subTasks.toMutableList()
                            updatedSubs[subIndex] = sub.copy(done = !sub.done)
                            list[index] = task.copy(subTasks = updatedSubs)
                        }
                    },
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Hairline()
        DiaryLinkRow(onClick = onOpenDiary)
        Hairline()

        Spacer(Modifier.height(32.dp))
    }
}
