package io.legado.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeDashboardSectionTest {

    @Test
    fun `blank storage falls back to default order`() {
        assertEquals(
            DEFAULT_HOME_DASHBOARD_SECTION_ORDER,
            HomeDashboardSection.fromStorage(""),
        )
    }

    @Test
    fun `custom order is preserved as stored`() {
        val stored = "webdav_backup,daily_goal,recent_book"

        assertEquals(
            listOf(
                HomeDashboardSection.WebDavBackup,
                HomeDashboardSection.DailyGoal,
                HomeDashboardSection.RecentBook,
            ),
            HomeDashboardSection.fromStorage(stored),
        )
    }

    @Test
    fun `legacy default is upgraded so the new carousel shows up`() {
        val legacy = listOf(
            HomeDashboardSection.RecentBook,
            HomeDashboardSection.TotalReadBooks,
            HomeDashboardSection.TotalReadTime,
            HomeDashboardSection.RecentBooks,
            HomeDashboardSection.DailyGoal,
            HomeDashboardSection.WebDavBackup,
        )

        assertEquals(
            DEFAULT_HOME_DASHBOARD_SECTION_ORDER,
            HomeDashboardSection.fromStorage(HomeDashboardSection.toStorage(legacy)),
        )
    }

    @Test
    fun `unknown or duplicated values are dropped`() {
        val parsed = HomeDashboardSection.fromStorage(
            "unknown_section,daily_goal,daily_goal,recent_book",
        )

        assertEquals(
            listOf(HomeDashboardSection.DailyGoal, HomeDashboardSection.RecentBook),
            parsed,
        )
    }

    @Test
    fun `storage round trip keeps the order`() {
        val sections = listOf(
            HomeDashboardSection.BookCarousel,
            HomeDashboardSection.RecentBooks,
            HomeDashboardSection.TotalReadTime,
        )

        assertEquals(
            sections,
            HomeDashboardSection.fromStorage(HomeDashboardSection.toStorage(sections)),
        )
    }
}
