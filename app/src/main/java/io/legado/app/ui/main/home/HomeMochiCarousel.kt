package io.legado.app.ui.main.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.legado.app.R
import ui.feice.mochi.basic.MochiPageControl
import ui.feice.mochi.shape.MochiRoundedRectangle
import kotlin.math.abs
import kotlin.math.roundToInt

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
    /** 左右露出的相邻卡片点击回调；为 null 时保持普通 Pager 行为。 */
    onAdjacentClick: ((page: Int) -> Unit)? = null,
    content: @Composable (page: Int) -> Unit,
) {
    val shape: Shape = remember(cornerRadius) { MochiRoundedRectangle(cornerRadius) }
    val flingBehavior = PagerDefaults.flingBehavior(
        state = state,
        pagerSnapDistance = remember {
            // 默认的 atMost 只是「上限」，轻扫仍经常只走 1 页。这里按速度和自然
            // 吸附距离取较大值：任何带速度的轻扫至少过 1 页，快速滑动最多 3 页。
            object : PagerSnapDistance {
                override fun calculateTargetPage(
                    startPage: Int,
                    suggestedTargetPage: Int,
                    velocity: Float,
                    pageSize: Int,
                    pageSpacing: Int,
                ): Int {
                    if (abs(velocity) < MIN_FLING_VELOCITY_PX_PER_SEC) {
                        return suggestedTargetPage
                    }
                    val stride = (pageSize + pageSpacing).coerceAtLeast(1)
                    val byVelocity = (abs(velocity) / stride.toFloat() * 1.15f)
                        .roundToInt()
                        .coerceIn(1, MAX_FLING_PAGES)
                    val suggested = abs(suggestedTargetPage - startPage)
                    val direction = when {
                        suggestedTargetPage > startPage -> 1
                        suggestedTargetPage < startPage -> -1
                        velocity > 0f -> 1
                        else -> -1
                    }
                    return startPage + direction *
                        maxOf(suggested, byVelocity).coerceAtMost(MAX_FLING_PAGES)
                }
            }
        },
        // 拖过 1/5 页就吸附到下一页，而不是必须拖过半屏。
        snapPositionalThreshold = 0.2f,
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
        Box(modifier = Modifier.fillMaxWidth()) {
            HorizontalPager(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height),
                contentPadding = PaddingValues(horizontal = peek + spacing),
                pageSpacing = spacing,
                // 多页翻动时预组合前后页，避免中间页晚一拍才出现造成「突跳」。
                beyondViewportPageCount = 2,
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
            if (onAdjacentClick != null && pageCount > 1) {
                // 左右露出的 24dp 属于相邻页，但 Pager 的手势层会吞掉点击。
                // 在这两条窄区上盖显式点击目标，直接打开相邻书详情，不再切页。
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .width(peek)
                        .fillMaxHeight(0.72f)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = stringResource(R.string.home_carousel_previous),
                            onClick = { onAdjacentClick(state.currentPage - 1) },
                        ),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(peek)
                        .fillMaxHeight(0.72f)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = stringResource(R.string.home_carousel_next),
                            onClick = { onAdjacentClick(state.currentPage + 1) },
                        ),
                )
            }
        }
        if (showPageControl && pageCount > 1) {
            MochiPageControl(state)
        }
    }
}

private const val MIN_FLING_VELOCITY_PX_PER_SEC = 120f
private const val MAX_FLING_PAGES = 3
