package io.legado.app.ui.main.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ui.feice.mochi.basic.MochiPageControl
import ui.feice.mochi.shape.MochiRoundedRectangle
import kotlin.math.abs

/**
 * 首页随机推荐轮播的本地实现。
 *
 * 与 Mochi 自带的 MochiCarousel 相比有两处行为差异：
 * 1. 卡片缩放按「连续页位置」计算，currentPage 跨过 0.5 时不会从 1.0 突然跳到 0.5；
 *    后者正是滑动结束时会看到一次奇怪回弹/缩放的主因。
 * 2. 翻页 fling 允许一次跨 2 页、吸附加载弹簧无回弹，快速轻扫更容易到下一张，
 *    不再需要把卡片拖过大半屏才触发翻页。
 */
@Composable
internal fun HomeMochiCarousel(
    pageCount: Int,
    height: Dp,
    state: PagerState,
    modifier: Modifier = Modifier,
    peek: Dp = 24.dp,
    spacing: Dp = 10.dp,
    cornerRadius: Dp = 28.dp,
    showPageControl: Boolean = true,
    content: @Composable (page: Int) -> Unit,
) {
    val shape: Shape = remember(cornerRadius) { MochiRoundedRectangle(cornerRadius) }
    val flingBehavior = PagerDefaults.flingBehavior(
        state = state,
        pagerSnapDistance = PagerSnapDistance.atMost(2),
        snapAnimationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
    )
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HorizontalPager(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            contentPadding = PaddingValues(horizontal = peek + spacing),
            pageSpacing = spacing,
            flingBehavior = flingBehavior,
        ) { page ->
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val position = state.currentPage + state.currentPageOffsetFraction
                        val offset = abs(position - page).coerceIn(0f, 1f)
                        val scale = 1f + (0.96f - 1f) * offset
                        scaleX = scale
                        scaleY = scale
                        alpha = 1f + (0.80f - 1f) * offset
                        clip = true
                        this.shape = shape
                    },
            ) {
                content(page)
            }
        }
        if (showPageControl && pageCount > 1) {
            MochiPageControl(state)
        }
    }
}
