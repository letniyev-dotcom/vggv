package com.mimika.app.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mimika.app.data.MockRepo
import com.mimika.app.ui.components.BackRow
import com.mimika.app.ui.components.DiaryRowItem
import com.mimika.app.ui.components.Hairline
import com.mimika.app.ui.components.MimikaScroll
import com.mimika.app.ui.theme.Mimika

/**
 * Дневник — sleep / weight / water / nutrition. Full Letify functionality,
 * just relocated off the home screen so it never competes with the plan for
 * first place on the screen.
 */
@Composable
fun DiaryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MimikaScroll(modifier = modifier) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        BackRow(onClick = onBack)

        Text("Дневник", style = Mimika.typography.headlineMedium, color = Mimika.colors.text)
        Spacer(Modifier.height(10.dp))

        MockRepo.diary.forEachIndexed { i, row ->
            if (i > 0) Hairline()
            DiaryRowItem(
                label = row.label,
                value = row.value,
                unit = row.unit,
                empty = row.empty,
            )
        }

        Spacer(Modifier.height(18.dp))
        Text(
            "Записи те же по сути, что и в Letify — просто убраны с главного " +
                "экрана, чтобы не спорить с планом за внимание.",
            style = Mimika.typography.tiny,
            color = Mimika.colors.mutedDim,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(32.dp))
    }
}
