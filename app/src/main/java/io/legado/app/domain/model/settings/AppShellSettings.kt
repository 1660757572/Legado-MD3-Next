package io.legado.app.domain.model.settings

data class AppShellSettings(
    val themeMode: String = "0",
    val fontScale: Int = 10,
    val composeEngine: String = "material",
    val showHome: Boolean = true,
    val showDiscovery: Boolean = true,
    val showRss: Boolean = true,
    val showStatusBar: Boolean = true,
    val showNavigationBar: Boolean = true,
    val swipeAnimation: Boolean = true,
    val predictiveBackEnabled: Boolean = true,
    val readerSharedElementEnabled: Boolean = true,
    val showBottomView: Boolean = true,
    val useFloatingBottomBar: Boolean = false,
    /** Mochi 导航栏右侧的最近阅读附件入口。 */
    val mochiNavAccessory: Boolean = false,
    val useFloatingBottomBarLiquidGlass: Boolean = false,
    /** 是否允许在主页各页面之间左右滑动切换 */
    val pagerSwipeEnabled: Boolean = true,
    val tabletInterface: String = "auto",
    val multiPaneMode: String = "auto",
    val labelVisibilityMode: String = "auto",
    val defaultHomePage: String = "bookshelf",
    val mainNavigationOrder: String = "home,bookshelf,explore,rss,my",
    val navExtended: Boolean = false,
    val navIconHome: String = "",
    val navIconBookshelf: String = "",
    val navIconExplore: String = "",
    val navIconRss: String = "",
    val navIconMy: String = "",
    val navIconHomeSelected: String = "",
    val navIconBookshelfSelected: String = "",
    val navIconExploreSelected: String = "",
    val navIconRssSelected: String = "",
    val navIconMySelected: String = "",
    val launcherIcon: String = "ic_launcher",
    val navigationAnimationStyle: NavigationAnimationStyle = NavigationAnimationStyle.Default,
    val textAnimationLevel: TextAnimationLevel = TextAnimationLevel.Slide,
    /** Mochi 分类 chip 之间以及内容左右的间距，单位 dp。 */
    val categoryChipSpacing: Int = 12,
    /** Mochi 分类 chip 的整体尺寸百分比；80–120，默认 100。 */
    val categoryChipScale: Int = 100,
)

enum class TextAnimationLevel(val storedValue: String) {
    Slide("slide"),
    Off("off"),
    Subtle("subtle"),
    Standard("standard"),
    Strong("strong");

    companion object {
        fun fromStoredValue(value: String?): TextAnimationLevel =
            entries.firstOrNull { it.storedValue == value } ?: Slide
    }
}

enum class NavigationAnimationStyle(val storedValue: String) {
    Default("default"),
    Miuix("miuix"),
    Scale("scale"),

    /** Mochi 风格的预测性返回：退场页缩小 + 圆角收起，配合玻璃投影。 */
    Mochi("mochi_predictive"),

    /** 跟随系统的预测性返回观感（无缩放/圆角，滑入滑出为主）。 */
    System("system_predictive");

    companion object {
        fun fromStoredValue(value: String?): NavigationAnimationStyle = when (value) {
            "mochi" -> Miuix
            "aosp" -> Scale
            else -> entries.firstOrNull { it.storedValue == value } ?: Default
        }
    }
}
