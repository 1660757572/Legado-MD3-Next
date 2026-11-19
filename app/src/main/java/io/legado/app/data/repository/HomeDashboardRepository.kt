package io.legado.app.data.repository

import io.legado.app.data.dao.ReadRecordDao
import io.legado.app.data.local.preferences.LocalPreferencesKeys
import io.legado.app.domain.gateway.HomeDashboardGateway
import io.legado.app.domain.model.DEFAULT_HOME_DASHBOARD_SECTION_ORDER
import io.legado.app.domain.model.HOMEPAGE_MODULES_DISABLED_TOKEN
import io.legado.app.domain.model.HomeDashboardSection
import io.legado.app.domain.model.HomeReadingBook
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class HomeDashboardRepository(
    private val readRecordDao: ReadRecordDao,
    private val localPreferencesRepository: SettingsRepository,
) : HomeDashboardGateway {

    override fun observeTotalReadBooks(): Flow<Int> =
        readRecordDao.observeTotalReadBookCount()

    override fun observeTotalReadTime(): Flow<Long> =
        readRecordDao.getTotalReadTime().map { it ?: 0L }

    override fun observeReadTime(date: String): Flow<Long> =
        readRecordDao.observeReadTimeByDate(date).map { it ?: 0L }

    override fun observeRecentBooks(limit: Int): Flow<List<HomeReadingBook>> =
        readRecordDao.observeRecentHomeBooks(limit).map { rows ->
            rows.map { row ->
                HomeReadingBook(
                    bookUrl = row.bookUrl,
                    name = row.recordName,
                    author = row.recordAuthor,
                    origin = row.origin,
                    coverPath = if (row.customCoverUrl.isNullOrEmpty()) {
                        row.coverUrl
                    } else {
                        row.customCoverUrl
                    },
                    chapterTitle = row.chapterTitle,
                    chapterProgress = if (
                        row.totalChapterNum != null &&
                        row.totalChapterNum > 0 &&
                        row.chapterIndex != null
                    ) {
                        (row.chapterIndex + 1)
                            .coerceIn(0, row.totalChapterNum)
                            .toFloat() / row.totalChapterNum
                    } else {
                        null
                    },
                )
            }
        }

    override fun observeDailyGoal(defaultValue: Int): Flow<Int> =
        localPreferencesRepository.getPreference(
            LocalPreferencesKeys.DAILY_READING_GOAL_MINUTES,
            defaultValue,
        )

    override fun observeSelectedSourceSetUrl(): Flow<String?> =
        localPreferencesRepository.getPreference(
            LocalPreferencesKeys.HOME_SOURCE_SET_URL,
            "",
        ).map { it.takeIf(String::isNotBlank) }

    /**
     * 首页组件的持久化串只读一次：组件顺序和「首页模块」开关都编在这一个值里。
     * 开关用尾部标记 [HOMEPAGE_MODULES_DISABLED_TOKEN] 表示关闭，[HomeDashboardSection.fromStorage]
     * 会忽略它，所以组件顺序不受影响。
     */
    private val dashboardSectionsPreference = localPreferencesRepository.getPreference(
        LocalPreferencesKeys.HOME_DASHBOARD_SECTIONS,
        HomeDashboardSection.toStorage(DEFAULT_HOME_DASHBOARD_SECTION_ORDER),
    )

    /** 最近一次读到的原始串，供两个写入口在「只改自己那部分」时合并。 */
    @Volatile
    private var latestSectionsRaw: String = HomeDashboardSection.toStorage(
        DEFAULT_HOME_DASHBOARD_SECTION_ORDER
    )

    override fun observeVisibleSections(): Flow<List<HomeDashboardSection>> =
        dashboardSectionsPreference
            .onEach { latestSectionsRaw = it }
            .map(HomeDashboardSection::fromStorage)

    override fun observeHomepageModulesEnabled(): Flow<Boolean> =
        dashboardSectionsPreference
            .onEach { latestSectionsRaw = it }
            // 串里带「关闭」标记 = 首页模块被关掉，因此这里要取反。
            .map { raw -> !hasHomepageModulesDisabledToken(raw) }

    override fun observeCarouselAutoPlay(): Flow<Boolean> =
        localPreferencesRepository.getBoolean(CAROUSEL_AUTOPLAY_KEY, true)

    override suspend fun updateCarouselAutoPlay(enabled: Boolean) {
        localPreferencesRepository.putBoolean(CAROUSEL_AUTOPLAY_KEY, enabled)
    }

    override fun observeCarouselStyle(): Flow<Int> =
        localPreferencesRepository.getInt(CAROUSEL_STYLE_KEY, 0)

    override suspend fun updateCarouselStyle(style: Int) {
        localPreferencesRepository.putInt(CAROUSEL_STYLE_KEY, style.coerceIn(0, 1))
    }

    override suspend fun updateDailyGoal(minutes: Int) {
        localPreferencesRepository.updatePreference(
            LocalPreferencesKeys.DAILY_READING_GOAL_MINUTES,
            minutes,
        )
    }

    override suspend fun updateSelectedSourceSetUrl(sourceUrl: String) {
        localPreferencesRepository.updatePreference(
            LocalPreferencesKeys.HOME_SOURCE_SET_URL,
            sourceUrl,
        )
    }

    override suspend fun updateVisibleSections(sections: List<HomeDashboardSection>) {
        // 只改组件顺序，保留「首页模块」关闭标记。
        val modulesDisabled = hasHomepageModulesDisabledToken(latestSectionsRaw)
        localPreferencesRepository.updatePreference(
            LocalPreferencesKeys.HOME_DASHBOARD_SECTIONS,
            encodeSections(sections.distinct(), modulesDisabled),
        )
    }

    override suspend fun updateHomepageModulesEnabled(enabled: Boolean) {
        // 只改开关，组件顺序沿用当前值。
        val sections = HomeDashboardSection.fromStorage(latestSectionsRaw)
        localPreferencesRepository.updatePreference(
            LocalPreferencesKeys.HOME_DASHBOARD_SECTIONS,
            encodeSections(sections, !enabled),
        )
    }

    private fun encodeSections(
        sections: List<HomeDashboardSection>,
        modulesDisabled: Boolean,
    ): String {
        val storage = HomeDashboardSection.toStorage(sections)
        if (!modulesDisabled) return storage
        return if (storage.isBlank()) {
            HOMEPAGE_MODULES_DISABLED_TOKEN
        } else {
            "$storage,$HOMEPAGE_MODULES_DISABLED_TOKEN"
        }
    }

    private fun hasHomepageModulesDisabledToken(raw: String): Boolean =
        raw.split(',').any { it.trim() == HOMEPAGE_MODULES_DISABLED_TOKEN }

    private companion object {
        /** 随机推荐是否自动轮播；用普通 String key 读写，避免再引入一条旧偏好调用。 */
        const val CAROUSEL_AUTOPLAY_KEY = "home_carousel_autoplay"

        /** 随机推荐卡片样式；0 = 经典横排，1 = Mochi 轮播。 */
        const val CAROUSEL_STYLE_KEY = "home_carousel_style"
    }
}
