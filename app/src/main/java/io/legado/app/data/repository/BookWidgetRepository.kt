package io.legado.app.data.repository

import io.legado.app.data.appDb
import io.legado.app.data.entities.Book
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** 桌面组件的数据入口，避免 UI 直接依赖 DAO。 */
object BookWidgetRepository {

    suspend fun getShelfBooks(limit: Int): List<Book> =
        appDb.bookDao.getWidgetShelfBooks(limit)

    suspend fun getRecentBook(): Book? =
        appDb.bookDao.getWidgetRecentBook()

    suspend fun getTodayReadMinutes(): Int {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val millis = appDb.readRecordDao.observeReadTimeByDate(date).first() ?: 0L
        return (millis / 60_000L).toInt()
    }
}
