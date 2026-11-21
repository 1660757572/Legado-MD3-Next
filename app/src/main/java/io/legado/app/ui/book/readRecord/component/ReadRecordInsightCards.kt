package io.legado.app.ui.book.readRecord.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.legado.app.R
import io.legado.app.ui.book.readRecord.ReadCategoryRanking
import io.legado.app.ui.book.readRecord.ReadRecordFormatter
import io.legado.app.ui.book.readRecord.ReadingDayPart
import io.legado.app.ui.theme.LegadoTheme
import io.legado.app.ui.theme.adaptiveHorizontalPadding
import io.legado.app.ui.widget.components.card.GlassCard
import io.legado.app.ui.widget.components.text.AppText
import kotlin.math.roundToInt

@Composable
fun ReadingTimeOfDayCard(
    hourlyMillis: List<Long>,
    mostReadHour: Int?,
    mostReadDayPart: ReadingDayPart?,
    averageStartMinute: Int?,
    nightShare: Float,
    modifier: Modifier = Modifier,
) {
    val normalized = remember(hourlyMillis) {
        if (hourlyMillis.size == 24) hourlyMillis else List(24) { hourlyMillis.getOrElse(it) { 0L } }
    }
    val rawMax = normalized.maxOrNull()?.coerceAtLeast(1L) ?: 1L
    val scaleMax = when {
        rawMax < 15 * 60_000L -> 15 * 60_000L
        rawMax < 30 * 60_000L -> 30 * 60_000L
        rawMax < 60 * 60_000L -> 60 * 60_000L
        rawMax < 2 * 60 * 60_000L -> 2 * 60 * 60_000L
        rawMax < 4 * 60 * 60_000L -> 4 * 60 * 60_000L
        else -> ((rawMax + 60 * 60_000L - 1) / (60 * 60_000L)) * 60 * 60_000L
    }
    val peakLabel = mostReadHour?.let { formatHour(it) } ?: "—"
    val distributionDescription = stringResource(
        R.string.a11y_time_of_day_distribution,
        peakLabel,
    )

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .adaptiveHorizontalPadding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .semantics { contentDescription = distributionDescription },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = LegadoTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                AppText(
                    text = stringResource(R.string.reading_time_of_day),
                    style = LegadoTheme.typography.titleMedium,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(124.dp),
            ) {
                Column(
                    modifier = Modifier
                        .width(34.dp)
                        .fillMaxHeight()
                        .padding(bottom = 22.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End,
                ) {
                    AppText(
                        text = ReadRecordFormatter.formatDuration(scaleMax),
                        style = LegadoTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = LegadoTheme.colorScheme.onSurfaceVariant,
                    )
                    AppText(
                        text = "0",
                        style = LegadoTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = LegadoTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        normalized.forEachIndexed { hour, millis ->
                            val factor by animateFloatAsState(
                                targetValue = (millis.toFloat() / scaleMax).coerceIn(0f, 1f),
                                animationSpec = tween(durationMillis = 260, delayMillis = hour * 8),
                                label = "HourBar",
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.BottomCenter,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.72f)
                                        .fillMaxHeight(if (millis > 0L) factor.coerceAtLeast(0.02f) else 0.01f)
                                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                        .background(
                                            if (millis > 0L) {
                                                LegadoTheme.colorScheme.primary
                                            } else {
                                                LegadoTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                            }
                                        ),
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        listOf("0", "6", "12", "18", "24").forEach { label ->
                            AppText(
                                text = label,
                                style = LegadoTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = LegadoTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TimeOfDayStat(
                    label = stringResource(R.string.most_read_time),
                    value = peakLabel,
                    modifier = Modifier.weight(1f),
                )
                TimeOfDayStat(
                    label = stringResource(R.string.most_read_period),
                    value = mostReadDayPart?.label() ?: "—",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TimeOfDayStat(
                    label = stringResource(R.string.average_start_time),
                    value = averageStartMinute?.let(::formatMinuteOfDay) ?: "—",
                    modifier = Modifier.weight(1f),
                )
                TimeOfDayStat(
                    label = stringResource(R.string.night_reading_share),
                    value = "${(nightShare * 100).roundToInt()}%",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun ReadingPeriodShareCard(
    hourlyMillis: List<Long>,
    modifier: Modifier = Modifier,
) {
    val normalized = remember(hourlyMillis) {
        if (hourlyMillis.size == 24) hourlyMillis else List(24) { hourlyMillis.getOrElse(it) { 0L } }
    }
    val parts = listOf(
        ReadingDayPart.DAWN to normalized.take(6).sum(),
        ReadingDayPart.MORNING to normalized.drop(6).take(6).sum(),
        ReadingDayPart.AFTERNOON to normalized.drop(12).take(6).sum(),
        ReadingDayPart.EVENING to normalized.drop(18).take(6).sum(),
    )
    val total = parts.sumOf { it.second }.coerceAtLeast(1L)
    val colors = listOf(
        LegadoTheme.colorScheme.primary,
        LegadoTheme.colorScheme.tertiary,
        LegadoTheme.colorScheme.secondary,
        LegadoTheme.colorScheme.primary.copy(alpha = 0.45f),
    )

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .adaptiveHorizontalPadding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PieChart,
                    contentDescription = null,
                    tint = LegadoTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                AppText(
                    text = stringResource(R.string.reading_period_share),
                    style = LegadoTheme.typography.titleMedium,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(LegadoTheme.colorScheme.surfaceVariant),
            ) {
                parts.forEachIndexed { index, (_, millis) ->
                    if (millis > 0L) {
                        Box(
                            modifier = Modifier
                                .weight(millis.toFloat())
                                .fillMaxHeight()
                                .background(colors[index]),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                parts.withIndex().chunked(2).forEach { rowParts ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        rowParts.forEach { (globalIndex, pair) ->
                            val (part, millis) = pair
                            PeriodLegend(
                                part = part,
                                millis = millis,
                                total = total,
                                color = colors[globalIndex],
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReadingCategoryRankingCard(
    items: List<ReadCategoryRanking>,
    totalTime: Long,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return
    val maxTime = items.maxOf { it.readTime }.coerceAtLeast(1L)
    val otherLabel = stringResource(R.string.category_other)

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .adaptiveHorizontalPadding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Leaderboard,
                    contentDescription = null,
                    tint = LegadoTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                AppText(
                    text = stringResource(R.string.reading_category_ranking),
                    style = LegadoTheme.typography.titleMedium,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            items.forEachIndexed { index, item ->
                val share = (item.readTime.toFloat() / maxTime).coerceIn(0f, 1f)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppText(
                        text = "${index + 1}",
                        style = LegadoTheme.typography.labelMedium,
                        color = if (index < 3) {
                            LegadoTheme.colorScheme.primary
                        } else {
                            LegadoTheme.colorScheme.onSurfaceVariant
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(26.dp),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AppText(
                                text = item.tag ?: otherLabel,
                                style = LegadoTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                            AppText(
                                text = ReadRecordFormatter.formatDuration(item.readTime),
                                style = LegadoTheme.typography.labelSmall,
                                color = LegadoTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(LegadoTheme.colorScheme.surfaceVariant),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(share)
                                    .fillMaxHeight()
                                    .background(
                                        if (index < 3) {
                                            LegadoTheme.colorScheme.primary
                                        } else {
                                            LegadoTheme.colorScheme.primary.copy(alpha = 0.55f)
                                        }
                                    ),
                            )
                        }
                    }
                    if (totalTime > 0L) {
                        AppText(
                            text = "${(item.readTime * 100.0 / totalTime).roundToInt()}%",
                            style = LegadoTheme.typography.labelSmall,
                            color = LegadoTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeOfDayStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        AppText(
            text = label,
            style = LegadoTheme.typography.labelSmall,
            color = LegadoTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(2.dp))
        AppText(
            text = value,
            style = LegadoTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = LegadoTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun PeriodLegend(
    part: ReadingDayPart,
    millis: Long,
    total: Long,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(color),
        )
        Spacer(modifier = Modifier.width(7.dp))
        AppText(
            text = part.label(),
            style = LegadoTheme.typography.labelSmall,
            color = LegadoTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(6.dp))
        AppText(
            text = "${(millis * 100.0 / total).roundToInt()}%",
            style = LegadoTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = LegadoTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ReadingDayPart.label(): String = when (this) {
    ReadingDayPart.DAWN -> stringResource(R.string.period_dawn)
    ReadingDayPart.MORNING -> stringResource(R.string.period_morning)
    ReadingDayPart.AFTERNOON -> stringResource(R.string.period_afternoon)
    ReadingDayPart.EVENING -> stringResource(R.string.period_evening)
}

private fun formatHour(hour: Int): String = "%02d:00".format(hour)

private fun formatMinuteOfDay(minuteOfDay: Int): String {
    val hour = minuteOfDay / 60
    val minute = minuteOfDay % 60
    return "%02d:%02d".format(hour, minute)
}
