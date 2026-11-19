package io.legado.app.ui.widget.components.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RssFeed
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.itos.coui.icons.CouiIcons
import io.legado.app.ui.main.MainDestination
import io.legado.app.ui.theme.ComposeEngine
import io.legado.app.ui.theme.LegadoTheme
import top.yukonga.miuix.kmp.icon.MiuixIcons
import ui.feice.mochi.icons.MochiIconStyle
import ui.feice.mochi.icons.rememberMochiIonicon
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.icon.extended.Album
import top.yukonga.miuix.kmp.icon.extended.ChevronBackward
import top.yukonga.miuix.kmp.icon.extended.Close
import top.yukonga.miuix.kmp.icon.extended.ContactsBook
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.Edit
import top.yukonga.miuix.kmp.icon.extended.Favorites
import top.yukonga.miuix.kmp.icon.extended.Filter
import top.yukonga.miuix.kmp.icon.extended.Info
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.icon.extended.MoreCircle
import top.yukonga.miuix.kmp.icon.extended.Notes
import top.yukonga.miuix.kmp.icon.extended.Pin
import top.yukonga.miuix.kmp.icon.extended.Refresh
import top.yukonga.miuix.kmp.icon.extended.Search
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.icon.extended.Unpin
import top.yukonga.miuix.kmp.icon.extended.WorldClock

/**
 * 四大引擎（Material / Miuix / COUI / Mochi）的图标出口。
 *
 * Mochi 使用自带的 Ionicons（filled）替换常用 Material 图标；映射表覆盖底部导航、
 * 顶栏动作、设置页和「我的」页的常用语义，表中没有的图标继续退回 Material，
 * 保证调用方不需要逐处判断引擎。
 */
object AppIcons {

    private val engine: ComposeEngine
        @Composable
        get() = LegadoTheme.composeEngine

    /** Mochi filled 图标；名字不存在时回退到调用方给的 Material 图标。 */
    @Composable
    fun mochi(name: String, fallback: ImageVector): ImageVector =
        rememberMochiIonicon(name, MochiIconStyle.Filled) ?: fallback

    /** 只按 Material 图标名做一次语义映射，避免每个调用点都传两套图标。 */
    private fun mochiNameFor(icon: ImageVector): String? =
        mochiNameByMaterial[icon.name.substringAfterLast('.')]

    /** 通用入口：Mochi 主题下按 Material 图标名换成 filled Ionicons，其他引擎返回 null。 */
    @Composable
    fun mochiOrNull(icon: ImageVector): ImageVector? =
        if (engine == ComposeEngine.Mochi) {
            mochiNameFor(icon)?.let { name -> mochi(name, icon) }
        } else {
            null
        }

    /**
     * 行首图标（菜单项、设置项）。
     *
     * COUI 的原生菜单与设置列表不带行首图标，与 Miuix 设置项（`ArrowPreference` /
     * `SwitchPreference` 不接收图标）保持同一取向，因此 Coui 返回 null 由组件跳过整块图标；
     * Material/Mochi 与 Miuix 菜单（仍要保留图标）原样返回。
     */
    @Composable
    fun leadingOrNull(icon: ImageVector): ImageVector? = when (engine) {
        ComposeEngine.Coui -> null
        ComposeEngine.Mochi -> mochiNameFor(icon)
            ?.let { rememberMochiIonicon(it, MochiIconStyle.Filled) }
            ?: icon

        else -> icon
    }


    val Search: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Miuix -> MiuixIcons.Search
            ComposeEngine.Coui -> CouiIcons.Outlined.Search
            ComposeEngine.Mochi -> mochi("search", Icons.Default.Search)
            else -> Icons.Default.Search
        }

    val MoreVert: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Miuix -> MiuixIcons.More
            ComposeEngine.Coui -> CouiIcons.Filled.More
            ComposeEngine.Mochi -> mochi("ellipsis-vertical", Icons.Default.MoreVert)
            else -> Icons.Default.MoreVert
        }

    // COUI 没有编辑图标，与 Material/Mochi 共用 Material 图标
    val Edit: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Miuix -> MiuixIcons.Edit
            ComposeEngine.Mochi -> mochi("pencil", Icons.Default.Edit)
            else -> Icons.Default.Edit
        }

    // COUI 没有删除图标
    val Delete: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Miuix -> MiuixIcons.Delete
            ComposeEngine.Mochi -> mochi("trash", Icons.Default.Delete)
            else -> Icons.Default.Delete
        }

    val Close: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Miuix -> MiuixIcons.Close
            ComposeEngine.Coui -> CouiIcons.Filled.Close
            ComposeEngine.Mochi -> mochi("close", Icons.Default.Clear)
            else -> Icons.Default.Clear
        }

    val Back: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Miuix -> MiuixIcons.ChevronBackward
            ComposeEngine.Coui -> CouiIcons.Filled.Back
            ComposeEngine.Mochi -> mochi("chevron-back", Icons.AutoMirrored.Filled.ArrowBack)
            else -> Icons.AutoMirrored.Filled.ArrowBack
        }

    val Filter: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Miuix -> MiuixIcons.Filter
            ComposeEngine.Coui -> CouiIcons.Outlined.ListFilter
            ComposeEngine.Mochi -> mochi("filter", Icons.Outlined.FilterAlt)
            else -> Icons.Outlined.FilterAlt
        }

    val Settings: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Miuix -> MiuixIcons.Settings
            ComposeEngine.Coui -> CouiIcons.Outlined.Setting
            ComposeEngine.Mochi -> mochi("settings", Icons.Default.Settings)
            else -> Icons.Default.Settings
        }

    // COUI 没有缺陷报告图标
    val BugReport: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Mochi -> mochi("bug", Icons.Default.BugReport)
            else -> Icons.Default.BugReport
        }

    // 精确/非精确搜索是一对开关图标，COUI 只有定位图钉、没有对应的「未定位」图形，
    // 两态都保持 Material 图标，避免切换时视觉不成对
    val PrecisionSearch: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Miuix -> MiuixIcons.Pin
            ComposeEngine.Mochi -> mochi("locate", Icons.Default.MyLocation)
            else -> Icons.Default.MyLocation
        }

    val UnPrecisionSearch: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Miuix -> MiuixIcons.Unpin
            ComposeEngine.Mochi -> mochi("search", Icons.Default.LocationSearching)
            else -> Icons.Default.LocationSearching
        }

    // COUI 没有时钟类图标，历史记录保持 Material 图标
    val History: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Miuix -> MiuixIcons.WorldClock
            ComposeEngine.Mochi -> mochi("time", Icons.Default.History)
            else -> Icons.Default.History
        }

    // COUI 的 Next 是前进箭头而非刷新，重试保持 Material 图标
    val Replay: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Miuix -> MiuixIcons.Refresh
            ComposeEngine.Mochi -> mochi("refresh", Icons.Default.Replay)
            else -> Icons.Default.Replay
        }

    val MoreCircle: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Miuix -> MiuixIcons.MoreCircle
            ComposeEngine.Coui -> CouiIcons.Filled.More
            ComposeEngine.Mochi -> mochi("ellipsis-horizontal-circle", Icons.Default.MoreHoriz)
            else -> Icons.Default.MoreHoriz
        }

    // COUI 没有勾选图标
    val Check: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Mochi -> mochi("checkmark", Icons.Default.Check)
            else -> Icons.Default.Check
        }

    val Add: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Miuix -> MiuixIcons.Add
            ComposeEngine.Coui -> CouiIcons.Filled.Add
            ComposeEngine.Mochi -> mochi("add", Icons.Default.Add)
            else -> Icons.Default.Add
        }

    // COUI 没有信息图标（SettingDetail 是设置齿轮，不能替代）
    val Info: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Miuix -> MiuixIcons.Info
            ComposeEngine.Mochi -> mochi("information-circle", Icons.Outlined.Info)
            else -> Icons.Outlined.Info
        }

    val Group: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Miuix -> MiuixIcons.Filter
            ComposeEngine.Coui -> CouiIcons.Filled.Users
            ComposeEngine.Mochi -> mochi("pricetags", Icons.Outlined.Sell)
            else -> Icons.Outlined.Sell
        }

    // ————————————— 播放器专用：Miuix/COUI 没有对应图标，各引擎共用 Material 图标 —————————————

    val Previous: ImageVector
        @Composable
        get() = Icons.Default.SkipPrevious

    val Next: ImageVector
        @Composable
        get() = Icons.Default.SkipNext

    val Speed: ImageVector
        @Composable
        get() = Icons.Default.Speed

    val Episodes: ImageVector
        @Composable
        get() = Icons.AutoMirrored.Filled.List

    val Fullscreen: ImageVector
        @Composable
        get() = Icons.Default.Fullscreen

    val FullscreenExit: ImageVector
        @Composable
        get() = Icons.Default.FullscreenExit

    val PictureInPicture: ImageVector
        @Composable
        get() = Icons.Default.PictureInPictureAlt

    val MiniWindow: ImageVector
        @Composable
        get() = Icons.Default.OpenInNew

    val ExpandMore: ImageVector
        @Composable
        get() = when (engine) {
            ComposeEngine.Mochi -> mochi("chevron-down", Icons.Default.KeyboardArrowDown)
            else -> Icons.Default.KeyboardArrowDown
        }

    @Composable
    fun mainDestination(destination: MainDestination, selected: Boolean): ImageVector {
        return when (engine) {
            ComposeEngine.Miuix -> when (destination) {
                MainDestination.Home -> MiuixIcons.Regular.ContactsBook
                MainDestination.Bookshelf -> MiuixIcons.Regular.Notes
                MainDestination.Explore -> MiuixIcons.Regular.Album
                MainDestination.Rss -> MiuixIcons.Regular.Favorites
                MainDestination.My -> MiuixIcons.Regular.Settings
            }

            ComposeEngine.Coui -> when (destination) {
                MainDestination.Home -> CouiIcons.Filled.Grid
                MainDestination.Bookshelf -> CouiIcons.Filled.BookOpen
                MainDestination.Explore ->
                    if (selected) CouiIcons.Filled.WandSpark else CouiIcons.Outlined.WandSpark

                MainDestination.Rss ->
                    if (selected) CouiIcons.Filled.CloudLink else CouiIcons.Outlined.CloudLink

                MainDestination.My -> CouiIcons.Filled.UserCircle
            }

            ComposeEngine.Mochi -> when (destination) {
                MainDestination.Home -> mochi("home", Icons.Default.Home)
                MainDestination.Bookshelf -> mochi("library", Icons.AutoMirrored.Filled.LibraryBooks)
                MainDestination.Explore -> mochi("compass", Icons.Default.Explore)
                MainDestination.Rss -> mochi("newspaper", Icons.Default.RssFeed)
                MainDestination.My -> mochi("person", Icons.Default.Person)
            }

            else -> when (destination) {
                MainDestination.Home -> if (selected) Icons.Default.Home else Icons.Outlined.Home
                MainDestination.Bookshelf -> if (selected) {
                    Icons.AutoMirrored.Filled.LibraryBooks
                } else {
                    Icons.AutoMirrored.Outlined.LibraryBooks
                }

                MainDestination.Explore -> if (selected) Icons.Default.Explore else Icons.Outlined.Explore
                MainDestination.Rss -> if (selected) Icons.Default.RssFeed else Icons.Outlined.RssFeed
                MainDestination.My -> if (selected) Icons.Default.Person else Icons.Outlined.Person
            }
        }
    }
}

/** Material 图标（去掉 Filled./Outlined. 前缀后的名字）到 Mochi Ionicons 名的语义映射。 */
private val mochiNameByMaterial: Map<String, String> = mapOf(
    "Search" to "search",
    "Settings" to "settings",
    "MoreVert" to "ellipsis-vertical",
    "MoreHoriz" to "ellipsis-horizontal-circle",
    "Edit" to "pencil",
    "Delete" to "trash",
    "Clear" to "close",
    "Close" to "close",
    "ArrowBack" to "chevron-back",
    "FilterAlt" to "filter",
    "BugReport" to "bug",
    "MyLocation" to "locate",
    "LocationSearching" to "search",
    "History" to "time",
    "Replay" to "refresh",
    "Check" to "checkmark",
    "Add" to "add",
    "Info" to "information-circle",
    "Sell" to "pricetag",
    "Label" to "pricetags",
    "KeyboardArrowDown" to "chevron-down",
    "Home" to "home",
    "LibraryBooks" to "library",
    "Explore" to "compass",
    "RssFeed" to "newspaper",
    "Person" to "person",
    "Source" to "server",
    "FindReplace" to "swap-horizontal",
    "Rule" to "document-text",
    "AutoAwesome" to "sparkles",
    "Bookmark" to "bookmark",
    "Download" to "download",
    "Folder" to "folder",
    "ExitToApp" to "log-out",
    "Web" to "globe",
    "ContentCopy" to "copy",
    "OpenInBrowser" to "open",
    "Palette" to "color-palette",
    "ColorLens" to "color-palette",
    "Tune" to "options",
    "Book" to "book",
    "Image" to "image",
    "ChatBubble" to "chatbubble",
    "CloudDownload" to "cloud-download",
    "CloudUpload" to "cloud-upload",
    "Backup" to "cloud-upload",
    "SmartToy" to "sparkles",
    "Language" to "language",
    "Security" to "shield-checkmark",
    "Science" to "flask",
    "Storage" to "server",
    // 常见 Material 名到 filled Ionicons 的补充映射，覆盖全局走 AppIcon 的按钮与菜单。
    "OpenInNew" to "open",
    "MenuBook" to "book",
    "AccountCircle" to "person-circle",
    "PersonOutline" to "person-circle",
    "Notifications" to "notifications",
    "Timer" to "time",
    "AccessTime" to "time",
    "Schedule" to "time",
    "Refresh" to "refresh",
    "Share" to "share-social",
    "ArrowForward" to "arrow-forward",
    "ArrowForwardIos" to "chevron-forward",
    "KeyboardArrowRight" to "chevron-forward",
    "KeyboardArrowLeft" to "chevron-back",
    "KeyboardArrowUp" to "chevron-up",
    "ExpandLess" to "chevron-up",
    "PlayArrow" to "play",
    "Pause" to "pause",
    "Star" to "star",
    "StarBorder" to "star",
    "Favorite" to "heart",
    "PushPin" to "pin",
    "Description" to "document-text",
    "Article" to "document-text",
    "List" to "list",
    "Sort" to "swap-vertical",
    "AddCircle" to "add-circle",
    "Remove" to "remove",
    "CheckCircle" to "checkmark-circle",
    "Warning" to "warning",
    "Error" to "alert-circle",
    "Lock" to "lock-closed",
    "Visibility" to "eye",
    "VisibilityOff" to "eye-off",
    "VolumeUp" to "volume-high",
    "Translate" to "language",
    "DarkMode" to "moon",
    "LightMode" to "sunny",
    "Cloud" to "cloud",
    "Email" to "mail",
    "Phone" to "call",
    "CalendarMonth" to "calendar",
    "Speed" to "speedometer",
)
