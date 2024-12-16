package pt.ipca.n26432.news

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SearchNewsScreen(innerPadding: PaddingValues) {
    val newsAPI = NewsAPI("f3452280ca304585b888e6ba23a80328")
    val headlines = remember { mutableStateOf<List<NewsAPI.NewsItem>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val searchQuery = remember { mutableStateOf("") }
    val debounceJob = remember { mutableStateOf<Job?>(null) }
    val firstTime = remember { mutableStateOf(true) }

    LaunchedEffect(searchQuery.value) {
        debounceJob.value?.cancel()
        debounceJob.value = coroutineScope.launch {
            delay(500) // 500ms debounce time
            val news = newsAPI.searchNews(searchQuery.value)
            headlines.value = news
            firstTime.value = false
        }
    }

    Column(modifier = Modifier.padding(innerPadding)) {
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = {
                searchQuery.value = it
                firstTime.value = false },
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
            LazyColumn {
                items(headlines.value) { headline ->
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
                            IconButton(onClick = { /* Handle favorite click */ }) {
                                Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favorite")
                            }
                        }
                    }
                }
            }
        }
    }
}