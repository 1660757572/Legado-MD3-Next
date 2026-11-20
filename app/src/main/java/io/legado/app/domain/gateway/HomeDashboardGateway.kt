package io.legado.app.domain.gateway

import io.legado.app.domain.model.HomeDashboardSection
import io.legado.app.domain.model.HomeReadingBook
import kotlinx.coroutines.flow.Flow

interface HomeDashboardGateway {
    fun observeTotalReadBooks(): Flow<Int>

    fun observeTotalReadTime(): Flow<Long>

    fun observeReadTime(date: String): Flow<Long>

    fun observeRecentBooks(limit: Int): Flow<List<HomeReadingBook>>

    fun observeDailyGoal(defaultValue: Int): Flow<Int>

    fun observeSelectedSourceSetUrl(): Flow<String?>

    fun observeVisibleSections(): Flow<List<HomeDashboardSection>>

    /** 首页模块（发现页风格的模块流）是否参与首页布局；关闭后连同「首页模块管理」入口一起隐藏。 */
    fun observeHomepageModulesEnabled(): Flow<Boolean>

    /** 首页「随机推荐」是否自动轮播。 */
    fun observeCarouselAutoPlay(): Flow<Boolean>

    /** 首页「随机推荐」的卡片样式：0 = 经典横排，1 = Mochi 轮播。 */
    fun observeCarouselStyle(): Flow<Int>

    /** 首页「最近阅读」进度样式：0 = 横向进度条，1 = 圆环。 */
    fun observeRecentProgressStyle(): Flow<Int>

    suspend fun updateDailyGoal(minutes: Int)

    suspend fun updateSelectedSourceSetUrl(sourceUrl: String)

    suspend fun updateVisibleSections(sections: List<HomeDashboardSection>)

    suspend fun updateHomepageModulesEnabled(enabled: Boolean)

    suspend fun updateCarouselAutoPlay(enabled: Boolean)

    suspend fun updateCarouselStyle(style: Int)

    suspend fun updateRecentProgressStyle(style: Int)
}
