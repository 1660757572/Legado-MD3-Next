package io.legado.app.ui.book.info.edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.legado.app.R
import io.legado.app.data.repository.ReusableTagRepository
import io.legado.app.ui.book.info.ChangeCoverSheet
import io.legado.app.ui.theme.ComposeEngine
import io.legado.app.ui.theme.LegadoTheme
import io.legado.app.ui.theme.fadingEdge
import io.legado.app.ui.widget.components.AppScaffold
import io.legado.app.ui.widget.components.AppTextField
import io.legado.app.ui.widget.components.alert.AppAlertDialog
import io.legado.app.ui.widget.components.button.series.MediumOutlinedButton
import io.legado.app.ui.widget.components.card.NormalCard
import io.legado.app.ui.widget.components.icon.AppIcon
import io.legado.app.ui.widget.components.icon.AppIcons
import io.legado.app.ui.widget.components.image.cover.CoilBookCover
import io.legado.app.ui.widget.components.menu.AppChoiceOption
import io.legado.app.ui.widget.components.modalBottomSheet.AppModalBottomSheet
import io.legado.app.ui.widget.components.modalBottomSheet.MochiSheetLayout
import io.legado.app.ui.widget.components.reorderAccessibility
import io.legado.app.ui.widget.components.settingItem.SettingItem
import io.legado.app.ui.widget.components.settingItem.SwitchSettingItem
import io.legado.app.ui.widget.components.text.AnimatedTextLine
import io.legado.app.ui.widget.components.text.AppText
import io.legado.app.ui.widget.components.topbar.GlassMediumFlexibleTopAppBar
import io.legado.app.ui.widget.components.topbar.GlassTopAppBarDefaults
import io.legado.app.ui.widget.components.topbar.TopBarActionButton
import io.legado.app.ui.widget.components.topbar.TopBarNavigationButton
import io.legado.app.utils.SelectImageContract
import io.legado.app.utils.launch
import kotlinx.collections.immutable.toImmutableList
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import ui.feice.mochi.basic.MochiButton
import ui.feice.mochi.basic.MochiButtonStyle
import ui.feice.mochi.basic.MochiChip
import ui.feice.mochi.basic.MochiChipGroup
import ui.feice.mochi.basic.MochiTextField

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BookInfoEditScreen(
    viewModel: BookInfoEditViewModel,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onOpenCharacterList: (String) -> Unit,
    onOpenCharacterNetwork: (String) -> Unit,
    onOpenKnowledgeList: (String) -> Unit,
    onOpenEventList: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = GlassTopAppBarDefaults.defaultScrollBehavior()

    AppScaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            GlassMediumFlexibleTopAppBar(
                title = stringResource(id = R.string.book_info_edit),
                navigationIcon = {
                    TopBarNavigationButton(
                        onClick = onBack
                    )
                },
                actions = {
                    TopBarActionButton(
                        onClick = { viewModel.save(onSave) },
                        imageVector = Icons.Default.Save,
                        contentDescription = stringResource(R.string.save)
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        content = { paddingValues ->
            uiState.book?.let {
                BookInfoEditContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                        .imePadding()
                        .verticalScroll(rememberScrollState()),
                    uiState = uiState,
                    viewModel = viewModel,
                    onOpenCharacterList = onOpenCharacterList,
                    onOpenCharacterNetwork = onOpenCharacterNetwork,
                    onOpenKnowledgeList = onOpenKnowledgeList,
                    onOpenEventList = onOpenEventList,
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BookInfoEditContent(
    modifier: Modifier = Modifier,
    uiState: BookInfoEditUiState,
    viewModel: BookInfoEditViewModel,
    onOpenCharacterList: (String) -> Unit,
    onOpenCharacterNetwork: (String) -> Unit,
    onOpenKnowledgeList: (String) -> Unit,
    onOpenEventList: (String) -> Unit,
) {
    val context = LocalContext.current
    var showChangeCoverSheet by remember { mutableStateOf(false) }
    ChangeCoverSheet(
        show = showChangeCoverSheet,
        name = uiState.name,
        author = uiState.author,
        onDismissRequest = { showChangeCoverSheet = false },
        onSelect = { coverUrl ->
            viewModel.onCoverUrlChange(coverUrl)
            showChangeCoverSheet = false
        },
    )

    val selectCover = rememberLauncherForActivityResult(SelectImageContract()) {
        it.uri?.let { uri ->
            viewModel.coverChangeTo(context, uri)
        }
    }

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CoilBookCover(
                name = uiState.name,
                author = uiState.author,
                path = uiState.coverUrl,
                // 编辑页封面同样本地优先：编辑页封面请求不带书标识时，URL 被启动刷新轮换后
                // 精确键会 miss，只能走慢速路径解析书源规则（执行 headerRule 里的登录检测脚本），
                // 每次进编辑页都弹“未登录”提示。
                // 传 bookUrl 后，书架/详情页缓存过的封面（含别名命中）直接本地返回，不碰书源。
                bookUrl = uiState.book?.bookUrl,
                preferCache = true,
                modifier = Modifier
                    .width(110.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MediumOutlinedButton(
                        onClick = { showChangeCoverSheet = true },
                        icon = Icons.Default.ImageSearch,
                        contentDescription = stringResource(R.string.refresh_cover)
                    )
                    MediumOutlinedButton(
                        onClick = { selectCover.launch() },
                        icon = Icons.Default.FolderOpen,
                        contentDescription = stringResource(R.string.select_folder)
                    )
                    MediumOutlinedButton(
                        onClick = { viewModel.resetCover() },
                        icon = Icons.Default.Replay,
                        contentDescription = stringResource(R.string.cover_reset)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        BookKnowledgeEditCard(
            bookUrl = uiState.book?.bookUrl.orEmpty(),
            onOpenCharacterList = onOpenCharacterList,
            onOpenCharacterNetwork = onOpenCharacterNetwork,
            onOpenKnowledgeList = onOpenKnowledgeList,
            onOpenEventList = onOpenEventList,
        )
        Spacer(modifier = Modifier.height(16.dp))
        BookTypeDropdown(
            bookTypes = listOf(
                BookInfoEditType.TEXT to stringResource(R.string.book_type_text),
                BookInfoEditType.AUDIO to stringResource(R.string.book_type_audio),
                BookInfoEditType.VIDEO to stringResource(R.string.video),
                BookInfoEditType.IMAGE to stringResource(R.string.book_type_image)
            ),
            selectedType = uiState.selectedType,
            onTypeSelected = { viewModel.onBookTypeChange(it) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        SwitchSettingItem(
            title = stringResource(R.string.fixed_book_type),
            description = stringResource(R.string.fixed_book_type_summary),
            checked = uiState.fixedType,
            onCheckedChange = { viewModel.onFixedTypeChange(it) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        BookInfoEditTextField(
            value = uiState.name,
            onValueChange = { viewModel.onNameChange(it) },
            label = stringResource(R.string.book_name),
            backgroundColor = LegadoTheme.colorScheme.surfaceInput,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        BookInfoEditTextField(
            value = uiState.author,
            onValueChange = { viewModel.onAuthorChange(it) },
            label = stringResource(R.string.author),
            backgroundColor = LegadoTheme.colorScheme.surfaceInput,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        BookInfoEditTextField(
            value = uiState.coverUrl ?: "",
            onValueChange = { viewModel.onCoverUrlChange(it) },
            label = stringResource(R.string.cover_url),
            backgroundColor = LegadoTheme.colorScheme.surfaceInput,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        var showSourceKinds by rememberSaveable { mutableStateOf(true) }
        SwitchSettingItem(
            title = stringResource(R.string.show_source_categories),
            description = stringResource(R.string.show_source_categories_summary),
            checked = showSourceKinds,
            onCheckedChange = { showSourceKinds = it }
        )
        if (showSourceKinds) {
            Spacer(modifier = Modifier.height(8.dp))
            BookInfoEditTextField(
                value = uiState.sourceKindList.joinToString(", "),
                onValueChange = {},
                readOnly = true,
                label = stringResource(R.string.source_categories),
                backgroundColor = LegadoTheme.colorScheme.surfaceInput,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        KindEditor(
            kindList = uiState.kindList,
            onKindListChange = { viewModel.onKindListChange(it) },
            onReset = { viewModel.resetKinds() },
            backgroundColor = LegadoTheme.colorScheme.surfaceInput,
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Mochi 输入框有字数上限，内容简介动辄上千字，这里在大段文本上回退到通用输入框。
        AppTextField(
            value = uiState.intro ?: "",
            onValueChange = { viewModel.onIntroChange(it) },
            label = stringResource(R.string.book_intro),
            backgroundColor = LegadoTheme.colorScheme.surfaceInput,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        BookInfoEditTextField(
            value = uiState.remark ?: "",
            onValueChange = { viewModel.onRemarkChange(it) },
            label = stringResource(R.string.book_remark),
            backgroundColor = LegadoTheme.colorScheme.surfaceInput,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun BookInfoEditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
) {
    if (LegadoTheme.composeEngine == ComposeEngine.Mochi) {
        MochiTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            placeholder = label,
            enabled = !readOnly,
            singleLine = singleLine,
            maxLines = if (singleLine) 1 else maxLines.coerceAtMost(6),
            clearButton = !readOnly,
        )
    } else {
        AppTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            readOnly = readOnly,
            label = label,
            backgroundColor = backgroundColor,
            singleLine = singleLine,
            maxLines = maxLines,
        )
    }
}

@Composable
private fun BookKnowledgeEditCard(
    bookUrl: String,
    onOpenCharacterList: (String) -> Unit,
    onOpenCharacterNetwork: (String) -> Unit,
    onOpenKnowledgeList: (String) -> Unit,
    onOpenEventList: (String) -> Unit,
) {
    NormalCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppText(
                text = stringResource(R.string.book_info_knowledge),
                style = LegadoTheme.typography.titleMedium,
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    MediumOutlinedButton(
                        onClick = { onOpenCharacterList(bookUrl) },
                        enabled = bookUrl.isNotEmpty(),
                        icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
                        text = stringResource(R.string.book_characters),
                    )
                }
                item {
                    MediumOutlinedButton(
                        onClick = { onOpenCharacterNetwork(bookUrl) },
                        enabled = bookUrl.isNotEmpty(),
                        icon = Icons.Default.Group,
                        text = stringResource(R.string.character_network),
                    )
                }
                item {
                    MediumOutlinedButton(
                        onClick = { onOpenKnowledgeList(bookUrl) },
                        enabled = bookUrl.isNotEmpty(),
                        icon = Icons.Default.Book,
                        text = stringResource(R.string.book_knowledge),
                    )
                }
                item {
                    MediumOutlinedButton(
                        onClick = { onOpenEventList(bookUrl) },
                        enabled = bookUrl.isNotEmpty(),
                        icon = Icons.Default.Timeline,
                        text = stringResource(R.string.plot_events),
                    )
                }
            }
        }
    }
}

@Composable
fun BookTypeDropdown(
    bookTypes: List<Pair<BookInfoEditType, String>>,
    selectedType: BookInfoEditType,
    onTypeSelected: (BookInfoEditType) -> Unit
) {
    // SettingItem 内置受控 AppChoiceDropdown：四引擎原生单选行，锚点在右侧取值区。
    // ID 用枚举名（唯一且稳定），回解时按 bookTypes 校验，不依赖列表下标。
    // trailingContent 必须给可见内容：SettingItem 的 trailing Box 只靠它撑尺寸，
    // 否则 matchParentSize() 的弹层锚点退化成 0 尺寸，弹层会飘到窗口左上角。
    // 底色与取值文本都走 LegadoTheme，避免编辑页里混进设置页的 MaterialTheme 配色。
    SettingItem(
        color = LegadoTheme.colorScheme.surfaceInput,
        title = stringResource(R.string.book_type_label),
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AppText(
                    text = bookTypes.firstOrNull { it.first == selectedType }?.second.orEmpty(),
                    style = LegadoTheme.typography.labelMediumEmphasized,
                    color = LegadoTheme.colorScheme.onSurface,
                )
                AppIcon(
                    imageVector = AppIcons.ExpandMore,
                    contentDescription = null,
                )
            }
        },
        choiceOptions = bookTypes.map { (type, label) ->
            AppChoiceOption(id = type.name, label = label)
        }.toImmutableList(),
        choiceSelectedId = selectedType.name,
        onChoiceSelect = { id ->
            bookTypes.firstOrNull { it.first.name == id }?.first?.let(onTypeSelected)
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class)
@Composable
private fun TagEditorActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    if (LegadoTheme.composeEngine == ComposeEngine.Mochi) {
        MochiButton(
            onClick = onClick,
            style = MochiButtonStyle.Tonal,
            minHeight = 38.dp,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = LegadoTheme.colorScheme.onSurface,
            )
            AppText(text = text, fontSize = 13.sp)        }
    } else {
        MediumOutlinedButton(
            onClick = onClick,
            icon = icon,
            text = text,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class)
@Composable
fun KindEditor(
    kindList: List<String>,
    onKindListChange: (List<String>) -> Unit,
    onReset: () -> Unit,
    backgroundColor: Color
) {
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var editText by remember { mutableStateOf("") }
    val hapticFeedback = LocalHapticFeedback.current
    val reusableTags by remember { ReusableTagRepository.observeTags() }
        .collectAsState(initial = ReusableTagRepository.tags())
    var showTagLibrary by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    fun moveKind(from: Int, to: Int) {
        val mutable = kindList.toMutableList()
        mutable.add(to, mutable.removeAt(from))
        onKindListChange(mutable)
    }
    val reorderableState = rememberReorderableLazyListState(listState) { from, to ->
        moveKind(from.index, to.index)
    }

    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AppText(
            text = stringResource(R.string.my_tags)
        )
        // 标签库、重置、添加统一放右侧，Mochi 主题下使用 Mochi 按钮组件。
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (reusableTags.isNotEmpty()) {
                TagEditorActionButton(
                    text = stringResource(R.string.tag_library_quick),
                    icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
                    onClick = { showTagLibrary = true },
                )
            }
            TagEditorActionButton(
                text = stringResource(R.string.reset),
                icon = Icons.Default.Replay,
                onClick = onReset,
            )
            TagEditorActionButton(
                text = stringResource(R.string.add),
                icon = Icons.Default.Add,
                onClick = {
                    editingIndex = -1
                    editText = ""
                },
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fadingEdge(listState),
                contentPadding = PaddingValues(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(kindList.size, key = { kindList[it] }) { index ->
                    val kind = kindList[index]
                    ReorderableItem(
                        state = reorderableState,
                        key = kind
                    ) { isDragging ->
                        KindChip(
                            text = kind,
                            isDragging = isDragging,
                            onClick = {
                                editingIndex = index
                                editText = kind
                            },
                            modifier = Modifier
                                .reorderAccessibility(
                                    index = index,
                                    itemCount = kindList.size,
                                    onMove = ::moveKind,
                                )
                                .longPressDraggableHandle(
                                    onDragStarted = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                    },
                                    onDragStopped = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                    }
                                )
                                .animateItem()
                        )
                    }
                }
            }
        }
    }


    AppModalBottomSheet(
        show = showTagLibrary,
        onDismissRequest = { showTagLibrary = false },
        title = stringResource(R.string.tag_library_quick),
        mochiLayout = MochiSheetLayout.Compact,
        startAction = {
            IconButton(onClick = { showTagLibrary = false }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close)
                )
            }
        },
        endAction = {
            IconButton(onClick = { showTagLibrary = false }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.confirm)
                )
            }
        },
    ) {
        if (LegadoTheme.composeEngine == ComposeEngine.Mochi) {
            MochiChipGroup(
                modifier = Modifier.fillMaxWidth(),
                wrap = true,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                reusableTags.forEach { tag ->
                    val added = tag in kindList
                    MochiChip(
                        label = tag,
                        selected = added,
                        showCheck = true,
                        onClick = {
                            onKindListChange(if (added) kindList - tag else kindList + tag)
                        },
                    )
                }
            }
        } else {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                reusableTags.forEach { tag ->
                    val added = tag in kindList
                    KindChip(
                        text = tag,
                        isDragging = false,
                        selected = added,
                        onClick = {
                            onKindListChange(if (added) kindList - tag else kindList + tag)
                        },
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (editingIndex != null) {
        val isAdding = editingIndex == -1
        AppAlertDialog(
            show = editingIndex != null,
            onDismissRequest = { editingIndex = null },
            title = if (isAdding) {
                stringResource(R.string.add_tag)
            } else {
                stringResource(R.string.edit_tag)
            },
            content = {
                BookInfoEditTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    label = stringResource(R.string.tag),
                    backgroundColor = backgroundColor,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmText = stringResource(android.R.string.ok),
            onConfirm = {
                val trimmed = editText.trim()
                if (trimmed.isNotBlank()) {
                    val mutable = kindList.toMutableList()
                    if (isAdding) {
                        if (trimmed !in kindList) mutable.add(trimmed)
                    } else {
                        mutable[editingIndex!!] = trimmed
                    }
                    onKindListChange(mutable)
                }
                editingIndex = null
            },
            dismissText = if (isAdding) stringResource(android.R.string.cancel) else stringResource(
                R.string.delete
            ),
            onDismiss = if (isAdding) {
                { editingIndex = null }
            } else {
                {
                    onKindListChange(kindList.toMutableList().apply { removeAt(editingIndex!!) })
                    editingIndex = null
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun KindChip(
    text: String,
    isDragging: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    NormalCard(
        cornerRadius = 8.dp,
        containerColor = if (selected) {
            LegadoTheme.colorScheme.primaryContainer
        } else {
            LegadoTheme.colorScheme.surfaceContainer
        },
        contentColor = if (selected) {
            LegadoTheme.colorScheme.onPrimaryContainer
        } else {
            LegadoTheme.colorScheme.onSurface
        },
        elevation = if (isDragging) 2.dp else 0.dp,
        modifier = modifier
            .padding(2.dp)
            .zIndex(if (isDragging) 1f else 0f)
            .clickable(onClick = onClick)
    ) {
        AnimatedTextLine(
            text = text,
            style = LegadoTheme.typography.labelLargeEmphasized,
            color = if (selected) {
                LegadoTheme.colorScheme.onPrimaryContainer
            } else {
                LegadoTheme.colorScheme.onSurface
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
