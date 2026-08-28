package com.shuvo.quran.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.URL

class QuranRepository(
    private val context: Context,
    private val preferences: ReadingPreferences
) {

    private var surahs: List<Surah>? = null

    private fun readAsset(fileName: String): String {
        return context.assets
            .open(fileName)
            .bufferedReader()
            .use { it.readText() }
    }

    private fun readFile(fileName: String): String? {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            file.readText()
        } else {
            null
        }
    }

    private fun parseVerseTextMap(fileName: String, isAsset: Boolean = true): Map<Pair<Int, Int>, String> {
        val jsonStr = if (isAsset) readAsset(fileName) else readFile(fileName)
        if (jsonStr == null) return emptyMap()

        return try {
            val json = JSONObject(jsonStr)
            val map = mutableMapOf<Pair<Int, Int>, String>()
            val keys = json.keys()

            while (keys.hasNext()) {
                val surahKey = keys.next()
                val versesArray = json.getJSONArray(surahKey)
                for (i in 0 until versesArray.length()) {
                    val verseObj = versesArray.getJSONObject(i)
                    val chapter = verseObj.optInt("chapter", surahKey.toIntOrNull() ?: 0)
                    val verse = verseObj.getInt("verse")
                    map[chapter to verse] = verseObj.optString("text")
                }
            }
            map
        } catch (e: Exception) {
            // Try parsing as JSONArray if JSONObject fails (different formats)
            try {
                val array = JSONArray(jsonStr)
                val map = mutableMapOf<Pair<Int, Int>, String>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val chapter = obj.getInt("chapter")
                    val verse = obj.getInt("verse")
                    map[chapter to verse] = obj.optString("text")
                }
                map
            } catch (e2: Exception) {
                emptyMap()
            }
        }
    }

    private fun parseTransliterationMap(lang: String): Map<Pair<Int, Int>, String> {
        val fileName = if (lang == "bn") "quran_transliteration_bn.json" else "quran_transliteration.json"
        val jsonStr = readAsset(fileName)
        val map = mutableMapOf<Pair<Int, Int>, String>()

        try {
            if (lang == "bn") {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val surah = obj.getInt("surah")
                    val ayah = obj.getInt("ayah")
                    map[surah to ayah] = obj.optString("pronunciation_bn")
                }
            } else {
                val json = JSONObject(jsonStr)
                val keys = json.keys()
                while (keys.hasNext()) {
                    val surahKey = keys.next()
                    val versesArray = json.getJSONArray(surahKey)
                    for (i in 0 until versesArray.length()) {
                        val verseObj = versesArray.getJSONObject(i)
                        val chapter = verseObj.optInt("chapter", surahKey.toIntOrNull() ?: 0)
                        val verse = verseObj.getInt("verse")
                        map[chapter to verse] = verseObj.optString("text")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return map
    }

    private fun loadQuran(): List<Surah> {
        surahs?.let { return it }

        val transLang = preferences.getTranslationLanguage()
        val translationMap = if (transLang == "bn") {
            parseVerseTextMap("quran_bn.json")
        } else if (transLang == "en") {
            parseVerseTextMap("quran_en.json")
        } else {
            // Load from internal storage if downloaded
            parseVerseTextMap("quran_$transLang.json", isAsset = false)
        }

        val translitLang = preferences.getTransliterationLanguage()
        val transliterationMap = parseTransliterationMap(translitLang)

        val chaptersJson = JSONArray(readAsset("chapters.json"))
        val metadataMap = mutableMapOf<Int, ChapterMetadata>()
        for (i in 0 until chaptersJson.length()) {
            val item = chaptersJson.getJSONObject(i)
            val id = item.getInt("id")
            metadataMap[id] = ChapterMetadata(
                arabicName = item.optString("name"),
                transliteration = item.optString("transliteration"),
                type = item.optString("type")
            )
        }

        val quranJson = JSONObject(readAsset("quran.json"))
        val result = mutableListOf<Surah>()
        val keys = quranJson.keys()

        while (keys.hasNext()) {
            val surahNumberStr = keys.next()
            val surahNumber = surahNumberStr.toIntOrNull() ?: continue
            val versesArray = quranJson.getJSONArray(surahNumberStr)
            val ayahs = mutableListOf<Ayah>()

            for (i in 0 until versesArray.length()) {
                val vObj = versesArray.getJSONObject(i)
                val chapter = vObj.getInt("chapter")
                val verse = vObj.getInt("verse")
                val verseKey = chapter to verse

                ayahs.add(
                    Ayah(
                        chapter = chapter,
                        verse = verse,
                        text = vObj.getString("text"),
                        transliteration = transliterationMap[verseKey] ?: "",
                        translation = translationMap[verseKey] ?: ""
                    )
                )
            }

            val meta = metadataMap[surahNumber]
            result.add(
                Surah(
                    number = surahNumber,
                    arabicName = meta?.arabicName ?: "",
                    transliteration = meta?.transliteration ?: "Surah $surahNumber",
                    type = meta?.type ?: "",
                    verses = ayahs
                )
            )
        }

        val finalResult = result.sortedBy { it.number }
        surahs = finalResult
        return finalResult
    }

    fun clearCache() {
        surahs = null
    }

    fun downloadTranslation(langCode: String, onComplete: (Boolean) -> Unit) {
        Thread {
            try {
                val url = URL("https://raw.githubusercontent.com/risan/quran-json/main/data/$langCode.json")
                val content = url.readText()
                val file = File(context.filesDir, "quran_$langCode.json")
                file.writeText(content)
                onComplete(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }.start()
    }

    // ... Keep getSurahs(), getSurah(), search() methods as they were ...
    fun getSurahs(): List<Surah> = loadQuran()

    fun getSurah(surahNumber: Int): List<Ayah> {
        return loadQuran().firstOrNull { it.number == surahNumber }?.verses ?: emptyList()
    }

    fun getSurahInfo(surahNumber: Int): Surah? {
        return loadQuran().firstOrNull { it.number == surahNumber }
    }

    fun search(query: String): List<SearchResult> {
        if (query.isBlank()) return emptyList()
        val searchText = query.trim().lowercase()
        val results = mutableListOf<SearchResult>()

        for (surah in loadQuran()) {
            if (surah.transliteration.lowercase().contains(searchText) ||
                surah.arabicName.contains(query.trim())) {
                results.add(SearchResult.SurahResult(surah.number, surah.transliteration, surah.arabicName))
            }
            for (ayah in surah.verses) {
                if (ayah.text.contains(query.trim()) ||
                    ayah.translation.lowercase().contains(searchText) ||
                    ayah.transliteration.lowercase().contains(searchText)) {
                    results.add(SearchResult.AyahResult(surah.number, ayah.verse, ayah.translation))
                }
            }
        }
        return results
    }
}

// Data Classes
data class ChapterMetadata(
    val arabicName: String,
    val transliteration: String,
    val type: String
)

sealed class SearchResult {
    data class SurahResult(val surahNumber: Int, val name: String, val arabicName: String) : SearchResult()
    data class AyahResult(val surahNumber: Int, val verseNumber: Int, val text: String) : SearchResult()
}