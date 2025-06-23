package etf.ri.rma.newsfeedapp.data.network

import android.Manifest
import android.util.Log
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService
import etf.ri.rma.newsfeedapp.data.network.api.RetrofitInstance
import etf.ri.rma.newsfeedapp.data.toNewsItem
import etf.ri.rma.newsfeedapp.model.NewsItem
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidUUIDException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresPermission
import etf.ri.rma.newsfeedapp.data.NewsDatabase
import etf.ri.rma.newsfeedapp.data.SavedNewsDAO

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun hasInternetConnection(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    } else {

        @Suppress("DEPRECATION")
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        @Suppress("DEPRECATION")
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}

class NewsDAO ( private val context: Context){
    private val savedNewsDAO: SavedNewsDAO by lazy {
        NewsDatabase.getDatabase(context).savedNewsDAO()
    }
    private var apiService: NewsApiService = RetrofitInstance.api

    fun setApiService(service: NewsApiService) {
        apiService = service
    }

    private val cacheTime = mutableMapOf<String, Long>()

    private val mutex = Mutex()

    private val API_TOKEN = "RltfEUuzmKQxGsNc8sBK4icfoi2b9m6m313O8p3Y"



    fun mapCategoryForApi(category: String): String {
        return when (category) {
            "Politika","politics" -> "politics"
            "Sport","sports" -> "sports"
            "Nauka","science" -> "science"
            "Tehnologija","tech" -> "tech"
            "Biznis" -> "business"
            "Zdravlje" -> "health"
            "Kultura","entertainment" -> "entertainment"
            "Hrana" -> "food"
            "Putovanja" -> "travel"
            "business" -> "business"
            "health" -> "health"
            "food" -> "food"
            "travel" -> "travel"
            "general" -> "general"
            else -> "general"
        }
    }

    private val featuredCache = ConcurrentHashMap<String, List<NewsItem>>()

    suspend fun getTopStoriesByCategory(category: String): List<NewsItem> = withContext(Dispatchers.IO)

     {
        val now = System.currentTimeMillis()
        val mappedCategory = mapCategoryForApi(category)
        val recentCall = cacheTime[mappedCategory]?.let { now - it < 30_000 } ?: false

        if (!hasInternetConnection(context)) {
            Log.d("NewsDAO", "Offline mode: Fetching from DB for category: $mappedCategory")
            return@withContext savedNewsDAO.getNewsWithCategory(mappedCategory)
        }
        if (recentCall) {
            Log.d("NewsDAO", "CACHE HIT: $mappedCategory")
            return@withContext featuredCache[mappedCategory] ?: NewsData.getByCategory(mappedCategory)
        }
         return@withContext try {
             val result =
                 apiService.searchNews(API_TOKEN, mappedCategory).data.map { it.toNewsItem() }
             val top3 = result.take(3)
                 .map { it.copy(news = it.news.copy(category = mappedCategory, isFeatured = true)) }

             mutex.withLock {
                 NewsData.addAllIfNew(top3)
                 cacheTime[mappedCategory] = now
                 featuredCache[mappedCategory] = top3
                 top3.forEach { newsItem ->
                     savedNewsDAO.saveNews(newsItem)
                 }

             }
             top3
         }
             catch (e: Exception) {
                 Log.e("NewsDAO", "Error fetching top stories from API: ${e.message}")
                 savedNewsDAO.getNewsWithCategory(mappedCategory)
             }
    }



    suspend fun getAllStories(): List<NewsItem> = withContext(Dispatchers.IO) {
        if (hasInternetConnection(context)) {
        return@withContext NewsData.getAllNews()} else {
            // vrati iz baze, ako je offline
            Log.d("NewsDAO", "Offline mode: Returning all stories from DB.")
            return@withContext savedNewsDAO.getAllNewsItems()
        }
    }

    suspend fun getSimilarStories(uuid: String): List<NewsItem> = withContext(Dispatchers.IO) {
        try {
            UUID.fromString(uuid)
        } catch (e: IllegalArgumentException) {
            throw InvalidUUIDException("Neispravan UUID format: $uuid")
        }

        if (!hasInternetConnection(context)) {
            val offlineCached = NewsCache.uuidCache[uuid]
            return@withContext offlineCached?.take(2) ?: emptyList()
        }



        return@withContext try {
            val response = apiService.getSimilarStories(uuid = uuid, apiToken = API_TOKEN)
            val fetchedDTOs = response.data
                .filter { it.uuid != uuid }
                .take(2)

            val fetchedNews = fetchedDTOs.map { dto ->
                val resolvedCategory = dto.categories?.firstOrNull() ?: "general"
                dto.toNewsItem().copy(news = dto.toNewsItem().news.copy(category = resolvedCategory, isFeatured = false))
            }

            mutex.withLock {
                NewsCache.uuidCache[uuid] = fetchedNews
                NewsData.addAllIfNew(fetchedNews)
                fetchedNews.forEach { newsItem ->
                    savedNewsDAO.saveNews(newsItem)
                }
            }

            fetchedNews
        } catch (e: Exception) {

            emptyList()
        }
    }
}
