package com.shuvo.quran.data

import android.content.Context
import androidx.core.content.edit

class ReadingPreferences(
    context: Context
) {

    private val preferences =
        context.getSharedPreferences(
            "quran_preferences",
            Context.MODE_PRIVATE
        )

    // -----------------------------------------
    // CONTINUE READING
    // -----------------------------------------

    fun saveLastRead(
        surah: Int,
        verse: Int
    ) {

        preferences.edit {
            putInt("last_surah", surah)
                .putInt("last_verse", verse)
        }
    }

    fun getLastRead(): Pair<Int, Int>? {

        if (!preferences.contains("last_surah")) {
            return null
        }

        return Pair(
            preferences.getInt("last_surah", 1),
            preferences.getInt("last_verse", 1)
        )
    }

    // -----------------------------------------
    // SINGLE BOOKMARK
    // -----------------------------------------

    fun saveBookmark(
        surah: Int,
        verse: Int
    ) {

        preferences.edit {
            putInt("bookmark_surah", surah)
                .putInt("bookmark_verse", verse)
        }
    }

    fun getBookmark(): Pair<Int, Int>? {

        if (!preferences.contains("bookmark_surah")) {
            return null
        }

        return Pair(
            preferences.getInt("bookmark_surah", 1),
            preferences.getInt("bookmark_verse", 1)
        )
    }

    fun clearBookmark() {

        preferences.edit {
            remove("bookmark_surah")
                .remove("bookmark_verse")
        }
    }

    fun isBookmarked(
        surah: Int,
        verse: Int
    ): Boolean {

        val bookmark =
            getBookmark()

        return bookmark?.first == surah &&
                bookmark.second == verse
    }

    // -----------------------------------------
    // FAVORITES
    // -----------------------------------------

    private fun favoriteKey(
        surah: Int,
        verse: Int
    ): String {

        return "$surah:$verse"
    }

    fun getFavorites(): Set<String> {

        return preferences
            .getStringSet(
                "favorites",
                emptySet()
            )
            ?.toSet()
            ?: emptySet()
    }

    fun isFavorite(
        surah: Int,
        verse: Int
    ): Boolean {

        return getFavorites().contains(
            favoriteKey(surah, verse)
        )
    }

    fun toggleFavorite(
        surah: Int,
        verse: Int
    ) {

        val favorites =
            getFavorites().toMutableSet()

        val key =
            favoriteKey(surah, verse)

        if (favorites.contains(key)) {

            favorites.remove(key)

        } else {

            favorites.add(key)
        }

        preferences.edit {
            putStringSet(
                "favorites",
                favorites
            )
        }
    }

    // -----------------------------------------
    // SETTINGS
    // -----------------------------------------

    fun getTranslationLanguage(): String =
        preferences.getString("translation_language", "bn") ?: "bn"

    fun setTranslationLanguage(lang: String) =
        preferences.edit { putString("translation_language", lang) }

    fun getTransliterationLanguage(): String =
        preferences.getString("transliteration_language", "bn") ?: "bn"

    fun setTransliterationLanguage(lang: String) =
        preferences.edit { putString("transliteration_language", lang) }

    fun getArabicFont(): String =
        preferences.getString("arabic_font", "Uthman Taha") ?: "Uthman Taha"

    fun setArabicFont(font: String) =
        preferences.edit { putString("arabic_font", font) }

    fun getArabicFontSize(): Int =
        preferences.getInt("arabic_font_size", 24)

    fun setArabicFontSize(size: Int) =
        preferences.edit { putInt("arabic_font_size", size) }

    fun getTranslationFontSize(): Int =
        preferences.getInt("translation_font_size", 16)

    fun setTranslationFontSize(size: Int) =
        preferences.edit { putInt("translation_font_size", size) }

    fun getLineSpacing(): Float =
        preferences.getFloat("line_spacing", 1.4f)

    fun setLineSpacing(spacing: Float) =
        preferences.edit { putFloat("line_spacing", spacing) }

    fun isShowTransliteration(): Boolean =
        preferences.getBoolean("show_transliteration", true)

    fun setShowTransliteration(show: Boolean) =
        preferences.edit { putBoolean("show_transliteration", show) }

    fun getTheme(): String =
        preferences.getString("theme", "Light") ?: "Light"

    fun setTheme(theme: String) =
        preferences.edit { putString("theme", theme) }
}