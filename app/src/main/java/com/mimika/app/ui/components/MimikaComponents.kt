package com.mimika.app.ui.components

import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mimika.app.data.SubTask
import com.mimika.app.data.TaskState
import com.mimika.app.data.TimelineTask
import com.mimika.app.data.WeekMark
import com.mimika.app.ui.theme.Mimika
import androidx.compose.material3.Text

/**
 * Shared full-screen scroll scaffold: transparent-status-bar clearance +
 * Мимика's iOS-style elastic overscroll (never the system stretch glow) +
 * the app's standard 20dp horizontal gutter. Every screen uses this so the
 * overscroll wiring only has to be gotten right once.
 */
@Composable
fun MimikaScroll(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val elastic = rememberElasticOverscroll()
    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .nestedScroll(elastic.connection),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .graphicsLayer { translationY = elastic.verticalOverscroll.floatValue }
                    .padding(horizontal = 20.dp),
                content = content,
            )
        }
    }
}

/** Thin 1px hairline, the ONLY separator device used anywhere in Мимика —
 * no cards, no containers, matching the concept's stated design rule. */
@Composable
fun Hairline(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Mimika.colors.hairline),
    )
}

@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    trailing: String? = null,
    onTrailingClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text.uppercase(), style = Mimika.typography.sectionLabel, color = Mimika.colors.mutedDim)
        if (trailing != null) {
            Text(
                trailing,
                style = Mimika.typography.tiny,
                color = Mimika.colors.accent,
                modifier = if (onTrailingClick != null) {
                    Modifier.clickable(
                        interactionSource = noRippleSource(),
                        indication = null,
                        onClick = onTrailingClick,
                    )
                } else Modifier,
            )
        }
    }
}

@Composable
private fun noRippleSource(): MutableInteractionSource =
    remember { MutableInteractionSource() }

/** Back arrow + "Назад" label, exactly matching the concept's `.back` row. */
@Composable
fun BackRow(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clickable(
                interactionSource = noRippleSource(),
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("← ", style = Mimika.typography.bodyMedium, color = Mimika.colors.muted)
        Text("Назад", style = Mimika.typography.bodyMedium, color = Mimika.colors.accent)
    }
}

/** A single day-square used in the compact weekly habit row. */
@Composable
private fun WeekSquare(mark: WeekMark) {
    val bg = if (mark.done) Mimika.colors.accent else Mimika.colors.squareOff
    val borderColor = when {
        mark.isToday -> Mimika.colors.text
        mark.done -> Mimika.colors.accent
        else -> Mimika.colors.squareBorder
    }
    Box(
        modifier = Modifier
            .size(17.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(bg)
            .border(if (mark.isToday) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(5.dp)),
    )
}

/** Compact, tappable habit row for the home screen — name, streak, 7 squares. */
@Composable
fun HabitRow(
    name: String,
    streakLabel: String,
    week: List<WeekMark>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = noRippleSource(),
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 11.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(name, style = Mimika.typography.body, color = Mimika.colors.text)
            Text(streakLabel, style = Mimika.typography.small, color = Mimika.colors.accent)
        }
        Spacer(Modifier.height(7.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            week.forEach { WeekSquare(it) }
        }
    }
}

/** One row of the "Сегодня" timeline: rail dot/line, time, name, check circle. */
@Composable
fun TimelineRow(
    task: TimelineTask,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val timeColor = when (task.state) {
        TaskState.LIVE -> Mimika.colors.accent
        TaskState.DONE -> Mimika.colors.mutedDim
        TaskState.UPCOMING -> Mimika.colors.muted
    }
    val nameColor = if (task.state == TaskState.DONE) Mimika.colors.muted else Mimika.colors.text

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = noRippleSource(),
                indication = null,
                onClick = onToggle,
            )
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Rail: a small dot; live tasks get an accent glow.
        Box(
            modifier = Modifier.width(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            val dotSize = if (task.state == TaskState.LIVE) 9.dp else 7.dp
            val dotColor = if (task.state == TaskState.LIVE) Mimika.colors.accent else Mimika.colors.mutedDim
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(dotColor),
            )
        }
        Text(
            task.time,
            style = Mimika.typography.tiny,
            color = timeColor,
            modifier = Modifier.width(38.dp),
        )
        Text(
            task.name,
            style = Mimika.typography.body,
            color = nameColor,
            modifier = Modifier.weight(1f),
        )
        CheckCircle(done = task.state == TaskState.DONE, live = task.state == TaskState.LIVE)
    }
}

@Composable
fun SubTaskRow(sub: SubTask, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 48.dp)
            .clickable(
                interactionSource = noRippleSource(),
                indication = null,
                onClick = onToggle,
            )
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        CheckCircle(done = sub.done, live = false, small = true)
        Text(
            sub.name,
            style = Mimika.typography.caption,
            color = if (sub.done) Mimika.colors.mutedDim else Mimika.colors.muted,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CheckCircle(done: Boolean, live: Boolean, small: Boolean = false) {
    val size = if (small) 16.dp else 21.dp
    if (done) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Mimika.colors.mutedDim),
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(
                    if (live) 1.6.dp else 1.4.dp,
                    if (live) Mimika.colors.accent else Mimika.colors.mutedDim,
                    CircleShape,
                ),
        )
    }
}

/**
 * The current/live task's rounded card — the ONLY card container on the
 * Plan page, matching Letify's `WCard` treatment for the one thing that
 * deserves visual weight right now. Everything else on the page stays a
 * plain hairline-separated row.
 */
@Composable
fun NowTaskCard(
    timeRange: String,
    name: String,
    subTasks: List<SubTask>,
    onToggleSub: (Int) -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Mimika.colors.cardBg)
            .border(1.dp, Mimika.colors.cardBorder, RoundedCornerShape(24.dp)),
    ) {
        // Accent edge, matching the concept's left accent bar.
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(Mimika.colors.accent),
        )
        Column(modifier = Modifier.padding(start = 21.dp, top = 18.dp, end = 18.dp, bottom = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Mimika.colors.accentDim)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(timeRange, style = Mimika.typography.small, color = Mimika.colors.accent)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Mimika.colors.accent),
                    )
                    Text("идёт сейчас", style = Mimika.typography.small, color = Mimika.colors.accent)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(name, style = Mimika.typography.cardTitle, color = Mimika.colors.text)
            if (subTasks.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    subTasks.forEachIndexed { i, sub ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = noRippleSource(),
                                    indication = null,
                                    onClick = { onToggleSub(i) },
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            CheckCircle(done = sub.done, live = false, small = true)
                            Text(
                                sub.name,
                                style = Mimika.typography.bodyMedium,
                                color = if (sub.done) Mimika.colors.muted else Mimika.colors.text,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Mimika.colors.accent)
                    .clickable(
                        interactionSource = noRippleSource(),
                        indication = null,
                        onClick = onComplete,
                    )
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Отметить выполненной", style = Mimika.typography.bodyMedium, color = Mimika.colors.bg)
            }
        }
    }
}

/** A plain, non-live timeline row for the Plan page's "Дальше" list. */
@Composable
fun PlainTaskRow(time: String, name: String, done: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = noRippleSource(),
                indication = null,
                onClick = onToggle,
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(Mimika.colors.mutedDim),
        )
        Text(
            time,
            style = Mimika.typography.tiny,
            color = if (done) Mimika.colors.mutedDim else Mimika.colors.muted,
            modifier = Modifier.width(42.dp),
        )
        Text(
            name,
            style = Mimika.typography.body,
            color = if (done) Mimika.colors.muted else Mimika.colors.text,
            modifier = Modifier.weight(1f),
        )
        CheckCircle(done = done, live = false)
    }
}

/**
 * Habits page card — softer, more generous than the old compact home-screen
 * row: rounded container, bigger week squares, a pill-shaped streak badge.
 * Used only on the dedicated Habits page (swipe from Plan), never on Plan.
 */
@Composable
fun HabitCard(
    name: String,
    streakLabel: String,
    week: List<WeekMark>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Mimika.colors.cardBgSoft)
            .border(1.dp, Mimika.colors.cardBorder, RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = noRippleSource(),
                indication = null,
                onClick = onClick,
            )
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(name, style = Mimika.typography.body, color = Mimika.colors.text)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Mimika.colors.accentDim)
                    .padding(horizontal = 9.dp, vertical = 4.dp),
            ) {
                Text("🔥 $streakLabel", style = Mimika.typography.small, color = Mimika.colors.accent)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            week.forEach { mark ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(7.dp))
                        .background(if (mark.done) Mimika.colors.accent else Mimika.colors.squareOff)
                        .border(
                            if (mark.isToday) 1.6.dp else 1.dp,
                            when {
                                mark.isToday -> Mimika.colors.text
                                mark.done -> Mimika.colors.accent
                                else -> Mimika.colors.squareBorder
                            },
                            RoundedCornerShape(7.dp),
                        ),
                )
            }
        }
    }
}

/** Two-tab pager header ("План" / "Привычки") synced with a PagerState. */
@Composable
fun PlanHabitsTabs(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val titles = listOf("План", "Привычки")
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        titles.forEachIndexed { i, title ->
            val selected = i == selectedIndex
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (selected) Color(0x14FFFFFF) else Color.Transparent)
                    .clickable(
                        interactionSource = noRippleSource(),
                        indication = null,
                        onClick = { onSelect(i) },
                    )
                    .padding(horizontal = 13.dp, vertical = 7.dp),
            ) {
                Text(
                    title,
                    style = Mimika.typography.tab,
                    color = if (selected) Mimika.colors.text else Mimika.colors.mutedDim,
                )
            }
        }
    }
}

/** Quiet, unremarkable link row to the Diary screen — no icon, no accent. */
@Composable
fun DiaryLinkRow(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = noRippleSource(),
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Дневник · сон, вес, вода", style = Mimika.typography.caption, color = Mimika.colors.muted)
        Text("→", style = Mimika.typography.caption, color = Mimika.colors.mutedDim)
    }
}

@Composable
fun MonthGrid(cells: List<Boolean?>, todayIndex: Int, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = modifier.height(((cells.size / 7 + 1) * 24).dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        userScrollEnabled = false,
    ) {
        items(cells.size) { i ->
            val on = cells[i] == true
            val isToday = i == todayIndex
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (on) Mimika.colors.accent else Mimika.colors.squareOff)
                    .border(
                    if (isToday) 1.5.dp else 1.dp,
                    when {
                        isToday -> Mimika.colors.text
                        on -> Mimika.colors.accent
                        else -> Mimika.colors.squareBorder
                    },
                    RoundedCornerShape(6.dp),
                ),
            )
        }
    }
}

@Composable
fun HabitMiniRow(
    name: String,
    week: List<WeekMark>,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = noRippleSource(),
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            name,
            style = Mimika.typography.bodyMedium,
            color = if (active) Mimika.colors.text else Mimika.colors.muted,
            modifier = Modifier.weight(1f),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            week.forEach { mark ->
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(if (mark.done) Mimika.colors.accent else Color(0x1AFFFFFF)),
                )
            }
        }
    }
}

@Composable
fun DiaryRowItem(label: String, value: String, unit: String?, empty: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = Mimika.typography.body, color = Mimika.colors.text)
        if (empty) {
            Text(value, style = Mimika.typography.caption, color = Mimika.colors.mutedDim)
        } else {
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(value, style = Mimika.typography.body, color = Mimika.colors.accent)
                if (unit != null) {
                    Text(unit, style = Mimika.typography.tiny, color = Mimika.colors.mutedDim)
                }
            }
        }
    }
}

@Composable
fun DayHero(day: Int, dayLabel: String, weekDots: List<Boolean>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(day.toString(), style = Mimika.typography.headlineLarge, color = Mimika.colors.text)
        Spacer(Modifier.height(4.dp))
        Text("ДЕНЬ", style = Mimika.typography.label, color = Mimika.colors.muted, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(dayLabel, style = Mimika.typography.caption, color = Mimika.colors.mutedDim)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            weekDots.forEach { filled ->
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(if (filled) Mimika.colors.accent else Mimika.colors.mutedDim),
                )
            }
        }
    }
}
