package pt.ipca.n26432.news.screen

import android.content.Intent
import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun FavoriteScreen(innerPadding: PaddingValues) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val favorites = remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            coroutineScope.launch {
                firestore.collection("favorites")
                    .whereEqualTo("userId", currentUser.uid)
                    .get()
                    .addOnSuccessListener { documents ->
                        val favoriteList = documents.map { it.data }
                        favorites.value = favoriteList
                    }
            }
        }
    }

    LazyColumn(modifier = Modifier.padding(innerPadding)) {
        items(favorites.value) { favorite ->
            Card(modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable {
                    val url = favorite["url"] as String
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Box(modifier = Modifier
                        .size(100.dp)
                        .padding(end = 16.dp)) {
                        val imageUrl = favorite["image"] as String
                        val painter = rememberAsyncImagePainter(model = Base64.decode(imageUrl, Base64.DEFAULT).toString())
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
                        Text(text = favorite["name"] as String, style = MaterialTheme.typography.titleMedium)
                        Text(text = favorite["author"] as String, style = MaterialTheme.typography.titleSmall)
                    }
                }
            }
        }
    }
}