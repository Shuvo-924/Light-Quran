# Implementation Plan - Quran App UI and Feature Update

Update the Quran app to match the provided reference images, add comprehensive settings, support Bangla as the default language, and allow downloading other translations.

## User Review Required

> [!IMPORTANT]
> - **Theme Implementation**: I will add custom color schemes for "Sepia", "Night Blue", and "Forest Green" in addition to standard Light/Dark modes.
> - **Downloadable Translations**: I will implement a basic download mechanism for translations from the provided GitHub repository. This will require internet access in the app.
> - **Data Structure Changes**: I will modify `QuranRepository` to handle different JSON formats for translations and transliterations (Object vs Array).

## Proposed Changes

### [Data Layer]

#### [MODIFY] [ReadingPreferences.kt](file:///C:/Users/USER/AndroidStudioProjects/Quran/app/src/main/java/com/shuvo/quran/data/ReadingPreferences.kt)
- Add new settings: `translationLanguage`, `arabicFont`, `arabicFontSize`, `translationFontSize`, `lineSpacing`, `showTransliteration`, `showTafsir`, `theme`.
- Add methods to get and set these preferences.

#### [MODIFY] [QuranRepository.kt](file:///C:/Users/USER/AndroidStudioProjects/Quran/app/src/main/java/com/shuvo/quran/data/QuranRepository.kt)
- Update `loadQuran` to dynamically load translations and transliterations based on `ReadingPreferences`.
- Add support for parsing `JSONArray` based transliteration files (like `quran_transliteration_bn.json`).
- Implement a method to download and save new translations from GitHub.

---

### [UI Components]

#### [NEW] [SettingsScreen.kt](file:///C:/Users/USER/AndroidStudioProjects/Quran/app/src/main/java/com/shuvo/quran/ui/SettingsScreen.kt)
- Create the Reading Settings screen (Image 6).
- Create the Theme Options screen (Image 7).
- Create the Font Options screen (Image 8).
- Include sliders for font sizes and line spacing.
- Include toggles for transliteration and tafsir.

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/USER/AndroidStudioProjects/Quran/app/src/main/java/com/shuvo/quran/ui/HomeScreen.kt)
- Update bottom navigation to include: Home, Favorites, Search, Settings.
- Favorites will replace the current "Bookmarks" spot as requested.
- Remove search bar logic from top bar if it's now a separate tab in bottom nav (Images suggest Search is a tab).
- Search bar should stay where it is *if* it's on the home screen, but images show a "Search" tab. I will follow the user's specific instruction: "The search bar should stay where it is currently".

#### [MODIFY] [QuranReaderScreen.kt](file:///C:/Users/USER/AndroidStudioProjects/Quran/app/src/main/java/com/shuvo/quran/ui/QuranReaderScreen.kt)
- Apply settings: Arabic font, font sizes, line spacing.
- Implement toggles for Transliteration and Tafsir visibility.
- Apply theme-specific background and text colors.

---

### [Theming]

#### [MODIFY] [Color.kt](file:///C:/Users/USER/AndroidStudioProjects/Quran/app/src/main/java/com/shuvo/quran/ui/theme/Color.kt)
- Define colors for Sepia, Night Blue, and Forest Green themes.

#### [MODIFY] [Theme.kt](file:///C:/Users/USER/AndroidStudioProjects/Quran/app/src/main/java/com/shuvo/quran/ui/theme/Theme.kt)
- Update `QuranTheme` to accept the new theme types and apply corresponding color schemes.

---

### [Main Activity]

#### [MODIFY] [MainActivity.kt](file:///C:/Users/USER/AndroidStudioProjects/Quran/app/src/main/java/com/shuvo/quran/MainActivity.kt)
- Update state management to handle bottom navigation between Home, Favorites, Search, and Settings.

## Verification Plan

### Automated Tests
- No specific automated tests planned, but will verify build and run.

### Manual Verification
- Deploy to emulator/device.
- Verify Home Screen bottom nav works correctly.
- Verify Settings can be changed and are persisted.
- Verify Quran Reader reflects the changes (font size, spacing, theme).
- Verify Bangla is the default translation and transliteration.
- Test downloading a new translation (if possible in emulator environment).
