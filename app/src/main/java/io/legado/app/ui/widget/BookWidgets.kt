package io.legado.app.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import io.legado.app.R
import io.legado.app.data.entities.Book
import io.legado.app.data.repository.BookWidgetRepository
import io.legado.app.help.coil.CoverFileCache
import io.legado.app.help.glide.ImageLoader
import io.legado.app.ui.main.MainIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.math.roundToInt

/** 两个桌面组件共用同一套异步查询、刷新和跳转逻辑。 */
abstract class BaseBookWidgetProvider : AppWidgetProvider() {

    @get:LayoutRes
    protected abstract val layoutId: Int

    internal abstract suspend fun buildViews(context: Context, appWidgetId: Int): RemoteViews

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                appWidgetIds.forEach { appWidgetId ->
                    val views = runCatching { buildViews(context, appWidgetId) }.getOrNull()
                        ?: RemoteViews(context.packageName, layoutId)
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: android.os.Bundle,
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        refresh(context)
    }

    protected fun refresh(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val component = ComponentName(context, javaClass)
        val ids = manager.getAppWidgetIds(component)
        if (ids.isEmpty()) return
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            ids.forEach { appWidgetId ->
                val views = runCatching { buildViews(context, appWidgetId) }.getOrNull()
                    ?: RemoteViews(context.packageName, layoutId)
                manager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    protected fun openBookIntent(
        context: Context,
        book: Book,
        requestCode: Int,
    ): PendingIntent {
        val intent = MainIntent.createReadBookIntent(context, book.bookUrl)
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    /** 书架侧排点击进入详情页，而不是直接打开阅读界面。 */
    protected fun openBookDetailIntent(
        context: Context,
        book: Book,
        requestCode: Int,
    ): PendingIntent {
        val intent = MainIntent.createBookInfoIntent(
            context = context,
            name = book.name,
            author = book.author,
            bookUrl = book.bookUrl,
            origin = book.origin,
            coverPath = book.getDisplayCover(),
        )
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    protected suspend fun loadCover(context: Context, book: Book): Bitmap? {
        return runCatching {
            val cached = CoverFileCache.readByBookUrl(book.bookUrl)
            val model = cached?.absolutePath ?: book.getDisplayCover()
            ImageLoader.loadBitmap(context, model)
                .centerCrop()
                .submit(288, 384)
                .get(10, TimeUnit.SECONDS)
        }.getOrNull()
    }
}

/** 「书架侧排」：书架里的书按最近阅读顺序竖排成书脊。 */
class ShelfSpineWidgetProvider : BaseBookWidgetProvider() {

    override val layoutId: Int = R.layout.widget_shelf

    private val spineIds = intArrayOf(
        R.id.widget_spine_0,
        R.id.widget_spine_1,
        R.id.widget_spine_2,
        R.id.widget_spine_3,
        R.id.widget_spine_4,
        R.id.widget_spine_5,
        R.id.widget_spine_6,
        R.id.widget_spine_7,
        R.id.widget_spine_8,
    )

    private val spineImageIds = intArrayOf(
        R.id.widget_spine_img_0,
        R.id.widget_spine_img_1,
        R.id.widget_spine_img_2,
        R.id.widget_spine_img_3,
        R.id.widget_spine_img_4,
        R.id.widget_spine_img_5,
        R.id.widget_spine_img_6,
        R.id.widget_spine_img_7,
        R.id.widget_spine_img_8,
    )

    private val spineTextIds = intArrayOf(
        R.id.widget_spine_text_0,
        R.id.widget_spine_text_1,
        R.id.widget_spine_text_2,
        R.id.widget_spine_text_3,
        R.id.widget_spine_text_4,
        R.id.widget_spine_text_5,
        R.id.widget_spine_text_6,
        R.id.widget_spine_text_7,
        R.id.widget_spine_text_8,
    )

    override suspend fun buildViews(context: Context, appWidgetId: Int): RemoteViews {
        val views = RemoteViews(context.packageName, layoutId)
        val books = BookWidgetRepository.getShelfBooks(spineIds.size)
        val covers = coroutineScope {
            books.map { book ->
                async { loadSpineCover(context, book) }
            }.awaitAll()
        }
        views.setViewVisibility(
            R.id.widget_shelf_empty,
            if (books.isEmpty()) View.VISIBLE else View.GONE,
        )
        spineIds.forEachIndexed { index, viewId ->
            val book = books.getOrNull(index)
            if (book == null) {
                views.setViewVisibility(viewId, View.GONE)
                return@forEachIndexed
            }
            views.setViewVisibility(viewId, View.VISIBLE)
            views.setTextViewText(spineTextIds[index], verticalTitle(book.name))
            val cover = covers.getOrNull(index)
            if (cover != null) {
                views.setViewVisibility(spineImageIds[index], View.VISIBLE)
                views.setImageViewBitmap(spineImageIds[index], cover)
            } else {
                views.setViewVisibility(spineImageIds[index], View.GONE)
            }
            views.setOnClickPendingIntent(
                viewId,
                openBookDetailIntent(context, book, appWidgetId * 16 + index),
            )
        }
        return views
    }

    private suspend fun loadSpineCover(context: Context, book: Book): Bitmap? {
        val cached = CoverFileCache.readByBookUrl(book.bookUrl)
        val model = cached?.absolutePath ?: book.getDisplayCover()
        if (model.isNullOrBlank()) return null
        return runCatching {
            val raw = ImageLoader.loadBitmap(context, model)
                .centerCrop()
                .submit(48, 150)
                .get(3, TimeUnit.SECONDS)
            blurSpineBitmap(raw)
        }.getOrNull()
    }

    private fun verticalTitle(name: String): String {
        val clean = name.filterNot(Char::isWhitespace).ifEmpty { "?" }
        return clean.take(6).toList().joinToString("\n")
    }

    companion object {
        fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, ShelfSpineWidgetProvider::class.java)
            )
            if (ids.isEmpty()) return
            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                ids.forEach { id ->
                    val views = runCatching {
                        buildWidget(context.applicationContext, id)
                    }.getOrNull() ?: return@forEach
                    manager.updateAppWidget(id, views)
                }
            }
        }

        private suspend fun buildWidget(context: Context, appWidgetId: Int): RemoteViews {
            val provider = ShelfSpineWidgetProvider()
            return provider.buildViews(context, appWidgetId)
        }
    }
}

/** 「最近阅读」：最近一本书 + 今日阅读时长 + 继续阅读按钮。 */
class RecentReadingWidgetProvider : BaseBookWidgetProvider() {

    override val layoutId: Int = R.layout.widget_recent

    override suspend fun buildViews(context: Context, appWidgetId: Int): RemoteViews {
        val views = RemoteViews(context.packageName, layoutId)
        val book = BookWidgetRepository.getRecentBook()
        if (book == null) {
            views.setViewVisibility(R.id.widget_recent_action, View.GONE)
            views.setViewVisibility(R.id.widget_recent_time, View.GONE)
            views.setTextViewText(R.id.widget_recent_title, context.getString(R.string.widget_no_recent))
            views.setTextViewText(
                R.id.widget_recent_author,
                context.getString(R.string.widget_open_a_book),
            )
            views.setImageViewResource(R.id.widget_recent_cover, R.drawable.image_cover_default)
            return views
        }
        views.setViewVisibility(R.id.widget_recent_action, View.VISIBLE)
        views.setViewVisibility(R.id.widget_recent_time, View.VISIBLE)
        views.setTextViewText(R.id.widget_recent_title, book.name)
        views.setTextViewText(R.id.widget_recent_author, book.author)
        views.setTextViewText(
            R.id.widget_recent_time,
            context.getString(R.string.widget_read_progress, book.readProgressPercent()),
        )
        val pending = openBookIntent(context, book, appWidgetId * 16 + 15)
        views.setOnClickPendingIntent(R.id.widget_recent_action, pending)
        views.setOnClickPendingIntent(R.id.widget_recent_card, pending)
        val cover = loadCover(context, book)
        if (cover != null) {
            views.setImageViewBitmap(R.id.widget_recent_cover, cover)
        } else {
            views.setImageViewResource(R.id.widget_recent_cover, R.drawable.image_cover_default)
        }
        return views
    }

    companion object {
        fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, RecentReadingWidgetProvider::class.java)
            )
            if (ids.isEmpty()) return
            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                ids.forEach { id ->
                    val views = runCatching {
                        buildWidget(context.applicationContext, id)
                    }.getOrNull() ?: return@forEach
                    manager.updateAppWidget(id, views)
                }
            }
        }

        private suspend fun buildWidget(context: Context, appWidgetId: Int): RemoteViews {
            val provider = RecentReadingWidgetProvider()
            return provider.buildViews(context, appWidgetId)
        }
    }
}

/** 应用回到前台时刷新两个组件，保证阅读进度和最近一本是最新的。 */
object BookWidgets {
    fun refreshAll(context: Context) {
        ShelfSpineWidgetProvider.refreshAll(context)
        RecentReadingWidgetProvider.refreshAll(context)
    }
}

/** 最近阅读组件显示整本书的阅读进度，而不是今日时长。 */
private fun Book.readProgressPercent(): Int {
    if (totalChapterNum <= 0) return 0
    return (((durChapterIndex + 1) * 100) / totalChapterNum).coerceIn(0, 100)
}

/**
 * 书脊封面模糊：85% 强度按书脊短边换算出半径，连续三次盒式模糊近似高斯。
 * 48×150 的位图很小，直接像素处理即可，不依赖 RenderScript / RenderEffect。
 */
private fun blurSpineBitmap(source: Bitmap): Bitmap {
    val width = source.width
    val height = source.height
    if (width <= 0 || height <= 0) return source
    val software = if (source.config == Bitmap.Config.ARGB_8888) {
        source
    } else {
        source.copy(Bitmap.Config.ARGB_8888, false) ?: return source
    }
    val radius = (min(width, height) * 0.85f / 8f)
        .roundToInt()
        .coerceIn(1, 8)
    var pixels = IntArray(width * height)
    software.getPixels(pixels, 0, width, 0, 0, width, height)
    repeat(3) {
        pixels = boxBlur(pixels, width, height, radius)
    }
    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        setPixels(pixels, 0, width, 0, 0, width, height)
    }
}

private fun boxBlur(pixels: IntArray, width: Int, height: Int, radius: Int): IntArray {
    val temp = IntArray(width * height)
    val result = IntArray(width * height)
    for (y in 0 until height) {
        var a = 0
        var r = 0
        var g = 0
        var b = 0
        for (i in -radius..radius) {
            val x = i.coerceIn(0, width - 1)
            val color = pixels[y * width + x]
            a += Color.alpha(color)
            r += Color.red(color)
            g += Color.green(color)
            b += Color.blue(color)
        }
        val count = radius * 2 + 1
        for (x in 0 until width) {
            temp[y * width + x] = Color.argb(
                a / count,
                r / count,
                g / count,
                b / count,
            )
            val removeX = (x - radius).coerceIn(0, width - 1)
            val addX = (x + radius + 1).coerceIn(0, width - 1)
            val remove = pixels[y * width + removeX]
            val add = pixels[y * width + addX]
            a += Color.alpha(add) - Color.alpha(remove)
            r += Color.red(add) - Color.red(remove)
            g += Color.green(add) - Color.green(remove)
            b += Color.blue(add) - Color.blue(remove)
        }
    }
    for (x in 0 until width) {
        var a = 0
        var r = 0
        var g = 0
        var b = 0
        for (i in -radius..radius) {
            val y = i.coerceIn(0, height - 1)
            val color = temp[y * width + x]
            a += Color.alpha(color)
            r += Color.red(color)
            g += Color.green(color)
            b += Color.blue(color)
        }
        val count = radius * 2 + 1
        for (y in 0 until height) {
            result[y * width + x] = Color.argb(
                a / count,
                r / count,
                g / count,
                b / count,
            )
            val removeY = (y - radius).coerceIn(0, height - 1)
            val addY = (y + radius + 1).coerceIn(0, height - 1)
            val remove = temp[removeY * width + x]
            val add = temp[addY * width + x]
            a += Color.alpha(add) - Color.alpha(remove)
            r += Color.red(add) - Color.red(remove)
            g += Color.green(add) - Color.green(remove)
            b += Color.blue(add) - Color.blue(remove)
        }
    }
    return result
}