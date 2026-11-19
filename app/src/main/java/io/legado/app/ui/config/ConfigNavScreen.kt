package io.legado.app.ui.config

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import io.legado.app.R
import io.legado.app.ui.theme.adaptiveContentPadding
import io.legado.app.ui.widget.components.AppScaffold
import io.legado.app.ui.widget.components.icon.AppIcons
import io.legado.app.ui.widget.components.SplicedColumnGroup
import io.legado.app.ui.widget.components.settingItem.ClickableSettingItem
import io.legado.app.ui.widget.components.topbar.GlassMediumFlexibleTopAppBar
import io.legado.app.ui.widget.components.topbar.GlassTopAppBarDefaults
import io.legado.app.ui.widget.components.topbar.TopBarNavigationButton
import io.legado.app.ui.widget.components.topbar.TopBarActionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigNavScreen(
    onBackClick: () -> Unit,
    onNavigateToOther: () -> Unit,
    onNavigateToRead: () -> Unit,
    onNavigateToManga: () -> Unit,
    onNavigateToReadAloud: () -> Unit,
    onNavigateToCover: () -> Unit,
    onNavigateToTheme: () -> Unit,
    onNavigateToBackup: () -> Unit,
    onNavigateToAi: () -> Unit,
    onNavigateToDownloadCache: () -> Unit,
    onNavigateToTranslation: () -> Unit,
    onNavigateToLab: () -> Unit,
    onNavigateToPrivate: () -> Unit,
    onNavigateToBubble: () -> Unit,
    onSearchSettings: () -> Unit,
) {
    val scrollBehavior = GlassTopAppBarDefaults.defaultScrollBehavior()

    AppScaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            GlassMediumFlexibleTopAppBar(
                title = stringResource(R.string.setting),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    TopBarNavigationButton(onClick = onBackClick)
                },
                actions = {
                    TopBarActionButton(
                        onClick = dropUnlessResumed(block = onSearchSettings),
                        imageVector = AppIcons.Search,
                        contentDescription = stringResource(R.string.settings_search_title),
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = adaptiveContentPadding(
                top = paddingValues.calculateTopPadding(),
                bottom = 120.dp
            )
        ) {
            item {
                SplicedColumnGroup {
                    ClickableSettingItem(
                        title = stringResource(R.string.theme_setting),
                        imageVector = AppIcons.mochi("color-palette", Icons.Default.ColorLens),
                        onClick = onNavigateToTheme
                    )
                    ClickableSettingItem(
                        title = stringResource(R.string.other_setting),
                        imageVector = AppIcons.mochi("options", Icons.Default.Settings),
                        onClick = onNavigateToOther
                    )
                    ClickableSettingItem(
                        title = stringResource(R.string.read_config),
                        imageVector = AppIcons.mochi(
                            "book",
                            Icons.AutoMirrored.Filled.LibraryBooks,
                        ),
                        onClick = onNavigateToRead
                    )
                    ClickableSettingItem(
                        title = stringResource(R.string.manga_config),
                        imageVector = AppIcons.mochi("images", Icons.Default.Image),
                        onClick = onNavigateToManga
                    )
                    ClickableSettingItem(
                        title = stringResource(R.string.aloud_config),
                        description = stringResource(R.string.multi_role_rule_summary),
                        imageVector = AppIcons.mochi("megaphone", Icons.Default.Settings),
                        onClick = onNavigateToReadAloud
                    )
                    ClickableSettingItem(
                        title = stringResource(R.string.cover_config),
                        imageVector = AppIcons.mochi("image", Icons.Default.Image),
                        onClick = onNavigateToCover
                    )
                    ClickableSettingItem(
                        title = stringResource(R.string.bubble_config),
                        description = stringResource(R.string.bubble_config_entry_summary),
                        imageVector = AppIcons.mochi("chatbubble", Icons.Default.ChatBubble),
                        onClick = onNavigateToBubble
                    )
                    ClickableSettingItem(
                        title = stringResource(R.string.download_cache_config),
                        imageVector = AppIcons.mochi(
                            "cloud-download",
                            Icons.Default.CloudDownload,
                        ),
                        onClick = onNavigateToDownloadCache
                    )
                    ClickableSettingItem(
                        title = stringResource(R.string.backup_restore),
                        imageVector = AppIcons.mochi("cloud-upload", Icons.Default.CloudUpload),
                        onClick = onNavigateToBackup
                    )
                    ClickableSettingItem(
                        title = stringResource(R.string.ai_config),
                        imageVector = AppIcons.mochi("sparkles", Icons.Default.SmartToy),
                        onClick = onNavigateToAi
                    )
                    ClickableSettingItem(
                        title = stringResource(R.string.translation_config),
                        imageVector = AppIcons.mochi("language", Icons.Default.Language),
                        onClick = onNavigateToTranslation
                    )
                    ClickableSettingItem(
                        title = stringResource(R.string.privacy),
                        imageVector = AppIcons.mochi("shield-checkmark", Icons.Default.Security),
                        onClick = onNavigateToPrivate
                    )
                    ClickableSettingItem(
                        title = stringResource(R.string.lab_setting),
                        imageVector = AppIcons.mochi("flask", Icons.Default.Science),
                        onClick = onNavigateToLab
                    )
                }
            }
        }
    }
}
