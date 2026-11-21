package io.legado.app.ui.ai

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.roundToIntRect
import io.legado.app.R
import io.legado.app.domain.model.AiReasoningLevel
import io.legado.app.ui.ai.chat.AiChatModelUi
import io.legado.app.ui.theme.ComposeEngine
import io.legado.app.ui.theme.LegadoTheme
import io.legado.app.ui.widget.components.AppSlider
import io.legado.app.ui.widget.components.button.series.MediumTonalButton
import io.legado.app.ui.widget.components.card.TextCard
import io.legado.app.ui.widget.components.icon.AppIcon
import io.legado.app.ui.widget.components.text.AppText
import io.legado.app.ui.widget.components.modalBottomSheet.AppModalBottomSheet
import kotlinx.collections.immutable.toImmutableList
import ui.feice.mochi.basic.MochiButtonStyle
import ui.feice.mochi.basic.MochiDropdownDefaults
import ui.feice.mochi.basic.MochiDropdownMenu
import ui.feice.mochi.basic.MochiIconButton
import ui.feice.mochi.basic.MochiIntensitySlider
import ui.feice.mochi.basic.MochiItem
import kotlin.math.roundToInt

@Composable
fun AiReasoningModeButton(
    level: AiReasoningLevel,
    enabled: Boolean,
    onLevelChange: (AiReasoningLevel) -> Unit,
    models: List<AiChatModelUi> = emptyList(),
    onSelectModel: (String) -> Unit = {},
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var showModelMenu by rememberSaveable { mutableStateOf(false) }
    val onIconTint = if (level == AiReasoningLevel.OFF) {
        LegadoTheme.colorScheme.onSurfaceVariant
    } else {
        LegadoTheme.colorScheme.onPrimary
    }
    if (LegadoTheme.composeEngine == ComposeEngine.Mochi) {
        MochiIconButton(
            onClick = { showSheet = true },
            enabled = enabled,
            style = if (level == AiReasoningLevel.OFF) MochiButtonStyle.Tonal else MochiButtonStyle.Filled,
        ) {
            AppIcon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = stringResource(R.string.ai_thinking_mode),
                tint = onIconTint,
            )
        }
    } else {
        MediumTonalButton(
            onClick = { showSheet = true },
            icon = Icons.Default.Lightbulb,
            enabled = enabled,
            selected = level != AiReasoningLevel.OFF,
            contentDescription = stringResource(R.string.ai_thinking_mode),
        )
    }
    AppModalBottomSheet(
        show = showSheet,
        onDismissRequest = {
            showModelMenu = false
            showSheet = false
        },
        title = stringResource(R.string.ai_thinking_mode),
        endAction = {
            var menuAnchorBounds by remember { mutableStateOf(IntRect.Zero) }
            val mochiItems = remember(models) {
                models.map { model ->
                    MochiItem(
                        label = model.providerName.takeIf { it.isNotBlank() }
                            ?.let { "${model.displayName} · $it" }
                            ?: model.displayName,
                        id = model.id,
                        checked = model.selected,
                    )
                }.toImmutableList()
            }
            Box(
                modifier = Modifier.onGloballyPositioned {
                    menuAnchorBounds = it.boundsInWindow().roundToIntRect()
                },
            ) {
                MochiIconButton(
                    onClick = { showModelMenu = true },
                    style = MochiButtonStyle.Glass,
                    size = 40.dp,
                ) {
                    AppIcon(
                        imageVector = Icons.Default.List,
                        contentDescription = stringResource(R.string.ai_select_model),
                        tint = LegadoTheme.colorScheme.onSurface,
                    )
                }
                if (LegadoTheme.composeEngine == ComposeEngine.Mochi) {
                    MochiDropdownMenu(
                        anchorBounds = menuAnchorBounds,
                        expanded = showModelMenu,
                        onExpandedChange = { showModelMenu = it },
                        items = if (mochiItems.isEmpty()) {
                            listOf(
                                MochiItem(
                                    label = stringResource(R.string.ai_select_model),
                                    enabled = false,
                                )
                            ).toImmutableList()
                        } else {
                            mochiItems
                        },
                        onItemClick = { index ->
                            showModelMenu = false
                            models.getOrNull(index)?.id?.let(onSelectModel)
                        },
                        selectedIndex = models.indexOfFirst { it.selected },
                        menuWidth = MochiDropdownDefaults.MenuWidth,
                    )
                } else {
                    DropdownMenu(
                        expanded = showModelMenu,
                        onDismissRequest = { showModelMenu = false },
                    ) {
                        if (models.isEmpty()) {
                            DropdownMenuItem(
                                text = {
                                    AppText(
                                        text = stringResource(R.string.ai_select_model),
                                        style = LegadoTheme.typography.bodySmall,
                                        color = LegadoTheme.colorScheme.onSurfaceVariant,
                                    )
                                },
                                enabled = false,
                                onClick = {},
                            )
                        } else {
                            models.forEach { model ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            AppText(
                                                text = model.displayName,
                                                style = LegadoTheme.typography.bodyMedium,
                                                maxLines = 1,
                                            )
                                            if (model.providerName.isNotBlank()) {
                                                AppText(
                                                    text = model.providerName,
                                                    style = LegadoTheme.typography.labelSmall,
                                                    color = LegadoTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1,
                                                )
                                            }
                                        }
                                    },
                                    trailingIcon = if (model.selected) {
                                        { AppIcon(Icons.Default.Check, contentDescription = null) }
                                    } else {
                                        null
                                    },
                                    onClick = {
                                        showModelMenu = false
                                        onSelectModel(model.id)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        },
    ) {
        AiReasoningLevelControl(level, onLevelChange)
    }
}

@Composable
internal fun AiReasoningLevelControl(
    level: AiReasoningLevel,
    onLevelChange: (AiReasoningLevel) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        TextCard(
            text = level.label(),
            textStyle = LegadoTheme.typography.titleLargeEmphasized,
            backgroundColor = LegadoTheme.colorScheme.onSheetContent
        )
        Spacer(Modifier.height(32.dp))
        val thinkingModeDescription = stringResource(R.string.ai_thinking_mode)
        val currentLevelLabel = level.label()
        if (LegadoTheme.composeEngine == ComposeEngine.Mochi) {
            MochiIntensitySlider(
                value = level.ordinal / AiReasoningLevel.entries.lastIndex.toFloat(),
                onValueChange = { value ->
                    val index = (value * AiReasoningLevel.entries.lastIndex).roundToInt()
                    onLevelChange(AiReasoningLevel.entries[index.coerceIn(0, AiReasoningLevel.entries.lastIndex)])
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = thinkingModeDescription
                        stateDescription = currentLevelLabel
                    },
                stops = AiReasoningLevel.entries.size,
                detent = null,
            )
        } else {
            AppSlider(
                value = level.ordinal.toFloat(),
                onValueChange = { value ->
                    // Pixel-to-step conversion can yield 1.9999999 for the LOW tick.
                    AiReasoningLevel.entries[value.roundToInt().coerceIn(
                        0,
                        AiReasoningLevel.entries.lastIndex,
                    )].let(onLevelChange)
                },
                modifier = Modifier.fillMaxWidth(),
                valueRange = 0f..AiReasoningLevel.entries.lastIndex.toFloat(),
                steps = AiReasoningLevel.entries.size - 2,
                accessibilityLabel = stringResource(R.string.ai_thinking_mode),
                accessibilityValue = currentLevelLabel,
            )
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun AiReasoningLevel.label(): String = when (this) {
    AiReasoningLevel.OFF -> stringResource(R.string.ai_thinking_off)
    AiReasoningLevel.AUTO -> stringResource(R.string.ai_thinking_auto)
    AiReasoningLevel.LOW -> stringResource(R.string.ai_reasoning_level_low)
    AiReasoningLevel.MEDIUM -> stringResource(R.string.ai_reasoning_level_medium)
    AiReasoningLevel.HIGH -> stringResource(R.string.ai_reasoning_level_high)
    AiReasoningLevel.XHIGH -> stringResource(R.string.ai_reasoning_level_xhigh)
    AiReasoningLevel.MAX -> stringResource(R.string.ai_reasoning_level_max)
}
