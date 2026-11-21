package io.legado.app.ui.book.readRecord

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.legado.app.data.entities.Book
import io.legado.app.data.entities.readRecord.ReadRecord
import io.legado.app.data.entities.readRecord.ReadRecordDetail
import io.legado.app.data.entities.readRecord.ReadRecordSession
import io.legado.app.data.repository.BookRepository
import io.legado.app.data.repository.ReadRecordRepository
import io.legado.app.domain.gateway.HomeDashboardGateway
import io.legado.app.domain.model.DEFAULT_DAILY_READING_GOAL_MINUTES
import io.legado.app.domain.usecase.readRecord.GetReadRecordOverviewUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

@Stable
data class ReadRecordOverviewUiState(
    val period: ReadPeriod = ReadPeriod.DAY,
    val referenceDate: LocalDate = LocalDate.now(),
    val totalTime: Long = 0,
    val readingDays: Int = 0,
    val totalBooks: Int = 0,
    val finishedBooks: Int = 0,
    val readingBooks: Int = 0,
    val dailyTimeData: List<Pair<LocalDate, Long>> = emptyList(),
    val topBooks: List<ReadBookRanking> = emptyList(),
    val dailyTopBook: Map<LocalDate, Pair<String, String>> = emptyMap(),
    val allReadTimes: Map<LocalDate, Long> = emptyMap(),
    val allReadCounts: Map<LocalDate, Int> = emptyMap(),
    val hourlyMillis: List<Long> = List(24) { 0L },
    val mostReadHour: Int? = null,
    val mostReadDayPart: ReadingDayPart? = null,
    val averageStartMinute: Int? = null,
    val nightShare: Float = 0f,
    val maxSingleDayTime: Long = 0L,
    val longestSessionStart: Long? = null,
    val longestSessionEnd: Long? = null,
    val goalDays: Int = 0,
    val goalStreak: Int = 0,
    val categoryRankings: List<ReadCategoryRanking> = emptyList(),
    val dailyGoalMinutes: Int = DEFAULT_DAILY_READING_GOAL_MINUTES,
) {
    val hasTimeOfDayData: Boolean
        get() = hourlyMillis.sum() > 0L
}

data class ReadBookRanking(
    val bookName: String,
    val bookAuthor: String,
    val readTime: Long,
    var coverPath: String? = null
)

data class ReadCategoryRanking(
    val tag: String?,
    val readTime: Long,
    val bookCount: Int,
)

enum class ReadPeriod {
    DAY, WEEK, MONTH, YEAR, ALL
}

enum class ReadingDayPart(val startHour: Int, val endHour: Int) {
    DAWN(0, 6),
    MORNING(6, 12),
    AFTERNOON(12, 18),
    EVENING(18, 24),
}

private data class OverviewSource(
    val details: List<ReadRecordDetail>,
    val latestRecords: List<ReadRecord>,
    val allBooks: List<Book>,
    val sessions: List<ReadRecordSession>,
    val dailyGoalMinutes: Int,
)

class ReadRecordOverviewViewModel(
    private val repository: ReadRecordRepository,
    private val bookRepository: BookRepository,
    private val homeDashboardGateway: HomeDashboardGateway,
    private val getReadRecordOverviewUseCase: GetReadRecordOverviewUseCase
) : ViewModel() {

    private val _period = MutableStateFlow(ReadPeriod.DAY)
    private val _referenceDate = MutableStateFlow(LocalDate.now())

    private val source = combine(
        repository.getAllRecordDetails(""),
        repository.getLatestReadRecords(""),
        bookRepository.getAllBooks(),
        repository.getAllSessions(),
        homeDashboardGateway.observeDailyGoal(DEFAULT_DAILY_READING_GOAL_MINUTES),
    ) { details, latestRecords, allBooks, sessions, dailyGoalMinutes ->
        OverviewSource(
            details = details,
            latestRecords = latestRecords,
            allBooks = allBooks,
            sessions = sessions,
            dailyGoalMinutes = dailyGoalMinutes,
        )
    }

    val uiState: StateFlow<ReadRecordOverviewUiState> = combine(
        _period,
        _referenceDate,
        source,
    ) { period, refDate, data ->
        getReadRecordOverviewUseCase(
            period = period,
            refDate = refDate,
            details = data.details,
            latestRecords = data.latestRecords,
            allBooks = data.allBooks,
            sessions = data.sessions,
            dailyGoalMinutes = data.dailyGoalMinutes,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReadRecordOverviewUiState()
    )

    fun onIntent(intent: ReadRecordOverviewIntent) {
        when (intent) {
            is ReadRecordOverviewIntent.SetPeriod -> setPeriod(intent.period)
            ReadRecordOverviewIntent.NextDate -> nextDate()
            ReadRecordOverviewIntent.PreviousDate -> prevDate()
        }
    }

    fun setPeriod(period: ReadPeriod) {
        _period.value = period
    }

    fun nextDate() {
        val current = _referenceDate.value
        _referenceDate.value = when (_period.value) {
            ReadPeriod.DAY -> current.plusDays(1)
            ReadPeriod.WEEK -> current.plusWeeks(1)
            ReadPeriod.MONTH -> current.plusMonths(1)
            ReadPeriod.YEAR -> current.plusYears(1)
            ReadPeriod.ALL -> current
        }
    }

    fun prevDate() {
        val current = _referenceDate.value
        _referenceDate.value = when (_period.value) {
            ReadPeriod.DAY -> current.minusDays(1)
            ReadPeriod.WEEK -> current.minusWeeks(1)
            ReadPeriod.MONTH -> current.minusMonths(1)
            ReadPeriod.YEAR -> current.minusYears(1)
            ReadPeriod.ALL -> current
        }
    }

    suspend fun getBookCover(name: String, author: String) = bookRepository.getBookCoverByNameAndAuthor(name, author)
}

sealed interface ReadRecordOverviewIntent {
    data class SetPeriod(val period: ReadPeriod) : ReadRecordOverviewIntent
    data object NextDate : ReadRecordOverviewIntent
    data object PreviousDate : ReadRecordOverviewIntent
}

sealed interface ReadRecordOverviewEffect
