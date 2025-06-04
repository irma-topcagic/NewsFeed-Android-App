package etf.ri.rma.newsfeedapp.data.network

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

class NewsDAO {

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

    suspend fun getTopStoriesByCategory(category: String): List<NewsItem> = withContext(Dispatchers.IO){
        val now = System.currentTimeMillis()
        val mappedCategory = mapCategoryForApi(category)
        val recentCall = cacheTime[mappedCategory]?.let { now - it < 30_000 } ?: false
        Log.d("NewsDAO", " API call for category: $mappedCategory")
        if (recentCall) {
            Log.d("NewsDAO", "CACHE HIT: $mappedCategory")
            return@withContext featuredCache[mappedCategory] ?: NewsData.getByCategory(mappedCategory)
        }

        val result = apiService.searchNews(API_TOKEN, mappedCategory).data.map { it.toNewsItem() }
        val top3 = result.take(3).map { it.copy(category = mappedCategory, isFeatured = true) }

        mutex.withLock {
            NewsData.addAllIfNew(top3)
            cacheTime[mappedCategory] = now
            featuredCache[mappedCategory] = top3

        }

        return@withContext top3
    }



    fun getAllStories(): List<NewsItem> {  return NewsData.getAllNews() }

    suspend fun getSimilarStories(uuid: String): List<NewsItem> = withContext(Dispatchers.IO) {
        try {
            UUID.fromString(uuid)
        } catch (e: IllegalArgumentException) {
            throw InvalidUUIDException("Neispravan UUID format: $uuid")
        }

        val cachedSimilarStories = NewsCache.uuidCache[uuid]
        if (cachedSimilarStories != null) {
            if (cachedSimilarStories.isNotEmpty()) {
                return@withContext cachedSimilarStories
            }
        }

        return@withContext try {
            val response = apiService.getSimilarStories(uuid = uuid, apiToken = API_TOKEN)
            val fetchedDTOs = response.data
                .filter { it.uuid != uuid }
                .take(2)

            val fetchedNews = fetchedDTOs.map { dto ->
                val resolvedCategory = dto.categories?.firstOrNull() ?: "general"
                dto.toNewsItem().copy(category = resolvedCategory, isFeatured = false)
            }

            mutex.withLock {
                NewsCache.uuidCache[uuid] = fetchedNews
                NewsData.addAllIfNew(fetchedNews)
            }

            fetchedNews
        } catch (e: Exception) {

            emptyList()
        }
    }
}
