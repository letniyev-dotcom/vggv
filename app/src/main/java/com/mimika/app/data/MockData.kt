package com.mimika.app.data

/** Seven-day window ending today; `null` = no data for that day yet. */
data class WeekMark(val done: Boolean, val isToday: Boolean = false)

data class Habit(
    val id: String,
    val name: String,
    val streakLabel: String,
    val week: List<WeekMark>,
    val monthLabel: String,
    val month: List<Boolean?>, // null = future/unmarked cell, true = done, false = missed
)

enum class TaskState { DONE, LIVE, UPCOMING }

data class SubTask(
    val name: String,
    val done: Boolean,
)

data class TimelineTask(
    val time: String,
    val name: String,
    val state: TaskState,
    val subTasks: List<SubTask> = emptyList(),
)

data class DiaryRow(
    val label: String,
    val value: String,
    val unit: String? = null,
    val empty: Boolean = false,
)

object MockRepo {
    val dayNumber = 14
    val dayLabel = "30 июля, четверг"
    val weekDots = listOf(true, true, true, false, false, false, false)

    val habits = listOf(
        Habit(
            id = "water",
            name = "Вода",
            streakLabel = "5 дней подряд",
            week = listOf(
                WeekMark(true), WeekMark(true), WeekMark(false), WeekMark(true),
                WeekMark(true), WeekMark(true), WeekMark(true, isToday = true),
            ),
            monthLabel = "🔥 5 дней подряд · 22 из 30 в июле",
            month = listOf(
                true, true, false, true, true, true, true,
                true, false, true, true, true, false, true,
                true, true, true, false, true, true, true,
                true, true, false, true, true, true, true,
                true, true,
            ),
        ),
        Habit(
            id = "reading",
            name = "Чтение",
            streakLabel = "2 дня подряд",
            week = listOf(
                WeekMark(false), WeekMark(true), WeekMark(false), WeekMark(false),
                WeekMark(true), WeekMark(true), WeekMark(false, isToday = true),
            ),
            monthLabel = "2 дня подряд",
            month = List(30) { i -> if (i % 3 == 0) true else if (i % 5 == 0) false else null },
        ),
    )

    val timeline = listOf(
        TimelineTask("08:00", "Зарядка", TaskState.DONE),
        TimelineTask(
            "09:30 – 11:00", "Глубокая работа", TaskState.LIVE,
            subTasks = listOf(
                SubTask("Написать модуль", done = true),
                SubTask("Ревью PR", done = false),
            ),
        ),
        TimelineTask("13:30", "Тренировка", TaskState.UPCOMING),
        TimelineTask("19:00", "Чтение", TaskState.UPCOMING),
    )

    val diary = listOf(
        DiaryRow("Сон", "7ч 20м"),
        DiaryRow("Вес", "74.2", unit = "кг"),
        DiaryRow("Вода", "1.4", unit = "/ 2.5 л"),
        DiaryRow("Питание", "не отмечено", empty = true),
    )

    fun habit(id: String): Habit = habits.first { it.id == id }
}
