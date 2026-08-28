package com.shuvo.quran.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shuvo.quran.data.Ayah
import com.shuvo.quran.data.QuranRepository
import com.shuvo.quran.data.ReadingPreferences

@Composable
fun QuranScreen() {

    val context = LocalContext.current

    val preferences = remember {
        ReadingPreferences(context)
    }

    val repository = remember {
        QuranRepository(context, preferences)
    }

    var ayahs by remember {
        mutableStateOf<List<Ayah>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {

        try {

            ayahs = repository.getSurah(1)

        } catch (e: Exception) {

            error = "${e.javaClass.simpleName}: ${e.message}"

        } finally {

            loading = false
        }
    }

    MaterialTheme {

        when {

            loading -> {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    CircularProgressIndicator()

                    Text(
                        text = "Loading Quran...",
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }

            error != null -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "Unable to load Quran",
                        fontSize = 22.sp
                    )

                    Text(
                        text = error!!,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }

            else -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {

                    Text(
                        text = "القرآن الكريم",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 28.sp
                    )

                    Text(
                        text = "الفاتحة",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 8.dp,
                                bottom = 20.dp
                            ),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )

                    LazyColumn {

                        items(ayahs) { ayah ->

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp)
                            ) {

                                Text(
                                    text = ayah.text,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Right,
                                    fontSize = 28.sp,
                                    lineHeight = 48.sp
                                )

                                Text(
                                    text = "﴿${ayah.verse}﴾",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Right,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}