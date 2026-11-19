package io.legado.app.ui.widget.components.topbar

/**
 * 折叠态（compact）标题的水平位置。
 *
 * - [Center]：在整条顶栏里居中，左右按较大的一侧（返回键 / 操作按钮）对称留白，视觉上不偏。
 * - [Start]：和展开态的大标题左对齐（发现页用，保持模块列表的左边距与原设计一致）。
 */
enum class TopBarTitleAlignment {
    Center,
    Start,
}
