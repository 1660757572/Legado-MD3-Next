package io.legado.app.ui.theme

import ui.feice.mochi.basic.MochiToastState

/**
 * 把全局的 Toast 调用接到当前 Mochi 页面根上的 ToastHost。
 *
 * ToastUtils 是非 Compose 的集中入口，拿不到 CompositionLocal；这里只保存当前
 * [ProvideMochiTheme] 注册的 state，页面销毁时清掉，避免把提示发到已经离开的窗口。
 */
object MochiToastBridge {
    private var state: MochiToastState? = null

    fun attach(value: MochiToastState) {
        state = value
    }

    fun detach(value: MochiToastState) {
        if (state === value) state = null
    }

    /** @return true 表示已经交给 Mochi Toast 显示；false 时调用方应回退到系统 Toast。 */
    fun show(message: CharSequence, long: Boolean): Boolean {
        val host = state ?: return false
        host.show(
            title = message.toString(),
            durationMillis = if (long) 3_200L else 1_900L,
        )
        return true
    }
}
