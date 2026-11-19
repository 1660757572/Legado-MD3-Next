package io.legado.app.ui.main.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import io.legado.app.ui.widget.components.coui.appVerticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.legado.app.R
import io.legado.app.data.entities.Book
import io.legado.app.data.entities.SearchBook
import io.legado.app.domain.model.HomeDashboardSection
import io.legado.app.domain.model.MAX_DAILY_READING_GOAL_MINUTES
import io.legado.app.lib.permission.Permissions
import io.legado.app.lib.permission.PermissionsCompat
import io.legado.app.ui.config.backupConfig.BackupOptionSheet
import io.legado.app.ui.config.backupConfig.RestoreOptionSheet
import io.legado.app.ui.main.bookCoverSharedElementKey
import io.legado.app.ui.main.homepage.HomepageFeedActions
import io.legado.app.ui.main.homepage.HomepageManageActions
import io.legado.app.ui.main.homepage.HomepageModuleFeed
import io.legado.app.ui.main.homepage.HomepageModuleManageSheet
import io.legado.app.ui.main.homepage.HomepageUiState
import io.legado.app.ui.main.homepage.HomepageViewModel
import io.legado.app.ui.theme.LegadoTheme
import io.legado.app.ui.theme.LocalBottomOverlayPadding
import io.legado.app.ui.widget.components.AppScaffold
import io.legado.app.ui.widget.components.alert.AppAlertDialog
import io.legado.app.ui.widget.components.book.SearchBookPreviewSheet
import io.legado.app.ui.widget.components.button.series.MediumTonalButton
import io.legado.app.ui.widget.components.button.series.SmallTonalButton
import io.legado.app.ui.widget.components.card.GlassCard
import io.legado.app.ui.widget.components.card.TextCard
import io.legado.app.ui.widget.components.conflict.BookshelfConflictSheet
import io.legado.app.ui.widget.components.icon.AppIcon
import io.legado.app.ui.widget.components.icon.AppIcons
import io.legado.app.ui.widget.components.image.cover.BookshelfCover
import io.legado.app.ui.widget.components.menu.AppAction
import io.legado.app.ui.widget.components.menu.AppActionGroup
import io.legado.app.ui.widget.components.menu.AppActionMenu
import io.legado.app.ui.widget.components.progressIndicator.AppContainedLoadingIndicator
import io.legado.app.ui.widget.components.settingItem.TinySwitchSettingItem
import io.legado.app.ui.widget.components.text.AppText
import io.legado.app.ui.widget.components.topbar.GlassMediumFlexibleTopAppBar
import io.legado.app.ui.widget.components.topbar.GlassTopAppBarDefaults
import io.legado.app.ui.widget.components.topbar.TopBarActionButton
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import io.legado.app.utils.isContentScheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import io.legado.app.utils.sendToClip
import io.legado.app.utils.takePersistablePermissionSafely
import io.legado.app.utils.toastOnUi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.DateFormat
import java.util.Date
import kotlin.math.roundToInt

@Composable
fun HomeRouteScreen(
    onOpenBook: (Book) -> Unit,
    onNavigateToBookInfo: (
        name: String?,
        author: String?,
        bookUrl: String,
        origin: String?,
        coverPath: String?,
        sharedCoverKey: String?,
    ) -> Unit,
    onOpenExploreShow: (
        title: String?,
        sourceUrl: String,
        exploreUrl: String?,
    ) -> Unit,
    onOpenBackupSettings: () -> Unit,
    onNavigateToReadRecord: () -> Unit,
    onNavigateToReadRecordOverview: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    viewModel: HomeViewModel = koinViewModel(),
    homepageViewModel: HomepageViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val homepageState by homepageViewModel.uiState.collectAsStateWithLifecycle()
    var previewBook by remember { mutableStateOf<SearchBook?>(null) }
    var previewSharedCoverKey by remember { mutableStateOf<String?>(null) }

    val feedActions = remember(homepageViewModel, onNavigateToBookInfo, onOpenExploreShow) {
        HomepageFeedActions(
            onModuleHeaderClick = { sourceUrl, exploreUrl, title ->
                onOpenExploreShow(title, sourceUrl, exploreUrl)
            },
            onRetryModule = homepageViewModel::retryModule,
            onLoadMoreModule = homepageViewModel::loadMoreModule,
            onBookClick = { book: SearchBook, sharedCoverKey: String? ->
                onNavigateToBookInfo(
                    book.name,
                    book.author,
                    book.bookUrl,
                    book.origin,
                    book.coverUrl,
                    sharedCoverKey,
                )
            },
            onKindUrlClick = homepageViewModel::onKindUrlClick,
            onRefreshButtonGroup = homepageViewModel::refreshButtonGroup,
        )
    }
    val manageActions = remember(homepageViewModel) {
        HomepageManageActions(
            onToggleSet = { url, isEnabled ->
                homepageViewModel.toggleSourceFilter(url, isEnabled)
            },
            onGetSourceModules = { url, setId ->
                homepageViewModel.getSourceModules(url, setId)
            },
            onSyncSourceModules = { homepageViewModel.syncSourceModules(it) },
            onToggleModule = { id, visible ->
                homepageViewModel.setModuleVisible(id, visible)
            },
            onJoinModule = { sourceUrl, targetSetId, def ->
                homepageViewModel.joinModule(sourceUrl, targetSetId, def)
            },
            onAddCustomModule = { sourceUrl, targetSetId, def ->
                homepageViewModel.addCustomModule(sourceUrl, targetSetId, def)
            },
            onAddButtonGroupFromKinds = { sourceUrl, targetSetId, title, kinds ->
                homepageViewModel.addButtonGroupFromKinds(sourceUrl, targetSetId, title, kinds)
            },
            onAddRankingFromKinds = { sourceUrl, targetSetId, title, type, kinds ->
                homepageViewModel.addRankingFromKinds(
                    sourceUrl,
                    targetSetId,
                    title,
                    type,
                    kinds
                )
            },
            onGetExploreKinds = { homepageViewModel.getSourceExploreKinds(it) },
            onUpdateModule = { globalId, def ->
                homepageViewModel.updateModule(globalId, def)
            },
            onDeleteModule = { homepageViewModel.deleteModule(it) },
            onReorderModules = { homepageViewModel.reorderJoinedModules(it) },
            onReorderSets = { homepageViewModel.reorderCustomSets(it) },
            onSetCustomSetTitle = { id, title ->
                homepageViewModel.setModuleCustomSetTitle(id, title)
            },
            onCreateCustomSet = { homepageViewModel.createCustomSet(it) },
            onRenameCustomSet = { id, name ->
                homepageViewModel.renameCustomSet(id, name)
            },
            onDeleteCustomSet = { homepageViewModel.deleteCustomSet(it) },
            onAssignModuleToCustomSet = { id, setId ->
                homepageViewModel.assignModuleToCustomSet(id, setId)
            },
        )
    }

    var pendingBackupDestination by remember {
        mutableStateOf<HomeBackupDestination?>(null)
    }
    val backupDirectoryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
    ) { uri ->
        val destination = pendingBackupDestination
        pendingBackupDestination = null
        if (uri != null && destination != null) {
            uri.takePersistablePermissionSafely(context)
            val path = if (uri.isContentScheme()) {
                uri.toString()
            } else {
                uri.path.orEmpty()
            }
            if (path.isNotEmpty()) {
                viewModel.onIntent(
                    HomeIntent.BackupDirectorySelected(
                        destination = destination,
                        path = path,
                    )
                )
            }
        }
    }
    val restoreFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let {
            viewModel.onIntent(HomeIntent.RestoreLocalFileSelected(it.toString()))
        }
    }

    LaunchedEffect(viewModel, context) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is HomeEffect.OpenBook -> onOpenBook(effect.book)
                HomeEffect.OpenBackupSettings -> onOpenBackupSettings()
                is HomeEffect.SelectBackupDirectory -> {
                    pendingBackupDestination = effect.destination
                    runCatching { backupDirectoryLauncher.launch(null) }
                }

                is HomeEffect.RequestBackupStoragePermission -> {
                    PermissionsCompat.Builder()
                        .addPermissions(*Permissions.Group.STORAGE)
                        .rationale(R.string.tip_perm_request_storage)
                        .onGranted {
                            viewModel.onIntent(
                                HomeIntent.BackupDirectorySelected(
                                    destination = effect.destination,
                                    path = effect.path,
                                )
                            )
                        }
                        .request()
                }

                HomeEffect.SelectRestoreFile -> {
                    restoreFileLauncher.launch(arrayOf("application/zip"))
                }

                is HomeEffect.ShowMessage -> {
                    val message = buildString {
                        append(context.getString(effect.messageRes))
                        effect.detail?.takeIf { it.isNotBlank() }?.let {
                            append('\n')
                            append(it)
                        }
                    }
                    context.toastOnUi(message)
                }
            }
        }
    }

    LaunchedEffect(homepageViewModel) {
        homepageViewModel.effects.collectLatest { effect ->
            when (effect) {
                is io.legado.app.ui.main.homepage.HomepageEffect.NavigateToBookInfo ->
                    onNavigateToBookInfo(
                        effect.name,
                        effect.author,
                        effect.bookUrl,
                        effect.origin,
                        effect.coverPath,
                        effect.sharedCoverKey,
                    )

                is io.legado.app.ui.main.homepage.HomepageEffect.NavigateToExploreShow ->
                    onOpenExploreShow(effect.title, effect.sourceUrl, effect.exploreUrl)

                is io.legado.app.ui.main.homepage.HomepageEffect.ShowSnackbar -> {}
            }
        }
    }

    HomeScreen(
        state = state,
        homepageState = homepageState,
        homepageFeedActions = feedActions,
        homepageManageActions = manageActions,
        onIntent = viewModel::onIntent,
        onRefreshHomepage = homepageViewModel::onRefresh,
        onToggleHomepageManage = homepageViewModel::toggleManageMode,
        onNavigateToReadRecord = onNavigateToReadRecord,
        onNavigateToReadRecordOverview = onNavigateToReadRecordOverview,
        onOpenBookInfo = { book ->
            // 与阅读页同一个封面共享元素契约：点轮播进详情，不绕过详情直接开读
            onNavigateToBookInfo(
                book.name,
                book.author,
                book.bookUrl,
                book.origin,
                book.coverPath,
                null,
            )
        },
        onHomepageBookLongClick = { book, sharedCoverKey ->
            previewBook = book
            previewSharedCoverKey = sharedCoverKey
        },
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
    )

    SearchBookPreviewSheet(
        data = previewBook,
        shelfState = previewBook?.let { homepageViewModel.getCurrentBookShelfState(it) },
        sharedCoverKey = previewSharedCoverKey,
        onDismissRequest = { previewBook = null },
        onOpenDetail = { book, sharedCoverKey ->
            previewBook = null
            onNavigateToBookInfo(
                book.name,
                book.author,
                book.bookUrl,
                book.origin,
                book.coverUrl,
                sharedCoverKey,
            )
        },
        onAddToShelf = { book ->
            homepageViewModel.onAddToShelf(book)
        },
    )

    BookshelfConflictSheet(
        conflict = homepageState.bookshelfConflict,
        isResolving = homepageState.isResolvingBookshelfConflict,
        onDismissRequest = homepageViewModel::dismissBookshelfConflict,
        onOpenExistingBook = homepageViewModel::openBookshelfConflictBook,
        onCoexist = homepageViewModel::coexistWithBookshelfConflict,
        onMigrate = homepageViewModel::migrateBookshelfConflict,
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalSharedTransitionApi::class,
)
@Composable
fun HomeScreen(
    state: HomeUiState,
    homepageState: HomepageUiState,
    homepageFeedActions: HomepageFeedActions,
    homepageManageActions: HomepageManageActions,
    onIntent: (HomeIntent) -> Unit,
    onRefreshHomepage: () -> Unit,
    onToggleHomepageManage: () -> Unit,
    onNavigateToReadRecord: () -> Unit = {},
    onNavigateToReadRecordOverview: () -> Unit = {},
    onOpenBookInfo: (HomeCarouselBookUi) -> Unit = {},
    onHomepageBookLongClick: (SearchBook, String?) -> Unit = { _, _ -> },
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {
    val scrollBehavior = GlassTopAppBarDefaults.defaultScrollBehavior()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val selectedSets = remember(homepageState.manageState.sets) {
        homepageState.manageState.sets.filter { it.isSelected }
    }
    val pagerState = rememberPagerState(pageCount = {
        selectedSets.size.coerceAtLeast(1)
    })

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val currentPageSourceName by remember(selectedSets, pagerState) {
        derivedStateOf { selectedSets.getOrNull(pagerState.currentPage)?.sourceName }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                val url = selectedSets.getOrNull(page)?.sourceUrl ?: return@collect
                onIntent(HomeIntent.SelectSourceSet(url))
            }
    }

    LaunchedEffect(state.selectedSourceSetUrl, pagerState) {
        val targetUrl = state.selectedSourceSetUrl ?: return@LaunchedEffect
        val targetIndex = selectedSets.indexOfFirst { it.sourceUrl == targetUrl }
        if (targetIndex >= 0 && targetIndex != pagerState.currentPage) {
            pagerState.animateScrollToPage(targetIndex)
        }
    }

    AppScaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            GlassMediumFlexibleTopAppBar(
                title = stringResource(R.string.home),
                subtitle = currentPageSourceName,
                scrollBehavior = scrollBehavior,
                actions = {
                    // 「首页模块」在首页组件里关掉后，模块流与其管理入口一起隐藏。
                    if (state.homepageModulesEnabled) {
                        TopBarActionButton(
                            onClick = onToggleHomepageManage,
                            imageVector = AppIcons.Settings,
                            contentDescription = stringResource(R.string.homepage_module_manage),
                        )
                    }
                    AppActionMenu(
                        groups = persistentListOf(
                            AppActionGroup(
                                id = "home_actions",
                                actions = persistentListOf(
                                    AppAction(
                                        id = "refresh_homepage",
                                        label = stringResource(R.string.refresh),
                                        icon = Icons.Default.Refresh,
                                    ),
                                    AppAction(
                                        "dashboard_settings",
                                        stringResource(R.string.home_dashboard_settings),
                                    ),
                                ),
                            ),
                            AppActionGroup(
                                id = "home_pages",
                                actions = selectedSets.mapIndexed { index, source ->
                                    AppAction(
                                        id = "page:$index",
                                        label = source.sourceName,
                                        selected = index == pagerState.currentPage,
                                    )
                                }.toImmutableList(),
                            ),
                        ),
                        onAction = { id ->
                            when {
                                id == "refresh_homepage" -> onRefreshHomepage()
                                id == "dashboard_settings" ->
                                    onIntent(HomeIntent.DashboardSettingsClick)
                                id.startsWith("page:") -> {
                                    val index = id.removePrefix("page:").toIntOrNull()
                                    if (index != null && index in selectedSets.indices) {
                                        scope.launch { pagerState.animateScrollToPage(index) }
                                    }
                                }
                            }
                        },
                    ) {
                        AppIcon(
                            imageVector = AppIcons.MoreVert,
                            contentDescription = stringResource(R.string.more_menu),
                            modifier = this.modifier,
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        // 下拉刷新会和越界回弹抢手势，刷新入口改由右上角更多菜单提供。
        Box(modifier = Modifier.fillMaxSize()) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val dashboardScrollState = rememberScrollState()
                val moduleNestedScrollConnection = remember(dashboardScrollState) {
                    object : NestedScrollConnection {
                        override fun onPreScroll(
                            available: Offset,
                            source: NestedScrollSource,
                        ): Offset {
                            return if (available.y >= 0f) {
                                Offset.Zero
                            } else {
                                val consumed = dashboardScrollState.dispatchRawDelta(-available.y)
                                Offset(0f, -consumed)
                            }
                        }

                        override fun onPostScroll(
                            consumed: Offset,
                            available: Offset,
                            source: NestedScrollSource,
                        ): Offset {
                            return if (available.y <= 0f) {
                                Offset.Zero
                            } else {
                                val c = dashboardScrollState.dispatchRawDelta(-available.y)
                                Offset(0f, -c)
                            }
                        }
                    }
                }
                val viewportHeight = maxHeight
                val hasDashboard = state.visibleSections.isNotEmpty()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .appVerticalScroll(dashboardScrollState),
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(paddingValues.calculateTopPadding() + 8.dp),
                    )
                    if (hasDashboard) {
                        HomeDashboardContent(
                            state = state,
                            onIntent = onIntent,
                            onNavigateToReadRecord = onNavigateToReadRecord,
                            onNavigateToReadRecordOverview = onNavigateToReadRecordOverview,
                            onOpenBookInfo = onOpenBookInfo,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }

                    // 「首页模块」关闭时不显示模块流，首页只保留上面的组件区。
                    if (state.homepageModulesEnabled) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(viewportHeight),
                        ) {
                            if (selectedSets.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    AppText(
                                        stringResource(R.string.homepage_no_source_sets_selected)
                                    )
                                }
                            } else {
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .nestedScroll(moduleNestedScrollConnection),
                                    key = { index ->
                                        selectedSets.getOrNull(index)?.sourceUrl ?: index
                                    },
                                ) { pageIndex ->
                                    val source = selectedSets.getOrNull(pageIndex)
                                    val sourceModules =
                                        remember(homepageState.modules, source) {
                                            homepageState.modules.filter { module ->
                                                if (source?.isCustomSet == true) {
                                                    val setId = HomepageViewModel
                                                        .customSetIdFromUrl(source.sourceUrl)
                                                    module.customSetId == setId
                                                } else {
                                                    module.sourceUrl == source?.sourceUrl
                                                }
                                            }
                                        }
                                    HomepageModuleFeed(
                                        modules = sourceModules,
                                        actions = homepageFeedActions,
                                        modifier = Modifier.fillMaxSize(),
                                        sharedTransitionScope = sharedTransitionScope,
                                        animatedVisibilityScope = animatedVisibilityScope,
                                        onBookLongClick = onHomepageBookLongClick,
                                        onErrorClick = { errorMessage = it },
                                    )
                                }
                            }
                        }
                    }
                    // 「首页模块」关闭时下面没有模块流撑高度，必须自己补一段底部留白，
                    // 否则最后一个组件会被悬浮胶囊底栏压住。
                    if (!state.homepageModulesEnabled) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp.coerceAtLeast(LocalBottomOverlayPadding.current)),
                        )
                    }
                }

            }
        }

        HomepageModuleManageSheet(
            data = if (homepageState.isManageMode) Unit else null,
            onDismissRequest = onToggleHomepageManage,
            state = homepageState.manageState,
            actions = homepageManageActions,
        )

        HomeDialogs(
            dialog = state.activeDialog,
            onIntent = onIntent,
        )
        HomeSheets(
            sheet = state.activeSheet,
            sections = state.visibleSections,
            homepageModulesEnabled = state.homepageModulesEnabled,
            carouselAutoPlay = state.carouselAutoPlay,
            onIntent = onIntent,
        )
        AppAlertDialog(
            data = errorMessage,
            onDismissRequest = { errorMessage = null },
            title = stringResource(R.string.error_details),
            confirmText = stringResource(R.string.copy_text),
            onConfirm = { message ->
                context.sendToClip(message)
                errorMessage = null
            },
            dismissText = stringResource(R.string.close),
            onDismiss = { errorMessage = null },
            content = { message ->
                SelectionContainer {
                    AppText(
                        text = message,
                        style = LegadoTheme.typography.bodyMedium,
                        modifier = Modifier
                            .heightIn(max = 400.dp)
                            .appVerticalScroll(rememberScrollState()),
                    )
                }
            },
        )
    }
}

@Composable
private fun HomeDashboardContent(
    state: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    onNavigateToReadRecord: () -> Unit,
    onNavigateToReadRecordOverview: () -> Unit,
    onOpenBookInfo: (HomeCarouselBookUi) -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // 顺序完全由 visibleSections 决定；统计卡两张并排，所以它们共用第一个出现的位置。
        var statisticRowRendered = false
        state.visibleSections.forEach { section ->
            when (section) {
                HomeDashboardSection.RecentBook -> RecentBookCard(
                    book = state.recentBook,
                    onClick = { onIntent(HomeIntent.RecentBookClick) },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                )

                HomeDashboardSection.BookCarousel -> if (state.carouselBooks.isNotEmpty()) {
                    RandomBooksCarousel(
                        books = state.carouselBooks,
                        autoPlay = state.carouselAutoPlay,
                        // 轮播只做发现入口：点书进详情页，不直接跳阅读
                        onBookClick = onOpenBookInfo,
                    )
                }

                HomeDashboardSection.TotalReadBooks,
                HomeDashboardSection.TotalReadTime -> if (!statisticRowRendered) {
                    statisticRowRendered = true
                    StatisticCardsRow(
                        state = state,
                        onNavigateToReadRecord = onNavigateToReadRecord,
                        onNavigateToReadRecordOverview = onNavigateToReadRecordOverview,
                    )
                }

                HomeDashboardSection.RecentBooks -> if (state.recentBooks.isNotEmpty()) {
                    RecentBooksRow(
                        books = state.recentBooks,
                        onBookClick = { url -> onIntent(HomeIntent.RecentHistoryBookClick(url)) },
                    )
                }

                HomeDashboardSection.DailyGoal -> ReadingGoalCard(
                    todayReadTimeMillis = state.todayReadTimeMillis,
                    goalMinutes = state.dailyGoalMinutes,
                    onClick = { onIntent(HomeIntent.ReadingGoalClick) },
                )

                HomeDashboardSection.WebDavBackup -> WebDavBackupCard(
                    latestBackup = state.latestBackup,
                    isLoading = state.isBackupLoading,
                    isLoadError = state.isBackupLoadError,
                    isActionRunning = state.isBackupActionRunning,
                    onBackup = { onIntent(HomeIntent.BackupClick) },
                    onRestore = { onIntent(HomeIntent.RestoreClick) },
                    onOpenSettings = { onIntent(HomeIntent.BackupSettingsClick) },
                    onRetry = { onIntent(HomeIntent.RetryBackupInfo) },
                )
            }
        }
    }
}

@Composable
private fun StatisticCardsRow(
    state: HomeUiState,
    onNavigateToReadRecord: () -> Unit,
    onNavigateToReadRecordOverview: () -> Unit,
) {
    val hasStatRead = HomeDashboardSection.TotalReadBooks in state.visibleSections
    val hasStatTime = HomeDashboardSection.TotalReadTime in state.visibleSections
    if (!hasStatRead && !hasStatTime) return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (hasStatRead) {
            StatisticCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Leaderboard,
                title = stringResource(R.string.home_total_read_books),
                value = state.totalReadBooks.toString(),
                unit = stringResource(R.string.unit_books),
                onClick = onNavigateToReadRecord,
            )
        }
        if (hasStatTime) {
            StatisticCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.AccessTime,
                title = stringResource(R.string.home_total_reading_time),
                value = String.format(
                    "%.1f",
                    state.totalReadTimeMillis / 3_600_000.0,
                ),
                unit = stringResource(R.string.unit_hours),
                onClick = onNavigateToReadRecordOverview,
            )
        }
    }
}

/**
 * 随机推荐轮播：从书架里随机抽几本，自动轮播 + 手动横滑，点封面进书籍详情。
 */
@Composable
private fun RandomBooksCarousel(
    books: ImmutableList<HomeCarouselBookUi>,
    autoPlay: Boolean,
    onBookClick: (HomeCarouselBookUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (books.isEmpty()) return
    // 无限循环：页数设为极大值，用 page % books.size 映射回真实书目，
    // 首尾可直接继续滑动，自动轮播也不再回到第一页。
    val loopPageCount = Int.MAX_VALUE
    val initialPage = remember(books.size) {
        val middle = loopPageCount / 2
        middle - middle % books.size
    }
    val pagerState = rememberPagerState(initialPage = initialPage) { loopPageCount }
    // 记录最后一次滑动（含手动拖动和自动翻页）的结束时间，计时从松手后重新开始，
    // 避免用户刚滑完、旧的自动播放计时就到点，看起来像被弹回原位。
    var lastScrollAt by remember { mutableStateOf(0L) }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress }
            .filter { it }
            .collect { lastScrollAt = System.currentTimeMillis() }
    }
    LaunchedEffect(pagerState, books.size, autoPlay) {
        if (!autoPlay || books.size <= 1) return@LaunchedEffect
        while (true) {
            delay(CAROUSEL_INTERVAL_MILLIS)
            if (!pagerState.isScrollInProgress &&
                System.currentTimeMillis() - lastScrollAt >= CAROUSEL_INTERVAL_MILLIS
            ) {
                val next = pagerState.currentPage + 1
                pagerState.animateScrollToPage(next)
            }
        }
    }
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AppIcon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = null,
                    tint = LegadoTheme.colorScheme.primary,
                )
                AppText(
                    text = stringResource(R.string.home_random_recommend),
                    style = LegadoTheme.typography.titleMediumEmphasized,
                )
            }
            HorizontalPager(
                state = pagerState,
                // 页数有限，到边界后平台 stretch 过冲会在松手时反向回弹；轮播不需要越界反馈。
                overscrollEffect = null,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                pageSpacing = 12.dp,
            ) { page ->
                val book = books[page % books.size]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            role = Role.Button,
                            onClick = { onBookClick(book) },
                        )
                        .semantics {
                            contentDescription = bookAccessibilityLabel(book.name, book.author)
                            role = Role.Button
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .width(72.dp)
                            .aspectRatio(5f / 7f)
                            .clip(RoundedCornerShape(10.dp)),
                    ) {
                        BookshelfCover(
                            name = book.name,
                            author = book.author,
                            path = book.coverPath,
                            sourceOrigin = book.origin,
                            modifier = Modifier.fillMaxSize(),
                            coverModifier = Modifier.fillMaxSize(),
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        AppText(
                            text = book.name,
                            style = LegadoTheme.typography.titleMediumEmphasized,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        AppText(
                            text = book.author,
                            style = LegadoTheme.typography.bodySmall,
                            color = LegadoTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            if (books.size > 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    val currentBookIndex = pagerState.currentPage % books.size
                    books.indices.forEach { index ->
                        val selected = currentBookIndex == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(width = if (selected) 16.dp else 6.dp, height = 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (selected) {
                                        LegadoTheme.colorScheme.primary
                                    } else {
                                        LegadoTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                    }
                                )
                                .animateContentSize(),
                        )
                    }
                }
            }
        }
    }
}

private const val CAROUSEL_INTERVAL_MILLIS = 4_000L

@Composable
private fun StatisticCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    unit: String,
    onClick: () -> Unit,
) {
    GlassCard(
        modifier = modifier,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 10.dp,
                    end = 16.dp,
                    bottom = 8.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppIcon(
                imageVector = icon,
                contentDescription = null,
                tint = LegadoTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
            Column {
                AppText(
                    text = title,
                    style = LegadoTheme.typography.bodySmall,
                    color = LegadoTheme.colorScheme.onSurfaceVariant,
                )
                Row {
                    AppText(
                        text = value,
                        style = LegadoTheme.typography.titleMediumEmphasized,
                        maxLines = 1,
                        modifier = Modifier
                            .alignBy(FirstBaseline)
                            .basicMarquee(),
                    )
                    AppText(
                        text = unit,
                        style = LegadoTheme.typography.bodySmall,
                        color = LegadoTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .alignBy(FirstBaseline)
                            .padding(start = 2.dp),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun RecentBookCard(
    book: HomeRecentBookUi?,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = if (book?.bookUrl != null) onClick else null,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            book?.chapterProgress?.let { progress ->
                RecentReadingProgress(
                    progress = progress,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                )
            }

            Column(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 14.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    AppIcon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = LegadoTheme.colorScheme.primary,
                    )
                    AppText(
                        text = stringResource(R.string.home_recent_reading),
                        style = LegadoTheme.typography.titleMediumEmphasized,
                    )
                }

                if (book == null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        AppText(
                            text = stringResource(R.string.home_no_recent_reading),
                            color = LegadoTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BookshelfCover(
                            name = book.name,
                            author = book.author,
                            path = book.coverPath,
                            sourceOrigin = book.origin,
                            modifier = Modifier
                                .width(72.dp)
                                .aspectRatio(5f / 7f)
                                .clip(RoundedCornerShape(10.dp)),
                            coverModifier = Modifier.fillMaxSize(),
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            sharedCoverKey = book.bookUrl?.let(::bookCoverSharedElementKey),
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            AppText(
                                text = book.name,
                                style = LegadoTheme.typography.titleMediumEmphasized,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            AppText(
                                text = book.author,
                                style = LegadoTheme.typography.bodySmall,
                                color = LegadoTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            val chapterTitle = book.chapterTitle?.takeIf { it.isNotBlank() }
                            if (chapterTitle != null || book.chapterProgress != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    chapterTitle?.let { chapter ->
                                        AppText(
                                            text = chapter,
                                            modifier = Modifier
                                                .weight(1f)
                                                .basicMarquee(),
                                            style = LegadoTheme.typography.bodySmall,
                                            color = LegadoTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            softWrap = false,
                                            overflow = TextOverflow.Clip,
                                        )
                                    } ?: Spacer(modifier = Modifier.weight(1f))
                                    book.chapterProgress?.let { progress ->
                                        val percent = remember(progress) {
                                            (progress * 100f)
                                                .roundToInt()
                                                .coerceIn(0, 100)
                                        }
                                        TextCard(
                                            text = "$percent%",
                                            backgroundColor = LegadoTheme.colorScheme.secondaryContainer,
                                            contentColor = LegadoTheme.colorScheme.onSecondaryContainer,
                                            cornerRadius = 6.dp,
                                            horizontalPadding = 6.dp,
                                            verticalPadding = 2.dp,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentReadingProgress(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        label = "RecentReadingProgress",
    )
    val progressColor = LegadoTheme.colorScheme.primary
    val trackColor = LegadoTheme.colorScheme.onSurface.copy(alpha = 0.10f)

    Canvas(
        modifier = modifier.height(48.dp),
    ) {
        val progressWidth = size.width * animatedProgress
        val barHeight = 3.dp.toPx()
        val glowHeight = size.height - barHeight
        val stripHeight = 1.dp.toPx().coerceAtLeast(1f)
        val maxDiffusion = 48.dp.toPx()

        if (progressWidth > 0f) {
            var y = 0f
            while (y < glowHeight) {
                val distanceFromTop = (y / glowHeight).coerceIn(0f, 1f)
                val alpha = 0.18f * distanceFromTop * distanceFromTop
                val diffusion = maxDiffusion * (1f - distanceFromTop)
                val fadeStart = (progressWidth - diffusion * 0.15f)
                    .coerceAtLeast(0f)
                val fadeEnd = (progressWidth + diffusion)
                    .coerceAtMost(size.width)
                val height = stripHeight.coerceAtMost(glowHeight - y)

                if (alpha > 0.001f && fadeEnd > fadeStart) {
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                progressColor.copy(alpha = alpha),
                                progressColor.copy(alpha = 0f),
                            ),
                            startX = fadeStart,
                            endX = fadeEnd,
                        ),
                        topLeft = Offset(0f, y),
                        size = Size(fadeEnd, height),
                    )
                }
                y += stripHeight
            }
        }
        drawRect(
            color = trackColor,
            topLeft = Offset(0f, size.height - barHeight),
            size = Size(size.width, barHeight),
        )
        drawRect(
            color = progressColor,
            topLeft = Offset(0f, size.height - barHeight),
            size = Size(progressWidth, barHeight),
        )
    }
}

@Composable
private fun RecentBooksRow(
    books: List<HomeRecentBookUi>,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(books, key = { index, book -> "${book.bookUrl}_$index" }) { index, book ->
            RecentHistoryBookCard(
                book = book,
                onClick = { book.bookUrl?.let(onBookClick) },
            )
        }
    }
}

@Composable
private fun RecentHistoryBookCard(
    book: HomeRecentBookUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(60.dp)
            .aspectRatio(5f / 7f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                role = Role.Button,
                onClick = onClick,
            )
            .semantics {
                contentDescription = bookAccessibilityLabel(book.name, book.author)
                role = Role.Button
            },
    ) {
        BookshelfCover(
            name = book.name,
            author = book.author,
            path = book.coverPath,
            sourceOrigin = book.origin,
            modifier = Modifier.fillMaxSize(),
            coverModifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun ReadingGoalCard(
    todayReadTimeMillis: Long,
    goalMinutes: Int,
    onClick: () -> Unit,
) {
    val todayMinutes = (todayReadTimeMillis / 60_000L).toInt()
    val progress = (todayReadTimeMillis / 60_000f / goalMinutes)
        .coerceIn(0f, 1f)

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AppIcon(
                    imageVector = Icons.Default.TrackChanges,
                    contentDescription = null,
                    tint = LegadoTheme.colorScheme.primary,
                )
                AppText(
                    text = stringResource(R.string.home_today_reading_goal),
                    style = LegadoTheme.typography.titleMediumEmphasized,
                )
                Spacer(modifier = Modifier.weight(1f))
                MediumTonalButton(
                    onClick = onClick,
                    icon = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.home_set_goal),
                )
            }

            SemiCircleProgress(
                progress = progress,
                modifier = Modifier
                    .width(240.dp)
                    .padding(vertical = 8.dp)
                    .aspectRatio(2f),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AppText(
                        text = stringResource(
                            R.string.home_today_goal_value,
                            todayMinutes,
                            goalMinutes,
                        ),
                        style = LegadoTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    AppText(
                        text = stringResource(R.string.home_minutes),
                        style = LegadoTheme.typography.bodySmall,
                        color = LegadoTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun SemiCircleProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "DailyReadingProgress",
    )
    val trackColor = LegadoTheme.colorScheme.surfaceContainerHighest
    val progressColor = LegadoTheme.colorScheme.primary

    Box(
        // 装饰性进度环不暴露 progressBarRangeInfo：裸的进度语义会在 TalkBack 里
        // 形成一个朗读“进度条”却无任何交互的空焦点；进度已由环内文本表达，
        // 合并成一个节点即可。
        modifier = modifier.semantics(mergeDescendants = true) {},
        contentAlignment = Alignment.BottomCenter,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val arcSize = size.width - strokeWidth
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
            val boundingSize = Size(arcSize, arcSize)
            drawArc(
                color = trackColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = topLeft,
                size = boundingSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
            drawArc(
                color = progressColor,
                startAngle = 180f,
                sweepAngle = 180f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = boundingSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }
        content()
    }
}

@Composable
private fun WebDavBackupCard(
    latestBackup: HomeBackupUi?,
    isLoading: Boolean,
    isLoadError: Boolean,
    isActionRunning: Boolean,
    onBackup: () -> Unit,
    onRestore: () -> Unit,
    onOpenSettings: () -> Unit,
    onRetry: () -> Unit,
) {
    val lastBackupText = when {
        isLoading -> stringResource(R.string.home_loading_webdav_backup)
        latestBackup != null -> {
            val date = remember(latestBackup.lastModify) {
                DateFormat.getDateTimeInstance(
                    DateFormat.MEDIUM,
                    DateFormat.SHORT,
                ).format(Date(latestBackup.lastModify))
            }
            stringResource(R.string.home_latest_backup_value, date)
        }

        isLoadError -> stringResource(R.string.home_webdav_backup_load_error)
        else -> stringResource(R.string.home_no_webdav_backup)
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AppIcon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = null,
                    tint = LegadoTheme.colorScheme.primary,
                    modifier = Modifier.offset(y = 1.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    AppText(
                        text = stringResource(R.string.home_webdav_backup),
                        style = LegadoTheme.typography.titleMediumEmphasized,
                    )
                    AppText(
                        text = lastBackupText,
                        style = LegadoTheme.typography.bodySmall,
                        color = if (isLoadError) {
                            LegadoTheme.colorScheme.error
                        } else {
                            LegadoTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
                if (isLoading || isActionRunning) {
                    AppContainedLoadingIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            if (isLoadError && !isLoading) {
                SmallTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onRetry,
                    icon = Icons.Default.Refresh,
                    text = stringResource(R.string.retry),
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SmallTonalButton(
                        modifier = Modifier.weight(1f),
                        onClick = onBackup,
                        enabled = !isActionRunning,
                        icon = Icons.Default.CloudUpload,
                        text = stringResource(R.string.backup),
                    )
                    SmallTonalButton(
                        modifier = Modifier.weight(1f),
                        onClick = onRestore,
                        enabled = !isActionRunning,
                        icon = Icons.Default.CloudDownload,
                        text = stringResource(R.string.restore),
                    )
                    SmallTonalButton(
                        modifier = Modifier.weight(1f),
                        onClick = onOpenSettings,
                        enabled = !isActionRunning,
                        icon = Icons.Default.Settings,
                        text = stringResource(R.string.setting),
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeDashboardSettingsSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    sections: List<HomeDashboardSection>,
    homepageModulesEnabled: Boolean,
    carouselAutoPlay: Boolean,
    onSectionVisibilityChanged: (HomeDashboardSection, Boolean) -> Unit,
    onSectionMove: (HomeDashboardSection, Int) -> Unit,
    onHomepageModulesEnabledChanged: (Boolean) -> Unit,
    onCarouselAutoPlayChanged: (Boolean) -> Unit,
) {
    if (!show) return
    // 开启的组件按当前顺序排在前面，关闭的组件按默认顺序排在后面。
    val orderedSections = remember(sections) {
        sections + HomeDashboardSection.entries.filterNot { it in sections }
    }
    AppModalBottomSheet(
        show = show,
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.home_dashboard_settings),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            orderedSections.forEach { section ->
                val visible = section in sections
                val index = sections.indexOf(section)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        TinySwitchSettingItem(
                            title = stringResource(section.labelRes()),
                            checked = visible,
                            onCheckedChange = { checked ->
                                onSectionVisibilityChanged(section, checked)
                            },
                        )
                    }
                    if (visible) {
                        IconButton(
                            onClick = { onSectionMove(section, -1) },
                            enabled = index > 0,
                        ) {
                            AppIcon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = stringResource(R.string.a11y_move_up),
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        IconButton(
                            onClick = { onSectionMove(section, 1) },
                            enabled = index in 0 until sections.lastIndex,
                        ) {
                            AppIcon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = stringResource(R.string.a11y_move_down),
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
                // 随机推荐开着时给出自动轮播开关，关掉后只保留手动横滑。
                if (visible && section == HomeDashboardSection.BookCarousel) {
                    TinySwitchSettingItem(
                        title = stringResource(R.string.home_carousel_autoplay),
                        checked = carouselAutoPlay,
                        onCheckedChange = onCarouselAutoPlayChanged,
                    )
                }
            }
            // 首页模块是独立的整屏区块，固定在组件区之后，所以只有开关、没有排序箭头。
            TinySwitchSettingItem(
                title = stringResource(R.string.home_dashboard_homepage_modules),
                checked = homepageModulesEnabled,
                onCheckedChange = onHomepageModulesEnabledChanged,
            )
        }
    }
}

@Composable
private fun HomeDialogs(
    dialog: HomeDialog?,
    onIntent: (HomeIntent) -> Unit,
) {
    val goalDialog = dialog as? HomeDialog.SetReadingGoal
    var goalInput by remember(goalDialog?.currentMinutes) {
        mutableStateOf(goalDialog?.currentMinutes?.toString().orEmpty())
    }
    val goalMinutes = goalInput.toIntOrNull()
    val isGoalValid = goalMinutes != null &&
            goalMinutes in 1..MAX_DAILY_READING_GOAL_MINUTES

    AppAlertDialog(
        show = goalDialog != null,
        onDismissRequest = { onIntent(HomeIntent.DismissDialog) },
        title = stringResource(R.string.home_set_reading_goal),
        content = {
            OutlinedTextField(
                value = goalInput,
                onValueChange = { value ->
                    goalInput = value.filter(Char::isDigit)
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    AppText(stringResource(R.string.home_goal_minutes_label))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        },
        confirmText = stringResource(R.string.ok),
        onConfirm = if (isGoalValid) {
            {
                onIntent(HomeIntent.UpdateReadingGoal(goalMinutes))
            }
        } else {
            null
        },
        dismissText = stringResource(R.string.cancel),
        onDismiss = { onIntent(HomeIntent.DismissDialog) },
    )

    val restoreDialog = dialog as? HomeDialog.ConfirmRestore
    AppAlertDialog(
        show = restoreDialog != null,
        onDismissRequest = { onIntent(HomeIntent.DismissDialog) },
        title = stringResource(R.string.restore_confirmation),
        text = restoreDialog?.let {
            stringResource(R.string.home_webdav_restore_confirmation, it.backupName)
        },
        confirmText = stringResource(R.string.ok),
        onConfirm = { onIntent(HomeIntent.ConfirmRestore) },
        dismissText = stringResource(R.string.cancel),
        onDismiss = { onIntent(HomeIntent.DismissDialog) },
    )
}

@Composable
private fun HomeSheets(
    sheet: HomeSheet?,
    sections: List<HomeDashboardSection>,
    homepageModulesEnabled: Boolean,
    carouselAutoPlay: Boolean,
    onIntent: (HomeIntent) -> Unit,
) {
    HomeDashboardSettingsSheet(
        show = sheet is HomeSheet.DashboardSettings,
        onDismissRequest = { onIntent(HomeIntent.DismissSheet) },
        sections = sections,
        homepageModulesEnabled = homepageModulesEnabled,
        carouselAutoPlay = carouselAutoPlay,
        onSectionVisibilityChanged = { section, visible ->
            onIntent(HomeIntent.SetSectionVisible(section, visible))
        },
        onSectionMove = { section, offset ->
            onIntent(HomeIntent.MoveSection(section, offset))
        },
        onHomepageModulesEnabledChanged = { enabled ->
            onIntent(HomeIntent.SetHomepageModulesEnabled(enabled))
        },
        onCarouselAutoPlayChanged = { enabled ->
            onIntent(HomeIntent.SetCarouselAutoPlay(enabled))
        },
    )
    BackupOptionSheet(
        show = sheet is HomeSheet.BackupOptions,
        onDismissRequest = { onIntent(HomeIntent.DismissSheet) },
        onBackupToLocal = {
            onIntent(
                HomeIntent.BackupDestinationSelected(HomeBackupDestination.Local)
            )
        },
        onBackupToNetwork = {
            onIntent(
                HomeIntent.BackupDestinationSelected(HomeBackupDestination.WebDav)
            )
        },
        onBackupToLocalAndNetwork = {
            onIntent(
                HomeIntent.BackupDestinationSelected(HomeBackupDestination.LocalAndWebDav)
            )
        },
    )
    RestoreOptionSheet(
        show = sheet is HomeSheet.RestoreOptions,
        onDismissRequest = { onIntent(HomeIntent.DismissSheet) },
        onRestoreFromLocal = { onIntent(HomeIntent.RestoreFromLocal) },
        onRestoreFromNetwork = { onIntent(HomeIntent.RestoreFromNetwork) },
    )
}

@Composable
private fun AppModalBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    title: String? = null,
    content: @Composable () -> Unit,
) {
    io.legado.app.ui.widget.components.modalBottomSheet.AppModalBottomSheet(
        show = show,
        onDismissRequest = onDismissRequest,
        title = title,
    ) {
        content()
    }
}

@StringRes
private fun HomeDashboardSection.labelRes(): Int = when (this) {
    HomeDashboardSection.RecentBook -> R.string.home_recent_reading
    HomeDashboardSection.BookCarousel -> R.string.home_random_recommend
    HomeDashboardSection.TotalReadBooks -> R.string.home_total_read_books
    HomeDashboardSection.TotalReadTime -> R.string.home_total_reading_time
    HomeDashboardSection.RecentBooks -> R.string.home_recent_books
    HomeDashboardSection.DailyGoal -> R.string.home_today_reading_goal
    HomeDashboardSection.WebDavBackup -> R.string.home_webdav_backup
}

private fun bookAccessibilityLabel(
    name: String,
    author: String,
): String {
    return if (author.isBlank()) name else "$name, $author"
}
