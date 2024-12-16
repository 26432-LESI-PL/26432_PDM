package pt.ipca.n26432.news

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class NewsAPI(private val apiKey: String) {
    private val baseUrl = "https://newsapi.org/v2"

    suspend fun getTopHeadlines(country: String): List<NewsItem> {
        return withContext(Dispatchers.IO) {
            val urlString = "$baseUrl/top-headlines?country=$country&apiKey=$apiKey"
            Log.d("NewsAPI", "Request URL: $urlString")
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")

            try {
                val responseCode = connection.responseCode
                Log.d("NewsAPI", "Response Code: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()
                    Log.d("NewsAPI", response)
                    parseHeadlines(response)
                } else {
                    Log.e("NewsAPI", "Error fetching top headlines: $responseCode")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("NewsAPI", "Error fetching top headlines", e)
                e.printStackTrace()
                emptyList()
            } finally {
                connection.disconnect()
            }
        }
    }

    // Latest news from the API
    suspend fun searchNews(query: String = "apple"): List<NewsItem> {
        return withContext(Dispatchers.IO) {
            val urlString = "$baseUrl/everything?apiKey=$apiKey&q=$query"
            Log.d("NewsAPI", "Request URL: $urlString")
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")

            try {
                val responseCode = connection.responseCode
                Log.d("NewsAPI", "Response Code: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()
                    Log.d("NewsAPI", response)
                    parseHeadlines(response)
                } else {
                    Log.e("NewsAPI", "Error fetching top headlines: $responseCode")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("NewsAPI", "Error fetching top headlines", e)
                e.printStackTrace()
                emptyList()
            } finally {
                connection.disconnect()
            }
        }
    }
    // Parse the JSON response from the API

    data class NewsItem(val title: String, val imageUrl: String, val journal: String, val url: String) {

    }

    private fun parseHeadlines(jsonResponse: String): List<NewsItem> {
        val headlines = mutableListOf<NewsItem>()
        val jsonObject = JSONObject(jsonResponse)
        val articles = jsonObject.getJSONArray("articles")
        for (i in 0 until articles.length()) {
            val article = articles.getJSONObject(i)
            val title = article.getString("title")
            var imageUrl = article.getString("urlToImage")
            Log.d("NewsAPI", "imageUrl: $imageUrl")
            if (imageUrl == "null" || imageUrl.isEmpty()) {
                imageUrl = "https://via.placeholder.com/150"
            }
            var journal = article.getString("author")
            if (journal == "null" || journal.isEmpty()) {
                journal = "Unknown"
            }
            val url = article.getString("url")
            if (title == "[Removed]") {
                continue
            }
            headlines.add(NewsItem(title, imageUrl, journal, url))
        }
        return headlines
    }
}