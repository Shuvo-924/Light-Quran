package com.shuvo.quran.data

data class Surah(
    val number: Int,
    val arabicName: String,
    val transliteration: String,
    val type: String,
    val verses: List<Ayah>
)