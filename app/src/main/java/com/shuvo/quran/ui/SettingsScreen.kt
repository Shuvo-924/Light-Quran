package com.shuvo.quran.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shuvo.quran.data.ReadingPreferences
import com.shuvo.quran.data.QuranRepository

@Composable
fun SettingsScreen(
    preferences: ReadingPreferences,
    repository: QuranRepository,
    onBack: () -> Unit
) {
    var currentSubScreen by remember { mutableStateOf("Main") }

    when (currentSubScreen) {
        "Main" -> MainSettings(
            preferences = preferences,
            onNavigate = { currentSubScreen = it },
            onBack = onBack
        )
        "Theme" -> ThemeSettings(
            preferences = preferences,
            onBack = { currentSubScreen = "Main" }
        )
        "Font" -> FontSettings(
            preferences = preferences,
            onBack = { currentSubScreen = "Main" }
        )
        "Language" -> LanguageSettings(
            preferences = preferences,
            repository = repository,
            onBack = { currentSubScreen = "Main" }
        )
    }
}

@Composable
private fun MainSettings(
    preferences: ReadingPreferences,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F5ED))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "‹",
                fontSize = 38.sp,
                color = Color(0xFF234F3E),
                modifier = Modifier.clickable { onBack() }.padding(end = 16.dp)
            )
            Text(
                text = "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF234F3E)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Reading", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF25251F))
        Spacer(modifier = Modifier.height(16.dp))

        SettingItem("Translation Language", preferences.getTranslationLanguage()) { onNavigate("Language") }
        SettingItem("Arabic Font", preferences.getArabicFont()) { onNavigate("Font") }

        SliderSetting("Arabic Font Size", preferences.getArabicFontSize().toFloat(), 16f, 40f) {
            preferences.setArabicFontSize(it.toInt())
        }

        SliderSetting("Translation Font Size", preferences.getTranslationFontSize().toFloat(), 12f, 30f) {
            preferences.setTranslationFontSize(it.toInt())
        }

        SliderSetting("Line Spacing", preferences.getLineSpacing(), 1.0f, 2.5f) {
            preferences.setLineSpacing(it)
        }

        SwitchSetting("Show Transliteration", preferences.isShowTransliteration()) {
            preferences.setShowTransliteration(it)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Appearance", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF25251F))
        Spacer(modifier = Modifier.height(16.dp))

        SettingItem("Theme", preferences.getTheme()) { onNavigate("Theme") }
    }
}

@Composable
private fun ThemeSettings(
    preferences: ReadingPreferences,
    onBack: () -> Unit
) {
    val themes = listOf("Light", "Dark", "Sepia", "Night Blue", "Forest Green")
    val selectedTheme = preferences.getTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F5ED))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "‹",
                fontSize = 38.sp,
                color = Color(0xFF234F3E),
                modifier = Modifier.clickable { onBack() }.padding(end = 16.dp)
            )
            Text(
                text = "Theme",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF234F3E)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        themes.forEach { theme ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { preferences.setTheme(theme) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = (theme == selectedTheme), onClick = { preferences.setTheme(theme) })
                Text(text = theme, fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun FontSettings(
    preferences: ReadingPreferences,
    onBack: () -> Unit
) {
    val fonts = listOf("Uthman Taha", "Scheherazade", "KFGQPC HAFS", "Amiri Quran", "Noore Hira")
    val selectedFont = preferences.getArabicFont()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F5ED))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "‹",
                fontSize = 38.sp,
                color = Color(0xFF234F3E),
                modifier = Modifier.clickable { onBack() }.padding(end = 16.dp)
            )
            Text(
                text = "Arabic Font",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF234F3E)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        fonts.forEach { font ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { preferences.setArabicFont(font) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = (font == selectedFont), onClick = { preferences.setArabicFont(font) })
                Text(text = font, fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun LanguageSettings(
    preferences: ReadingPreferences,
    repository: QuranRepository,
    onBack: () -> Unit
) {
    val languages = listOf(
        "bn" to "Bangla",
        "en" to "English",
        "ar" to "Arabic",
        "fr" to "French",
        "de" to "German",
        "es" to "Spanish",
        "tr" to "Turkish"
    )
    val selectedLang = preferences.getTranslationLanguage()
    var downloading by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F5ED))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "‹",
                fontSize = 38.sp,
                color = Color(0xFF234F3E),
                modifier = Modifier.clickable { onBack() }.padding(end = 16.dp)
            )
            Text(
                text = "Translation Language",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF234F3E)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        languages.forEach { (code, name) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (code == selectedLang),
                    onClick = {
                        preferences.setTranslationLanguage(code)
                        repository.clearCache()
                    }
                )
                Text(text = name, fontSize = 16.sp, modifier = Modifier.weight(1f).padding(start = 8.dp))
                
                if (code != "bn" && code != "en") {
                    Button(
                        onClick = {
                            downloading = code
                            repository.downloadTranslation(code) { success ->
                                downloading = null
                                if (success) {
                                    preferences.setTranslationLanguage(code)
                                    repository.clearCache()
                                }
                            }
                        },
                        enabled = downloading == null,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF234F3E))
                    ) {
                        Text(if (downloading == code) "..." else "Download")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Transliteration Language", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        val transLangs = listOf("bn" to "Bangla", "en" to "English")
        val selectedTransLang = preferences.getTransliterationLanguage()
        
        transLangs.forEach { (code, name) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        preferences.setTransliterationLanguage(code)
                        repository.clearCache()
                    }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (code == selectedTransLang),
                    onClick = { 
                        preferences.setTransliterationLanguage(code)
                        repository.clearCache()
                    }
                )
                Text(text = name, fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun SettingItem(label: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Text(text = value, fontSize = 14.sp, color = Color.Gray)
        Text(text = " ›", fontSize = 18.sp, color = Color.Gray)
    }
}

@Composable
private fun SliderSetting(label: String, value: Float, min: Float, max: Float, onValueChange: (Float) -> Unit) {
    var currentValue by remember(value) { mutableStateOf(value) }
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, fontSize = 16.sp)
            Text(text = currentValue.toInt().toString(), fontSize = 14.sp, color = Color.Gray)
        }
        Slider(
            value = currentValue,
            onValueChange = { 
                currentValue = it
                onValueChange(it)
            },
            valueRange = min..max,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF234F3E),
                activeTrackColor = Color(0xFF234F3E)
            )
        )
    }
}

@Composable
private fun SwitchSetting(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    var isChecked by remember(checked) { mutableStateOf(checked) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Switch(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                onCheckedChange(it)
            },
            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF234F3E), checkedTrackColor = Color(0xFFD8E4DC))
        )
    }
}
