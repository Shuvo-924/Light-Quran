package com.shuvo.quran

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import android.widget.Toast
import com.shuvo.quran.data.QuranRepository
import com.shuvo.quran.data.ReadingPreferences
import com.shuvo.quran.ui.HomeScreen
import com.shuvo.quran.ui.LaunchScreen
import com.shuvo.quran.ui.QuranReaderScreen
import com.shuvo.quran.ui.theme.QuranTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = ReadingPreferences(this)
        val repository = QuranRepository(this, preferences)

        setContent {
            var currentTheme by remember { mutableStateOf(preferences.getTheme()) }
            
            // Periodically check if theme changed (simpler than listeners for this demo)
            LaunchedEffect(Unit) {
                while(true) {
                    val theme = preferences.getTheme()
                    if (theme != currentTheme) {
                        currentTheme = theme
                    }
                    kotlinx.coroutines.delay(500)
                }
            }

            QuranTheme(themeName = currentTheme) {
                var showLaunchScreen by remember { mutableStateOf(true) }
                var selectedSurah by remember { mutableStateOf<Int?>(null) }
                var selectedVerse by remember { mutableStateOf(1) }
                var lastBackPress by remember { mutableStateOf(0L) }

                BackHandler {
                    if (showLaunchScreen) return@BackHandler
                    if (selectedSurah != null) {
                        selectedSurah = null
                        selectedVerse = 1
                        return@BackHandler
                    }

                    val currentTime = SystemClock.elapsedRealtime()
                    if (currentTime - lastBackPress < 2000) {
                        finish()
                    } else {
                        lastBackPress = currentTime
                        Toast.makeText(this@MainActivity, "Press back again to exit", Toast.LENGTH_SHORT).show()
                    }
                }

                if (showLaunchScreen) {
                    LaunchScreen(onFinished = { showLaunchScreen = false })
                } else if (selectedSurah != null) {
                    QuranReaderScreen(
                        surahNumber = selectedSurah!!,

                        startVerse = selectedVerse,

                        repository = repository,

                        preferences = preferences,

                        onBack = {
                            selectedSurah = null
                            selectedVerse = 1
                        },

                        onSurahChange = { newSurah ->

                            selectedSurah = newSurah

                            // New Surah always starts at verse 1
                            selectedVerse = 1
                        }
                    )
                } else {
                    HomeScreen(
                        repository = repository,
                        preferences = preferences,
                        onSurahSelected = { surah, verse ->
                            selectedSurah = surah
                            selectedVerse = verse
                        }
                    )
                }
            }
        }
    }
}
