package io.legado.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextMotion
import ui.feice.mochi.basic.MochiToastHost
import ui.feice.mochi.basic.rememberMochiToastState
import ui.feice.mochi.effect.overscroll.MochiOverscrollSpec
import ui.feice.mochi.effect.overscroll.ProvideMochiOverscroll
import ui.feice.mochi.theme.MochiColorScheme
import ui.feice.mochi.theme.MochiTextStyles
import ui.feice.mochi.theme.MochiTheme
import ui.feice.mochi.theme.defaultTextStyles
import ui.feice.mochi.ios.iosMaterials

internal val LocalMochiScrollBlurProgress = compositionLocalOf<MutableState<() -> Float>?> { null }

/** Uses the app's resolved palette and fonts; the app host retains ownership of system bars. */
@Composable
internal fun ProvideMochiTheme(
    colorScheme: ColorScheme,
    isDark: Boolean,
    content: @Composable () -> Unit,
) {
    val typography = MaterialTheme.typography
    val settings = LocalAppUiConfiguration.current
    val registry = LocalMochiBackdropRegistry.current ?: remember { MochiBackdropRegistry() }
    val colors = remember(colorScheme, isDark) { colorScheme.toMochiColorScheme(isDark) }
    val textStyles = remember(typography) { typography.toMochiTextStyles() }
    // 默认 0.65 的阻力在分类栏/分页器上拉出的越界太远，松手后的回弹也很抢眼；
    // 调到 0.36 后手指只能带出很小一段位移，滚动到头的感觉更接近 Mochi 官方。
    val overscrollSpec = remember { MochiOverscrollSpec(rubberBand = 0.36f) }
    val toastState = rememberMochiToastState()
    DisposableEffect(toastState) {
        MochiToastBridge.attach(toastState)
        onDispose { MochiToastBridge.detach(toastState) }
    }
    MochiTheme(
        colors = colors,
        textStyles = textStyles,
        systemBarAppearance = false,
        materials = iosMaterials(
            isDark = isDark,
            // Mochi 的材质决定栏、菜单、面板、弹窗的玻璃：以前只跟「悬浮底栏液态玻璃」联动，
            // 于是用户开了顶栏液态玻璃，sheet 仍然是实色。这里把「Mochi 顶栏液态玻璃」也算进来。
            glass = Build.VERSION.SDK_INT >= 33 && (
                settings.appShell.useFloatingBottomBarLiquidGlass ||
                    settings.theme.mochiTopBarLiquidGlass
                ),
            blur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && settings.theme.mochiScrollEdgeBlurEnabled,
            themeAccent = true,
        ),
        content = {
            CompositionLocalProvider(LocalMochiBackdropRegistry provides registry) {
                ProvideMochiOverscroll(spec = overscrollSpec) {
                    content()
                    MochiToastHost(toastState)
                }
            }
        },
    )
}

internal fun ColorScheme.toMochiColorScheme(isDark: Boolean): MochiColorScheme = MochiColorScheme(
    isDark = isDark,
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    inversePrimary = inversePrimary,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = tertiaryContainer,
    onTertiaryContainer = onTertiaryContainer,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    surfaceTint = surfaceTint,
    inverseSurface = inverseSurface,
    inverseOnSurface = inverseOnSurface,
    error = error,
    onError = onError,
    errorContainer = errorContainer,
    onErrorContainer = onErrorContainer,
    outline = outline,
    outlineVariant = outlineVariant,
    scrim = scrim,
    shadow = Color.Black,
    surfaceBright = surfaceBright,
    surfaceDim = surfaceDim,
    surfaceContainerLowest = surfaceContainerLowest,
    surfaceContainerLow = surfaceContainerLow,
    surfaceContainer = surfaceContainer,
    surfaceContainerHigh = surfaceContainerHigh,
    surfaceContainerHighest = surfaceContainerHighest,
)

private fun Typography.toMochiTextStyles(): MochiTextStyles {
    val defaults = defaultTextStyles()
    fun TextStyle.animated() = copy(textMotion = TextMotion.Animated)
    return defaults.copy(
        displayLarge = displayLarge.animated(),
        displayMedium = displayMedium.animated(),
        displaySmall = displaySmall.animated(),
        headlineLarge = headlineLarge.animated(),
        headlineMedium = headlineMedium.animated(),
        headlineSmall = headlineSmall.animated(),
        titleLarge = titleLarge.animated(),
        titleMedium = titleMedium.animated(),
        titleSmall = titleSmall.animated(),
        bodyLarge = bodyLarge.animated(),
        bodyMedium = bodyMedium.animated(),
        bodySmall = bodySmall.animated(),
        labelLarge = labelLarge.animated(),
        labelMedium = labelMedium.animated(),
        labelSmall = labelSmall.animated(),
        largeTitle = defaults.largeTitle.copy(fontFamily = bodyLarge.fontFamily),
        navTitle = defaults.navTitle.copy(fontFamily = bodyLarge.fontFamily),
        body = defaults.body.copy(fontFamily = bodyLarge.fontFamily),
        subhead = defaults.subhead.copy(fontFamily = bodyLarge.fontFamily),
        footnote = defaults.footnote.copy(fontFamily = bodyLarge.fontFamily),
    )
}
