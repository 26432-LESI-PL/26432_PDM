package pt.ipca.n26432.news.screen

import android.content.Intent
import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.ipca.n26432.news.NewsAPI

@Composable
fun SearchNewsScreen(innerPadding: PaddingValues) {
    val newsAPI = NewsAPI("f3452280ca304585b888e6ba23a80328")
    val headlines = remember { mutableStateOf<List<NewsAPI.NewsItem>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val searchQuery = remember { mutableStateOf("") }
    val debounceJob = remember { mutableStateOf<Job?>(null) }
    val firstTime = remember { mutableStateOf(true) }
    val currentPage = remember { mutableIntStateOf(1) }
    val loading = remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    LaunchedEffect(searchQuery.value) {
        debounceJob.value?.cancel()
        debounceJob.value = coroutineScope.launch {
            delay(500) // 500ms debounce time
            if (searchQuery.value.isEmpty()) {
                headlines.value = emptyList()
                return@launch
            }
            loading.value = true
            val news = newsAPI.searchNews(searchQuery.value, currentPage.intValue)
            headlines.value = news
            firstTime.value = false
            loading.value = false
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex == headlines.value.size - 1 && !loading.value) {
                    coroutineScope.launch {
                        loading.value = true
                        currentPage.intValue += 1
                        val moreNews = newsAPI.searchNews(searchQuery.value, currentPage.intValue)
                        headlines.value += moreNews
                        loading.value = false
                    }
                }
            }
    }

    Column(modifier = Modifier.padding(innerPadding)) {
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = {
                searchQuery.value = it
                firstTime.value = false
                currentPage.intValue = 1
                headlines.value = emptyList()
            },
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )

        if (headlines.value.isEmpty() && !firstTime.value) {
            Text(
                text = "No news found",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(state = listState) {
                items(headlines.value) { headline ->
                    val isFavorite = remember { mutableStateOf(false) }

                    Card(modifier = Modifier.padding(vertical = 8.dp), onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(headline.url))
                        context.startActivity(intent)
                    }) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Box(modifier = Modifier
                                .size(100.dp)
                                .padding(end = 16.dp)) {
                                val painter = rememberAsyncImagePainter(model = headline.imageUrl)
                                val painterState by painter.state.collectAsState()
                                Image(
                                    painter = painter,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                                if (painterState is AsyncImagePainter.State.Loading) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = headline.title, style = MaterialTheme.typography.titleMedium)
                                Text(text = headline.journal, style = MaterialTheme.typography.titleSmall)
                            }
                            IconButton(onClick = {
                                if (currentUser == null) {
                                    return@IconButton
                                }
                                isFavorite.value = true
                                val favorite = hashMapOf(
                                    "name" to headline.title,
                                    "author" to headline.journal,
                                    "image" to Base64.encodeToString(headline.imageUrl.toByteArray(), Base64.DEFAULT),
                                    "url" to headline.url,
                                    "userId" to (currentUser.uid),
                                )
                                firestore.collection("favorites").add(favorite)
                            },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = if (isFavorite.value) Color.Red else Color.White
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favorite")
                            }
                        }
                    }
                }
                item {
                    if (loading.value) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp))
                    }
                }
            }
        }
    }
}