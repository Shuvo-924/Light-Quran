package com.shuvo.quran.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shuvo.quran.data.ReadingPreferences
import com.shuvo.quran.data.QuranRepository
import com.shuvo.quran.R

@Composable
fun QuranReaderScreen(
    surahNumber: Int,
    startVerse: Int = 1,
    repository: QuranRepository,
    preferences: ReadingPreferences,
    onBack: () -> Unit,
    onSurahChange: (Int) -> Unit
) {
    val surah = remember(surahNumber) {
        repository.getSurahInfo(surahNumber)
    }

    val ayahs = remember(surahNumber) {
        repository.getSurah(surahNumber)
    }

    val listState = rememberLazyListState()

    val arabicFontSize =
        preferences.getArabicFontSize().sp

    val translationFontSize =
        preferences.getTranslationFontSize().sp

    val lineSpacing =
        preferences.getLineSpacing()

    val showTransliteration =
        preferences.isShowTransliteration()

    var favoriteRefresh by remember {
        mutableStateOf(0)
    }

    // ---------------------------------------------------------
    // MOVE TO SPECIFIC VERSE
    // ---------------------------------------------------------

    LaunchedEffect(surahNumber, startVerse) {

        val index = ayahs.indexOfFirst {
            it.verse == startVerse
        }

        if (index >= 0) {

            listState.scrollToItem(index)
        }
    }

    // ---------------------------------------------------------
    // SAVE CURRENT READING POSITION
    // ---------------------------------------------------------

    val firstVisibleIndex by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex
        }
    }

    LaunchedEffect(
        surahNumber,
        firstVisibleIndex
    ) {

        if (
            ayahs.isNotEmpty() &&
            firstVisibleIndex < ayahs.size
        ) {

            val currentAyah =
                ayahs[firstVisibleIndex]

            preferences.saveLastRead(
                surah = surahNumber,
                verse = currentAyah.verse
            )
        }
    }

    Scaffold(

        topBar = {

            ReaderTopBar(
                surahName =
                    surah?.transliteration
                        ?: "Surah $surahNumber",

                arabicName =
                    surah?.arabicName
                        ?: "",

                onBack =
                    onBack
            )
        },

        bottomBar = {

            ReaderBottomBar(

                // ---------------------------------------------
                // PREVIOUS SURAH
                // ---------------------------------------------

                onPrevious = {

                    if (surahNumber > 1) {

                        onSurahChange(
                            surahNumber - 1
                        )
                    }
                },

                // ---------------------------------------------
                // NEXT SURAH
                // ---------------------------------------------

                onNext = {

                    if (surahNumber < 114) {

                        onSurahChange(
                            surahNumber + 1
                        )
                    }
                },

                // ---------------------------------------------
                // ENABLE / DISABLE BUTTONS
                // ---------------------------------------------

                hasPrevious =
                    surahNumber > 1,

                hasNext =
                    surahNumber < 114
            )
        },

        containerColor =
            MaterialTheme.colorScheme.background

    ) { padding ->

        LazyColumn(

            state = listState,

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding),

            contentPadding =
                PaddingValues(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(24.dp)

        ) {

            items(
                ayahs,
                key = {
                    it.verse
                }
            ) { ayah ->

                AyahItem(

                    ayah =
                        ayah,

                    isFavorite =
                        remember(
                            favoriteRefresh,
                            surahNumber,
                            ayah.verse
                        ) {
                            preferences.isFavorite(
                                surahNumber,
                                ayah.verse
                            )
                        },

                    onToggleFavorite = {

                        preferences.toggleFavorite(
                            surahNumber,
                            ayah.verse
                        )

                        favoriteRefresh++
                    },

                    arabicFontSize =
                        arabicFontSize,

                    translationFontSize =
                        translationFontSize,

                    lineSpacing =
                        lineSpacing,

                    showTransliteration =
                        showTransliteration
                )
            }
        }
    }
}

@Composable
private fun ReaderTopBar(surahName: String, arabicName: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Text(text = "‹", fontSize = 32.sp, color = MaterialTheme.colorScheme.primary)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = surahName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = arabicName, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun AyahItem(
    ayah: com.shuvo.quran.data.Ayah,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    arabicFontSize: androidx.compose.ui.unit.TextUnit,
    translationFontSize: androidx.compose.ui.unit.TextUnit,
    lineSpacing: Float,
    showTransliteration: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = ayah.verse.toString(), color = MaterialTheme.colorScheme.onPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(
                    if (isFavorite)
                        R.drawable.ic_favorites_filled
                    else
                        R.drawable.ic_favorites
                ),

                contentDescription = "Favourite",

                tint = Color.Black,

                modifier =
                    Modifier
                        .size(22.dp)
                        .clickable {
                            onToggleFavorite()
                        }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = ayah.text,
            fontSize = arabicFontSize,
            lineHeight = arabicFontSize * lineSpacing,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground
        )

        if (showTransliteration && ayah.transliteration.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = ayah.transliteration,
                fontSize = translationFontSize,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                lineHeight = translationFontSize * 1.2f
            )
        }

        if (ayah.translation.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ayah.translation,
                fontSize = translationFontSize,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                lineHeight = translationFontSize * 1.4f
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            Modifier,
            DividerDefaults.Thickness,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
        )
    }
}

@Composable
private fun ReaderBottomBar(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    hasPrevious: Boolean,
    hasNext: Boolean
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 12.dp
                    ),

            horizontalArrangement =
                Arrangement.SpaceEvenly,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            BottomActionItem(
                icon = "‹",
                label = "Previous",
                enabled = hasPrevious,
                onClick = onPrevious
            )

            BottomActionItem(
                icon = "›",
                label = "Next",
                enabled = hasNext,
                onClick = onNext
            )
        }
    }
}

@Composable
private fun BottomActionItem(
    icon: String,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val contentColor =
        if (enabled) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.3f
            )
        }

    Column(
        modifier = Modifier
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
            .padding(
                horizontal = 18.dp,
                vertical = 4.dp
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = icon,
            fontSize = 24.sp,
            color = contentColor
        )

        Text(
            text = label,
            fontSize = 10.sp,
            color = contentColor
        )
    }
}
