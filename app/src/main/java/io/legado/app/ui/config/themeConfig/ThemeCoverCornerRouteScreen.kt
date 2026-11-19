package io.legado.app.ui.config.themeConfig

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

/**
 * 「外观设置 → 封面圆角」独立路由。
 *
 * 以前这页是外观设置内部用 `rememberSaveable` 下钻的伪二级页：系统返回会直接弹掉整个
 * 外观设置路由，也没有 Navigation 3 的前进/返回转场。现在改成真实目的地后，返回上级
 * 是外观设置，预测性返回也能跟着导航栈走。
 */
@Composable
fun ThemeCoverCornerRouteScreen(
    onBackClick: () -> Unit,
    viewModel: ThemeConfigViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CoverCornerSettingsPage(
        theme = state.theme,
        onUpdateTheme = { transform ->
            viewModel.onIntent(ThemeConfigIntent.UpdateTheme(transform))
        },
        onBackClick = onBackClick,
    )
}
