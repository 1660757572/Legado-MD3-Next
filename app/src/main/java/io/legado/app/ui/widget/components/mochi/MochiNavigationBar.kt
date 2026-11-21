package io.legado.app.ui.widget.components.mochi

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import ui.feice.mochi.basic.MochiTabBar
import ui.feice.mochi.basic.MochiTabBarDefaults
import ui.feice.mochi.basic.MochiTabItem
import ui.feice.mochi.theme.MochiTheme

@Composable
internal fun AppMochiNavigationBar(
    items: ImmutableList<MochiTabItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    alwaysShowLabel: Boolean = true,
    floating: Boolean = false,
    glass: Boolean = true,
    /** MochiTabBar 自带的 bottomAccessory：固定贴在标签栏上方，和标签栏共用同一块玻璃。 */
    accessory: (@Composable (inline: Boolean) -> Unit)? = null,
) {
    MochiTabBar(
        items = items,
        selectedIndex = selectedIndex,
        onSelect = onSelect,
        onReselect = onSelect,
        modifier = modifier
            .windowInsetsPadding(
                if (floating) WindowInsets(0) else MochiTabBarDefaults.windowInsets
            )
            .onKeyEvent { event ->
                val target = when (event.key) {
                    Key.DirectionLeft -> (selectedIndex - 1).coerceAtLeast(0)
                    Key.DirectionRight -> (selectedIndex + 1).coerceAtMost(items.lastIndex)
                    Key.Enter, Key.NumPadEnter, Key.Spacebar, Key.DirectionCenter -> selectedIndex
                    else -> return@onKeyEvent false
                }
                if (event.type == KeyEventType.KeyUp && target in items.indices) onSelect(target)
                true
            }
            .focusable(),
        behavior = MochiTabBarDefaults.behavior(
            itemWidth = if (floating) 76.dp else 92.dp,
            showLabels = showLabel,
            selectedLabelOnly = showLabel && !alwaysShowLabel,
        ),
        windowInsets = WindowInsets(0),
        material = if (glass) MochiTheme.materials.bar else null,
        accessory = accessory,
    )
}
