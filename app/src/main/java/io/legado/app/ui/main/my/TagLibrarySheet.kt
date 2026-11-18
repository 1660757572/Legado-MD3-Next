package io.legado.app.ui.main.my

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.legado.app.R
import io.legado.app.data.repository.ReusableTagRepository
import io.legado.app.ui.theme.LegadoTheme
import io.legado.app.ui.widget.components.icon.AppIcon
import io.legado.app.ui.widget.components.AppTextField
import io.legado.app.ui.widget.components.button.series.MediumOutlinedButton
import io.legado.app.ui.widget.components.card.NormalCard
import io.legado.app.ui.widget.components.modalBottomSheet.AppModalBottomSheet
import io.legado.app.ui.widget.components.modalBottomSheet.MochiSheetLayout
import io.legado.app.ui.widget.components.text.AppText
import io.legado.app.ui.widget.components.EmptyMessage

/**
 * 「我的 → 标签管理」：维护可复用标签。书籍信息编辑时会把这些标签列出来点一下即添加。
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class)
@Composable
fun TagLibrarySheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
) {
    val tags by remember { ReusableTagRepository.observeTags() }
        .collectAsState(initial = ReusableTagRepository.tags())
    var input by remember { mutableStateOf("") }

    AppModalBottomSheet(
        show = show,
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.tag_library),
        mochiLayout = MochiSheetLayout.FitContent,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                tags.forEach { tag ->
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
            AppText(
                text = stringResource(R.string.tag_library_tip),
                style = LegadoTheme.typography.bodySmall,
                color = LegadoTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
            )
        }
    }
}
