package com.mimika.app.ui.nav

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * «Сдвиг» — Мимика's screen-to-screen transition, matching Letify's push
 * feel 1:1 (same easing curve + duration): CubicBezier(0.32, 0.72, 0.0, 1.0),
 * 360ms, full-width push (no parallax/cover — that's Letify's other, unused
 * "Cover" style).
 *
 * Built on Compose's [AnimatedContent] instead of Letify's hand-rolled
 * shared-Animatable + raw pointerInput gesture: same visual result — both
 * layers driven by ONE transitionSpec so they can't desync — with far less
 * surface area to get wrong. System predictive-back / gesture-back plays
 * the same shift in reverse automatically via [BackHandler].
 */

private val ShiftEasing = CubicBezierEasing(0.32f, 0.72f, 0.0f, 1.0f)
private const val ShiftDurationMs = 360
private val ShiftSpec = tween<Int>(ShiftDurationMs, easing = ShiftEasing)

/** Route stack for the app's single, shallow navigation graph. */
sealed interface Route {
    data object Home : Route
    data class HabitDetail(val habitId: String) : Route
    data object Diary : Route
}

class ShiftNavController internal constructor(initial: Route) {
    var stack by mutableStateOf(listOf(initial))
        private set

    val canGoBack: Boolean
        get() = stack.size > 1

    fun push(route: Route) {
        stack = stack + route
    }

    fun pop() {
        if (canGoBack) stack = stack.dropLast(1)
    }
}

@Composable
fun rememberShiftNavController(initial: Route = Route.Home): ShiftNavController =
    remember { ShiftNavController(initial) }

/**
 * Hosts the current top-of-stack screen and animates pushes/pops with the
 * shift transition. [content] receives the route to render plus an `onBack`
 * callback that pops the stack — screens call this from their back arrow.
 */
@Composable
fun ShiftNavHost(
    controller: ShiftNavController,
    modifier: Modifier = Modifier,
    content: @Composable (route: Route, onBack: () -> Unit) -> Unit,
) {
    BackHandler(enabled = controller.canGoBack) { controller.pop() }

    // Depth drives direction: a route pushed deeper slides in from the
    // right (outgoing exits fully left); popping reverses both.
    val depth = controller.stack.size
    var previousDepth by remember { mutableStateOf(depth) }
    val isPush = depth >= previousDepth
    previousDepth = depth

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = controller.stack.last(),
            transitionSpec = {
                if (isPush) {
                    (slideInHorizontally(ShiftSpec) { fullWidth -> fullWidth } togetherWith
                        slideOutHorizontally(ShiftSpec) { fullWidth -> -fullWidth })
                } else {
                    (slideInHorizontally(ShiftSpec) { fullWidth -> -fullWidth } togetherWith
                        slideOutHorizontally(ShiftSpec) { fullWidth -> fullWidth })
                }.using(SizeTransform(clip = false))
            },
            label = "shift",
        ) { route ->
            content(route) { controller.pop() }
        }
    }
}
