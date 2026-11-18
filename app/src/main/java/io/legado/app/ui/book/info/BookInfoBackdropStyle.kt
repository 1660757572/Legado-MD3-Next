package io.legado.app.ui.book.info

internal data class BookInfoBackdropStyle(
    val showCover: Boolean,
    val blurCover: Boolean,
    val applySeedOverlay: Boolean,
)

/**
 * @param overlayEnabled 外观设置里的「背景颜色遮罩」开关；关闭后只保留封面到页面底色的渐变，
 * 不再叠加取色着色层，清单里返回的三套样式都会失去 seed overlay。
 */
internal fun resolveBookInfoBackdropStyle(
    backgroundMode: String,
    overlayEnabled: Boolean = true,
): BookInfoBackdropStyle {
    // 取值与主题设置持久化的 bookInfoBackground 一致：off/ off_for_default / on
    return when (backgroundMode) {
        "off" -> BookInfoBackdropStyle(
            showCover = true,
            blurCover = false,
            applySeedOverlay = overlayEnabled,
        )

        "off_for_default" -> BookInfoBackdropStyle(
            showCover = false,
            blurCover = false,
            applySeedOverlay = false,
        )

        else -> BookInfoBackdropStyle(
            showCover = true,
            blurCover = true,
            applySeedOverlay = overlayEnabled,
        )
    }
}
