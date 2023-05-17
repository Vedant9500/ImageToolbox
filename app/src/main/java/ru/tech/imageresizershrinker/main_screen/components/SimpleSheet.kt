package ru.tech.imageresizershrinker.main_screen.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.t8rin.modalsheet.ModalSheet
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.modifier.fabBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSheet(
    sheetContent: @Composable ColumnScope.() -> Unit,
    visible: MutableState<Boolean>
) {
    var showSheet by visible

    ModalSheet(
        animationSpec = tween(
            durationMillis = 600,
            easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
        ),
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        sheetModifier = Modifier
            .statusBarsPadding()
            .offset(y = (LocalBorderWidth.current + 1.dp))
            .border(
                width = LocalBorderWidth.current,
                color = MaterialTheme.colorScheme.outlineVariant(
                    luminance = 0.3f,
                    onTopOf = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
                ),
                shape = BottomSheetDefaults.ExpandedShape
            )
            .fabBorder(
                shape = BottomSheetDefaults.ExpandedShape,
                elevation = 16.dp
            )
            .fabBorder(
                height = 0.dp,
                shape = BottomSheetDefaults.ExpandedShape,
                elevation = 16.dp
            ),
        elevation = 0.dp,
        visible = showSheet,
        onVisibleChange = { showSheet = it },
        content = {
            BackHandler { showSheet = false }
            sheetContent()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSheet(
    sheetContent: @Composable ColumnScope.() -> Unit,
    confirmButton: @Composable RowScope.() -> Unit,
    title: @Composable () -> Unit,
    visible: MutableState<Boolean>
) {
    var showSheet by visible

    ModalSheet(
        animationSpec = tween(
            durationMillis = 600,
            easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
        ),
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        sheetModifier = Modifier
            .statusBarsPadding()
            .offset(y = (LocalBorderWidth.current + 1.dp))
            .border(
                width = LocalBorderWidth.current,
                color = MaterialTheme.colorScheme.outlineVariant(
                    luminance = 0.3f,
                    onTopOf = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
                ),
                shape = BottomSheetDefaults.ExpandedShape
            )
            .fabBorder(
                shape = BottomSheetDefaults.ExpandedShape,
                elevation = 16.dp
            )
            .fabBorder(
                height = 0.dp,
                shape = BottomSheetDefaults.ExpandedShape,
                elevation = 16.dp
            ),
        elevation = 0.dp,
        visible = showSheet,
        onVisibleChange = { showSheet = it },
        content = {
            BackHandler { showSheet = false }
            Column(
                modifier = Modifier.weight(1f, false),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = sheetContent
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                title()
                Spacer(modifier = Modifier.weight(1f))
                confirmButton()
            }
        }
    )
}