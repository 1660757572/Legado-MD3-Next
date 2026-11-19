package io.legado.app.ui.config.customTheme

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.legado.app.lib.theme.ThemeStore
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

import io.legado.app.utils.toastOnUi
@Composable
fun CustomThemeRouteScreen(
    onBackClick: () -> Unit,
    viewModel: CustomThemeViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val state = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel, context) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is CustomThemeEffect.ApplyLegacyPrimarySeed -> {
                    ThemeStore.editTheme(context).primaryColor(effect.color).apply()
                }
                is CustomThemeEffect.SettingsUpdateFailed -> {
                    context.toastOnUi(effect.message)
                }
            }
        }
    }

    CustomThemeScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onBackClick = onBackClick,
    )
}
