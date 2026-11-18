package io.legado.app.data.repository

import io.legado.app.constant.PreferKey
import io.legado.app.help.config.AppConfigStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * 可复用标签库：在「我的 → 标签管理」维护，书籍信息编辑时可直接点选，避免每次手输。
 *
 * 存储只走 AppConfigStore（设置快照层），不放 Room：标签数量很小，且需要跨进程/备份可见。
 */
object ReusableTagRepository {

    private val separators = Regex("[,，、\\n]")

    fun tags(): List<String> = parse(AppConfigStore.getString(PreferKey.reusableTagLibrary))

    fun observeTags(): Flow<List<String>> =
        AppConfigStore.observeString(PreferKey.reusableTagLibrary)
            .map(::parse)
            .distinctUntilChanged()

    fun add(tag: String) {
        val trimmed = tag.trim()
        if (trimmed.isEmpty()) return
        val current = tags()
        if (current.any { it.equals(trimmed, ignoreCase = true) }) return
        save(current + trimmed)
    }

    fun remove(tag: String) {
        val current = tags()
        if (current.none { it == tag }) return
        save(current - tag)
    }

    fun save(tags: List<String>) {
        val cleaned = tags.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
        AppConfigStore.putString(PreferKey.reusableTagLibrary, cleaned.joinToString("\n"))
    }

    private fun parse(raw: String?): List<String> =
        raw.orEmpty()
            .split(separators)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
}
