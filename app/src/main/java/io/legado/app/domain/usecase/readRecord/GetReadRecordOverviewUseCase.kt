package io.legado.app.domain.usecase.readRecord

import io.legado.app.data.entities.Book
import io.legado.app.data.entities.readRecord.ReadRecord
import io.legado.app.data.entities.readRecord.ReadRecordDetail
import io.legado.app.data.entities.readRecord.ReadRecordSession
import io.legado.app.ui.book.readRecord.ReadBookRanking
import io.legado.app.ui.book.readRecord.ReadCategoryRanking
import io.legado.app.ui.book.readRecord.ReadPeriod
import io.legado.app.ui.book.readRecord.ReadRecordOverviewUiState
import io.legado.app.ui.book.readRecord.ReadingDayPart
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import kotlin.math.roundToInt

class GetReadRecordOverviewUseCase {

    operator fun invoke(
        period: ReadPeriod,
        refDate: LocalDate,
        details: List<ReadRecordDetail>,
        latestRecords: List<ReadRecord>,
        allBooks: List<Book>,
        sessions: List<ReadRecordSession>,
        dailyGoalMinutes: Int
    ): ReadRecordOverviewUiState {
        val (startDate, endDate) = getPeriodRange(period, refDate)

        val filteredDetails = if (period == ReadPeriod.ALL) {
            details
        } else {
            details.filter {
                try {
                    val d = LocalDate.parse(it.date, DateTimeFormatter.ISO_LOCAL_DATE)
                    !d.isBefore(startDate) && !d.isAfter(endDate)
                } catch (e: Exception) {
                    false
                }
            }
        }

        // 阅读时间口径与阅读记录页/首页一致：「总」模式使用合并后的 readRecord 汇总
        // （包含无每日详情来源的旧版遗留时长），周期模式只能按有日期的详情统计。
        val totalTime = if (period == ReadPeriod.ALL) {
            latestRecords.sumOf { it.readTime }
        } else {
            filteredDetails.sumOf { it.readTime }
        }
        val readingDays = filteredDetails.map { it.date }.distinct().size

        val periodBooks = filteredDetails.groupBy { it.bookName to it.bookAuthor }
        val totalBooks = periodBooks.size

        val shelfBooksMap = latestRecords.associateBy { it.bookName to it.bookAuthor }
        val allShelfBooksMap = allBooks.associateBy { it.name to it.author }

        var readingCount = 0
        var finishedCount = 0

        periodBooks.keys.forEach { key ->
            if (shelfBooksMap.containsKey(key)) {
                readingCount++
            }
            // 判断是否读完
            allShelfBooksMap[key]?.let { book ->
                if (book.totalChapterNum > 0 &&
                    book.durChapterIndex >= book.totalChapterNum - 1 &&
                    book.durChapterPos != 0
                ) {
                    finishedCount++
                }
            }
        }

        val topBooks = periodBooks.map { (key, bookDetails) ->
            ReadBookRanking(
                bookName = key.first,
                bookAuthor = key.second,
                readTime = bookDetails.sumOf { it.readTime }
            )
        }.sortedByDescending { it.readTime }.take(10)

        val dailyTopBookMap = details.groupBy { it.date }
            .mapNotNull { (dateStr, dayDetails) ->
                try {
                    val date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
                    val top = dayDetails.maxByOrNull { it.readTime }
                    if (top != null) date to (top.bookName to top.bookAuthor) else null
                } catch (e: Exception) {
                    null
                }
            }.toMap()

        val dailyTimeData = if (period == ReadPeriod.ALL) {
            emptyList()
        } else if (period == ReadPeriod.YEAR) {
            val dateToTime = filteredDetails.groupBy {
                LocalDate.parse(it.date, DateTimeFormatter.ISO_LOCAL_DATE)
                    .with(TemporalAdjusters.firstDayOfMonth())
            }.mapValues { it.value.sumOf { d -> d.readTime } }

            val monthList = mutableListOf<Pair<LocalDate, Long>>()
            var curr = startDate.with(TemporalAdjusters.firstDayOfMonth())
            while (!curr.isAfter(endDate)) {
                monthList.add(curr to (dateToTime[curr] ?: 0L))
                curr = curr.plusMonths(1)
            }
            monthList
        } else {
            val dateToTime = filteredDetails.groupBy { it.date }
                .mapValues { it.value.sumOf { d -> d.readTime } }

            val daysList = mutableListOf<Pair<LocalDate, Long>>()
            var curr = startDate
            while (!curr.isAfter(endDate)) {
                daysList.add(curr to (dateToTime[curr.format(DateTimeFormatter.ISO_LOCAL_DATE)] ?: 0L))
                curr = curr.plusDays(1)
            }
            daysList
        }

        val allReadTimesMap = details.groupBy { it.date }
            .mapKeys {
                try {
                    LocalDate.parse(it.key, DateTimeFormatter.ISO_LOCAL_DATE)
                } catch (e: Exception) {
                    LocalDate.MIN
                }
            }
            .filterKeys { it != LocalDate.MIN }
            .mapValues { it.value.sumOf { d -> d.readTime } }

        val allReadCountsMap = details.groupBy { it.date }
            .mapKeys {
                try {
                    LocalDate.parse(it.key, DateTimeFormatter.ISO_LOCAL_DATE)
                } catch (e: Exception) {
                    LocalDate.MIN
                }
            }
            .filterKeys { it != LocalDate.MIN }
            .mapValues { it.value.size }

        val zone = ZoneId.systemDefault()
        val rangeStartMillis = if (period == ReadPeriod.ALL) {
            null
        } else {
            startDate.atStartOfDay(zone).toInstant().toEpochMilli()
        }
        val rangeEndMillis = if (period == ReadPeriod.ALL) {
            null
        } else {
            endDate.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        }

        val hourlyMillis = hourBuckets(sessions, rangeStartMillis, rangeEndMillis, zone)
        val hourlyTotal = hourlyMillis.sum()
        val mostReadHour = hourlyMillis.indices
            .maxByOrNull { hourlyMillis[it] }
            ?.takeIf { hourlyMillis[it] > 0L }
        val mostReadPart = mostReadHour?.let { hour ->
            ReadingDayPart.entries.firstOrNull { hour >= it.startHour && hour < it.endHour }
        }
        val averageStartMinute = averageStartMinute(
            sessions = sessions,
            rangeStartMillis = rangeStartMillis,
            rangeEndMillis = rangeEndMillis,
            zone = zone,
        )
        val nightShare = if (hourlyTotal > 0L) {
            hourlyMillis.take(6).sum().toFloat() / hourlyTotal
        } else {
            0f
        }

        val longestSession = sessions
            .filter { it.endTime > it.startTime }
            .filter { session ->
                if (rangeStartMillis == null || rangeEndMillis == null) {
                    true
                } else {
                    session.endTime > rangeStartMillis && session.startTime < rangeEndMillis
                }
            }
            .maxByOrNull { it.endTime - it.startTime }

        val filteredDailyTimes = filteredDetails.groupBy { it.date }
            .mapNotNull { (date, dayDetails) ->
                runCatching { LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE) }
                    .getOrNull()
                    ?.let { it to dayDetails.sumOf { detail -> detail.readTime } }
            }
            .toMap()
        val maxSingleDayTime = if (period == ReadPeriod.DAY) {
            0L
        } else {
            filteredDailyTimes.values.maxOrNull() ?: 0L
        }

        val goalMillis = dailyGoalMinutes.coerceAtLeast(1) * 60_000L
        val goalDays = allReadTimesMap.count { it.value >= goalMillis }
        val goalStreak = longestGoalStreak(
            dailyTimes = allReadTimesMap,
            goalMillis = goalMillis,
        )

        val categoryRankings = buildCategoryRankings(
            details = filteredDetails,
            allBooks = allBooks,
        )

        return ReadRecordOverviewUiState(
            period = period,
            referenceDate = refDate,
            totalTime = totalTime,
            readingDays = readingDays,
            totalBooks = totalBooks,
            finishedBooks = finishedCount,
            readingBooks = readingCount,
            dailyTimeData = dailyTimeData,
            topBooks = topBooks,
            dailyTopBook = dailyTopBookMap,
            allReadTimes = allReadTimesMap,
            allReadCounts = allReadCountsMap,
            hourlyMillis = hourlyMillis.toList(),
            mostReadHour = mostReadHour,
            mostReadDayPart = mostReadPart,
            averageStartMinute = averageStartMinute,
            nightShare = nightShare,
            maxSingleDayTime = maxSingleDayTime,
            longestSessionStart = longestSession?.startTime,
            longestSessionEnd = longestSession?.endTime,
            goalDays = goalDays,
            goalStreak = goalStreak,
            categoryRankings = categoryRankings,
            dailyGoalMinutes = dailyGoalMinutes.coerceAtLeast(1),
        )
    }

    private fun hourBuckets(
        sessions: List<ReadRecordSession>,
        rangeStartMillis: Long?,
        rangeEndMillis: Long?,
        zone: ZoneId,
    ): LongArray {
        val buckets = LongArray(24)
        val rangeStart = rangeStartMillis ?: 0L
        val rangeEnd = rangeEndMillis ?: Long.MAX_VALUE
        sessions.forEach { session ->
            if (session.endTime <= session.startTime) return@forEach
            if (session.endTime <= rangeStart || session.startTime >= rangeEnd) return@forEach
            var cursor = maxOf(session.startTime, rangeStart)
            val end = minOf(session.endTime, rangeEnd)
            while (cursor < end) {
                val zoned = Instant.ofEpochMilli(cursor).atZone(zone)
                val nextHour = zoned.truncatedTo(ChronoUnit.HOURS)
                    .plusHours(1)
                    .toInstant()
                    .toEpochMilli()
                    .coerceAtLeast(cursor + 1)
                val sliceEnd = minOf(end, nextHour)
                buckets[zoned.hour] += sliceEnd - cursor
                cursor = sliceEnd
            }
        }
        return buckets
    }

    private fun averageStartMinute(
        sessions: List<ReadRecordSession>,
        rangeStartMillis: Long?,
        rangeEndMillis: Long?,
        zone: ZoneId,
    ): Int? {
        val rangeStart = rangeStartMillis ?: 0L
        val rangeEnd = rangeEndMillis ?: Long.MAX_VALUE
        var weightedMinutes = 0.0
        var totalWeight = 0.0
        sessions.forEach { session ->
            val duration = (session.endTime - session.startTime).coerceAtLeast(0L)
            if (duration <= 0L) return@forEach
            if (session.startTime < rangeStart || session.startTime >= rangeEnd) return@forEach
            val start = Instant.ofEpochMilli(session.startTime).atZone(zone)
            val minuteOfDay = start.hour * 60 + start.minute
            weightedMinutes += minuteOfDay * duration
            totalWeight += duration
        }
        if (totalWeight <= 0.0) return null
        return (weightedMinutes / totalWeight).roundToInt().coerceIn(0, 24 * 60 - 1)
    }

    /**
     * 连续达标按“历史最长连续达标天数”统计，而不是只看今天是否还在连击上。
     * 用户过几天再次打开统计页时，过去已经形成的连击不应该归零。
     */
    private fun longestGoalStreak(
        dailyTimes: Map<LocalDate, Long>,
        goalMillis: Long,
    ): Int {
        val days = dailyTimes.entries
            .filter { it.value >= goalMillis }
            .map { it.key }
            .sorted()
        var longest = 0
        var current = 0
        var previous: LocalDate? = null
        for (day in days) {
            current = if (previous != null && day == previous.plusDays(1)) {
                current + 1
            } else {
                1
            }
            if (current > longest) longest = current
            previous = day
        }
        return longest
    }

    private fun buildCategoryRankings(
        details: List<ReadRecordDetail>,
        allBooks: List<Book>,
    ): List<ReadCategoryRanking> {
        val tagsByBook = allBooks.associate { book ->
            (book.name to book.author) to book.readingTags()
        }
        val timesByTag = mutableMapOf<String?, Long>()
        val booksByTag = mutableMapOf<String?, MutableSet<Pair<String, String>>>()
        details.groupBy { it.bookName to it.bookAuthor }.forEach { (bookKey, bookDetails) ->
            val readTime = bookDetails.sumOf { it.readTime }
            if (readTime <= 0L) return@forEach
            val tags: List<String?> = tagsByBook[bookKey].orEmpty()
                .takeIf { it.isNotEmpty() }
                ?: listOf(null)
            tags.forEach { tag ->
                timesByTag[tag] = (timesByTag[tag] ?: 0L) + readTime
                booksByTag.getOrPut(tag) { mutableSetOf() }.add(bookKey)
            }
        }
        return timesByTag.map { (tag, readTime) ->
            ReadCategoryRanking(
                tag = tag,
                readTime = readTime,
                bookCount = booksByTag[tag]?.size ?: 0,
            )
        }.sortedByDescending { it.readTime }.take(10)
    }

    private fun Book.readingTags(): List<String> {
        val raw = listOfNotNull(customTag, kind)
        return raw.flatMap { value ->
            value.split(Regex("[,，、;；/|\\n]+"))
        }
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
    }

    private fun getPeriodRange(period: ReadPeriod, refDate: LocalDate): Pair<LocalDate, LocalDate> {
        return when (period) {
            ReadPeriod.DAY -> refDate to refDate
            ReadPeriod.WEEK -> {
                val start = refDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val end = refDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                start to end
            }

            ReadPeriod.MONTH -> {
                val start = refDate.with(TemporalAdjusters.firstDayOfMonth())
                val end = refDate.with(TemporalAdjusters.lastDayOfMonth())
                start to end
            }

            ReadPeriod.YEAR -> {
                val start = refDate.with(TemporalAdjusters.firstDayOfYear())
                val end = refDate.with(TemporalAdjusters.lastDayOfYear())
                start to end
            }

            ReadPeriod.ALL -> LocalDate.MIN to LocalDate.MAX
        }
    }
}
