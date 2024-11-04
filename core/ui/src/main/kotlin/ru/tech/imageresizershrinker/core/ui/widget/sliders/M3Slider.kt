/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.core.ui.widget.sliders

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.mixedContainer
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.utils.provider.SafeLocalContainerColor
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
fun M3Slider(
    value: Float,
    enabled: Boolean,
    colors: SliderColors,
    interactionSource: MutableInteractionSource,
    modifier: Modifier,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)?,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int
) {
    val settingsState = LocalSettingsState.current
    Slider(
        interactionSource = interactionSource,
        enabled = enabled,
        modifier = modifier
            .padding(vertical = 2.dp)
            .container(
                shape = RoundedCornerShape(12.dp),
                autoShadowElevation = animateDpAsState(
                    if (settingsState.drawSliderShadows) {
                        1.dp
                    } else 0.dp
                ).value,
                resultPadding = 0.dp,
                borderColor = MaterialTheme.colorScheme
                    .outlineVariant(
                        luminance = 0.1f,
                        onTopOf = SwitchDefaults.colors().disabledCheckedTrackColor
                    )
                    .copy(0.3f),
                color = SafeLocalContainerColor
                    .copy(0.3f)
                    .compositeOver(
                        takeColorFromScheme {
                            if (it) mixedContainer.copy(0.05f)
                            else mixedContainer.copy(0.2f)
                        }
                    )
                    .copy(colors.activeTrackColor.alpha),
                composeColorOnTopOfBackground = false
            )
            .padding(horizontal = 6.dp, vertical = 4.dp),
        value = animateFloatAsState(
            targetValue = value,
            animationSpec = tween(200)
        ).value,
        colors = colors,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        valueRange = valueRange,
        steps = steps,
    )
}