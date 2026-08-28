package com.shuvo.quran.data

data class Ayah(
    val chapter: Int,
    val verse: Int,
    val text: String,
    val transliteration: String = "",
    val translation: String = ""
)