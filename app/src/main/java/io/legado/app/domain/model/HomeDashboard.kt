package io.legado.app.domain.model

data class HomeDashboard(
    val totalReadBooks: Int,
    val totalReadTimeMillis: Long,
    val todayReadTimeMillis: Long,
    val dailyGoalMinutes: Int,
    val recentBooks: List<HomeReadingBook>,
)

data class HomeReadingBook(
    val bookUrl: String?,
    val name: String,
    val author: String,
    val origin: String?,
    val coverPath: String?,
    val chapterTitle: String?,
    val chapterProgress: Float?,
)

/**
 * 首页卡片组件。[storageValue] 是偏好里的持久化标识；偏好串的先后顺序就是首页渲染顺序。
 */
enum class HomeDashboardSection(val storageValue: String) {
    RecentBook("recent_book"),
    BookCarousel("book_carousel"),
    TotalReadBooks("total_read_books"),
    TotalReadTime("total_read_time"),
    RecentBooks("recent_books"),
    DailyGoal("daily_goal"),
    WebDavBackup("webdav_backup");

    companion object {
        /** 顺序即默认顺序；未知标识忽略，重复项只保留第一次出现的位置。 */
        fun fromStorage(value: String): List<HomeDashboardSection> {
            if (value.isBlank()) return DEFAULT_HOME_DASHBOARD_SECTION_ORDER
            val sections = value
                .split(',')
                .map(String::trim)
                .mapNotNull { stored ->
                    entries.firstOrNull { it.storageValue == stored }
                }
                .distinct()
            if (sections.isEmpty() || sections == LEGACY_DEFAULT_ORDER) {
                return DEFAULT_HOME_DASHBOARD_SECTION_ORDER
            }
            return sections
        }

        /**
         * 旧版本默认全开时存下的顺序。新增 [BookCarousel] 之前用户没可能主动关掉它，
         * 所以这类「老默认」值直接升级成新默认，避免老用户看不到新组件。
         */
        private val LEGACY_DEFAULT_ORDER = listOf(
            RecentBook,
            TotalReadBooks,
            TotalReadTime,
            RecentBooks,
            DailyGoal,
            WebDavBackup,
        )

        fun toStorage(sections: List<HomeDashboardSection>): String =
            sections.map { it.storageValue }.joinToString(",")
    }
}

/** 默认顺序（也用于偏好未写入时的兜底）。 */
val DEFAULT_HOME_DASHBOARD_SECTION_ORDER: List<HomeDashboardSection> =
    HomeDashboardSection.entries.toList()

val DEFAULT_HOME_DASHBOARD_SECTIONS: Set<HomeDashboardSection> =
    HomeDashboardSection.entries.toSet()

const val DEFAULT_DAILY_READING_GOAL_MINUTES = 30
const val MAX_DAILY_READING_GOAL_MINUTES = 24 * 60
