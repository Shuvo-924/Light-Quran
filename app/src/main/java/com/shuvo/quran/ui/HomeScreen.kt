package com.shuvo.quran.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import com.shuvo.quran.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shuvo.quran.data.QuranRepository
import com.shuvo.quran.data.ReadingPreferences
import com.shuvo.quran.data.SearchResult
import com.shuvo.quran.data.Surah

@Composable
fun HomeScreen(
    repository: QuranRepository,
    preferences: ReadingPreferences,
    onSurahSelected: (Int, Int) -> Unit
) {
    var currentTab by remember { mutableStateOf("Home") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F5ED))
    ) {
        Box(modifier = Modifier.weight(1f)) {
            when (currentTab) {
                "Home" -> HomeTab(repository, preferences, onSurahSelected)
                "Favorites" -> FavoritesTab(repository, preferences, onSurahSelected)
                "Search" -> SearchTab(repository, onSurahSelected)
                "Settings" -> SettingsScreen(preferences, repository, onBack = { currentTab = "Home" })
            }
        }

        BottomNavigationBar(
            currentTab = currentTab,
            onTabSelected = { currentTab = it }
        )
    }
}

@Composable
private fun HomeTab(
    repository: QuranRepository,
    preferences: ReadingPreferences,
    onSurahSelected: (Int, Int) -> Unit
) {
    var surahs by remember { mutableStateOf<List<Surah>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchOpen by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<SearchResult>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            surahs = repository.getSurahs()
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
        }
    }

    LaunchedEffect(searchText) {
        if (searchText.isNotBlank()) {
            searchResults = repository.search(searchText)
        } else {
            searchResults = emptyList()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar with Title and Search
        TopBar(
            title = "Quran",
            searchOpen = searchOpen,
            searchText = searchText,
            onSearchToggle = { 
                searchOpen = !searchOpen
                if (!searchOpen) searchText = ""
            },
            onSearchTextChange = { searchText = it }
        )

        if (searchOpen && searchText.isNotBlank()) {
            SearchResults(results = searchResults, onResultClick = { s, v -> onSurahSelected(s, v) })
        } else if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF234F3E))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(text = "Assalamu Alaikum", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(text = "What would you like to read today?", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                val lastRead = preferences.getLastRead()
                if (lastRead != null) {
                    item {
                        ContinueReadingCard(
                            surahNumber = lastRead.first,
                            verseNumber = lastRead.second,
                            repository = repository,
                            onClick = { onSurahSelected(lastRead.first, lastRead.second) }
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Surahs", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(text = "View All", fontSize = 14.sp, color = Color(0xFF234F3E), fontWeight = FontWeight.SemiBold)
                    }
                }

                items(surahs) { surah ->
                    SurahItem(surah = surah, onClick = { onSurahSelected(surah.number, 1) })
                }
            }
        }
    }
}

@Composable
private fun FavoritesTab(
    repository: QuranRepository,
    preferences: ReadingPreferences,
    onSurahSelected: (Int, Int) -> Unit
) {
    var favoritesRefresh by remember { mutableStateOf(0) }
    val favorites = remember(favoritesRefresh) {
        preferences.getFavorites().mapNotNull { key ->
            val parts = key.split(":")
            if (parts.size != 2) return@mapNotNull null
            val s = parts[0].toIntOrNull() ?: return@mapNotNull null
            val v = parts[1].toIntOrNull() ?: return@mapNotNull null
            val ayah = repository.getSurah(s).firstOrNull { it.verse == v } ?: return@mapNotNull null
            FavoriteVerse(s, v, ayah.text, ayah.translation)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Favorites", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF234F3E))
        Spacer(modifier = Modifier.height(16.dp))

        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No favorites added yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(favorites) { favorite ->
                    FavoriteVerseItem(
                        favorite = favorite,
                        onClick = { onSurahSelected(favorite.surahNumber, favorite.verseNumber) },
                        onRemove = {
                            preferences.toggleFavorite(favorite.surahNumber, favorite.verseNumber)
                            favoritesRefresh++
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchTab(
    repository: QuranRepository,
    onSurahSelected: (Int, Int) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<SearchResult>>(emptyList()) }

    LaunchedEffect(searchText) {
        if (searchText.isNotBlank()) {
            searchResults = repository.search(searchText)
        } else {
            searchResults = emptyList()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Search", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF234F3E))
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Search Ayah or Surah...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
        SearchResults(results = searchResults, onResultClick = onSurahSelected)
    }
}

@Composable
private fun TopBar(
    title: String,
    searchOpen: Boolean,
    searchText: String,
    onSearchToggle: () -> Unit,
    onSearchTextChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (!searchOpen) {

            Text(
                text = title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF234F3E),
                modifier = Modifier.weight(1f)
            )

        } else {

            TextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                placeholder = {
                    Text("Search...")
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        // Search / Close button
        if (searchOpen) {

            Text(
                text = "×",
                fontSize = 30.sp,
                color = Color(0xFF234F3E),
                modifier = Modifier
                    .clickable {
                        onSearchToggle()
                    }
                    .padding(
                        start = 12.dp
                    )
            )

        } else {

            Icon(
                painter = painterResource(
                    id = R.drawable.ic_search
                ),
                contentDescription = "Search",
                tint = Color(0xFF234F3E),
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        onSearchToggle()
                    }
                    .padding(2.dp)
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    val tabs = listOf(
        "Home" to R.drawable.ic_home,
        "Favorites" to R.drawable.ic_favorites,
        "Search" to R.drawable.ic_search,
        "Settings" to R.drawable.ic_settings
    )

    val selectedIndex = tabs.indexOfFirst {
        it.first == currentTab
    }.coerceAtLeast(0)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            )
    ) {

        val itemWidth = maxWidth / 4
        val itemHeight = itemWidth * 3/4

        val targetOffset =
            itemWidth * selectedIndex

        val animatedOffset by animateDpAsState(
            targetValue = targetOffset,
            animationSpec = tween(
                durationMillis = 300
            ),
            label = "bottomNavigationIndicator"
        )

        // -------------------------------------------------
        // SLIDING SELECTION BACKGROUND
        // -------------------------------------------------

        Box(
            modifier = Modifier
                .offset(x = animatedOffset)
                .width(itemWidth)
                .height(itemHeight)
                .padding(
                    horizontal = 5.dp,
                    vertical = 0.5.dp
                )
                .background(
                    color = Color(0xFFE5EFEA),
                    shape = RoundedCornerShape(16.dp)
                )
        )

        // -------------------------------------------------
        // NAVIGATION ITEMS
        // -------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            tabs.forEach { (label, icon) ->

                BottomNavItem(
                    icon = icon,
                    label = label,
                    selected = currentTab == label,
                    modifier = Modifier.width(itemWidth),
                    onClick = {
                        onTabSelected(label)
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: Int,
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    Column(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(
                vertical = 7.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            painter =
                painterResource(
                    id = icon
                ),

            contentDescription =
                label,

            tint =
                Color.Black.copy(
                    alpha =
                        if (selected)
                            1f
                        else
                            0.55f
                ),

            modifier =
                Modifier.size(22.dp)
        )

        Text(
            text = label,

            fontSize = 11.sp,

            fontWeight =
                if (selected)
                    FontWeight.SemiBold
                else
                    FontWeight.Normal,

            color = Color.Black,

            modifier =
                Modifier.padding(
                    top = 3.dp
                )
        )
    }
}

// Reuse existing components (SurahItem, ContinueReadingCard, SearchResults, FavoriteVerseItem)
@Composable
private fun SurahItem(surah: Surah, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = surah.number.toString(), fontWeight = FontWeight.Bold, color = Color(0xFF234F3E), modifier = Modifier.width(32.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = surah.transliteration, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = surah.arabicName, fontSize = 14.sp, color = Color.Gray)
        }
        Text(text = surah.verses.size.toString(), fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun ContinueReadingCard(surahNumber: Int, verseNumber: Int, repository: QuranRepository, onClick: () -> Unit) {
    val surah = remember(surahNumber) { repository.getSurahInfo(surahNumber) }
    Column(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF234F3E), RoundedCornerShape(16.dp)).clickable { onClick() }.padding(20.dp)
    ) {
        Text(text = "Continue Reading", fontSize = 14.sp, color = Color(0xFFD8E4DC))
        Text(text = surah?.transliteration ?: "Surah $surahNumber", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = "Ayah $verseNumber", fontSize = 14.sp, color = Color(0xFFD8E4DC))
    }
}

@Composable
private fun SearchResults(results: List<SearchResult>, onResultClick: (Int, Int) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(results) { result ->
            when (result) {
                is SearchResult.SurahResult -> {
                    Column(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).clickable { onResultClick(result.surahNumber, 1) }.padding(16.dp)) {
                        Text(text = result.name, fontWeight = FontWeight.Bold)
                        Text(text = result.arabicName, color = Color.Gray)
                    }
                }
                is SearchResult.AyahResult -> {
                    Column(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).clickable { onResultClick(result.surahNumber, result.verseNumber) }.padding(16.dp)) {
                        Text(text = "Surah ${result.surahNumber}:${result.verseNumber}", fontWeight = FontWeight.Bold, color = Color(0xFF234F3E))
                        Text(text = result.text, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteVerseItem(favorite: FavoriteVerse, onClick: () -> Unit, onRemove: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).clickable { onClick() }.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Surah ${favorite.surahNumber}:${favorite.verseNumber}", fontWeight = FontWeight.Bold, color = Color(0xFF234F3E), modifier = Modifier.weight(1f))
            IconButton(onClick = onRemove) { Text("♥", color = Color.Red, fontSize = 20.sp) }
        }
        Text(text = favorite.arabic, fontSize = 22.sp, textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth())
        if (favorite.translation.isNotBlank()) {
            Text(text = favorite.translation, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

private data class FavoriteVerse(val surahNumber: Int, val verseNumber: Int, val arabic: String, val translation: String)
