package io.legado.app.ui.main.my

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.legado.app.R
import io.legado.app.data.repository.ReusableTagRepository
import io.legado.app.ui.theme.ComposeEngine
import io.legado.app.ui.theme.LegadoTheme
import io.legado.app.ui.widget.components.AppScaffold
import io.legado.app.ui.widget.components.AppTextField
import io.legado.app.ui.widget.components.EmptyMessage
import io.legado.app.ui.widget.components.button.series.MediumOutlinedButton
import io.legado.app.ui.widget.components.card.NormalCard
import io.legado.app.ui.widget.components.icon.AppIcon
import io.legado.app.ui.widget.components.text.AppText
import io.legado.app.ui.widget.components.topbar.GlassMediumFlexibleTopAppBar
import io.legado.app.ui.widget.components.topbar.GlassTopAppBarDefaults
import io.legado.app.ui.widget.components.topbar.TopBarNavigationButton
import ui.feice.mochi.basic.MochiButton
import ui.feice.mochi.basic.MochiButtonStyle
import ui.feice.mochi.basic.MochiRemovableChip

/**
 * 「我的 → 标签管理」二级页面：维护书籍信息编辑页可复用的标签。
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class)
@Composable
fun TagLibraryScreen(onBack: () -> Unit) {
    val scrollBehavior = GlassTopAppBarDefaults.defaultScrollBehavior()
    val tags by remember { ReusableTagRepository.observeTags() }
        .collectAsState(initial = ReusableTagRepository.tags())
    var input by remember { mutableStateOf("") }

    AppScaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            GlassMediumFlexibleTopAppBar(
                title = stringResource(R.string.tag_library),
                navigationIcon = {
                    TopBarNavigationButton(onClick = onBack)
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (LegadoTheme.composeEngine == ComposeEngine.Mochi) {
                MochiButton(
                    onClick = {
                        val trimmed = input.trim()
                        if (trimmed.isNotEmpty()) {
                            ReusableTagRepository.add(trimmed)
                            input = ""
                        }
                    },
                    style = MochiButtonStyle.Filled,
                    cornerRadius = 20.dp,
                ) {
                    AppIcon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add),
                        tint = LegadoTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            } else {
                io.legado.app.ui.widget.components.AppFloatingActionButton(
                    onClick = {
                        val trimmed = input.trim()
                        if (trimmed.isNotEmpty()) {
                            ReusableTagRepository.add(trimmed)
                            input = ""
                        }
                    },
                    icon = Icons.Default.Add,
                    tooltipText = stringResource(R.string.add),
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = stringResource(R.string.tag),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                MediumOutlinedButton(
                    onClick = {
                        val trimmed = input.trim()
                        if (trimmed.isNotEmpty()) {
                            ReusableTagRepository.add(trimmed)
                            input = ""
                        }
                    },
                    icon = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add),
                )
            }

            if (tags.isEmpty()) {
                EmptyMessage(
                    message = stringResource(R.string.tag_library_empty),
                    modifier = Modifier.padding(vertical = 24.dp),
                )
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    tags.forEach { tag ->
                        if (LegadoTheme.composeEngine == ComposeEngine.Mochi) {
                            MochiRemovableChip(
                                label = tag,
                                onRemove = { ReusableTagRepository.remove(tag) },
                                removeDescription = "${stringResource(R.string.remove)} $tag",
                            )
                        } else {
                            NormalCard(
                                cornerRadius = 12.dp,
                                containerColor = LegadoTheme.colorScheme.surfaceContainer,
                                contentColor = LegadoTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(2.dp),
                                onClick = { ReusableTagRepository.remove(tag) },
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    AppText(
                                        text = tag,
                                        style = LegadoTheme.typography.labelLargeEmphasized,
                                        color = LegadoTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    AppIcon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = LegadoTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
                AppText(
                    text = stringResource(R.string.tag_library_tip),
                    style = LegadoTheme.typography.bodySmall,
                    color = LegadoTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}
