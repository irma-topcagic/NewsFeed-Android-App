package etf.ri.rma.newsfeedapp.data.network

import android.util.Log
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService
import etf.ri.rma.newsfeedapp.data.network.api.RetrofitInstance
import etf.ri.rma.newsfeedapp.data.toNewsItem
import etf.ri.rma.newsfeedapp.model.NewsItem
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.Collections
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidUUIDException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class NewsDAO {

    private var apiService: NewsApiService = RetrofitInstance.api

    fun setApiService(service: NewsApiService) {
        apiService = service
    }

    private val cacheTime = mutableMapOf<String, Long>()
    private val uuidCache = mutableMapOf<String, List<NewsItem>>()
    private val mutex = Mutex()

    private val API_TOKEN = "RltfEUuzmKQxGsNc8sBK4icfoi2b9m6m313O8p3Y"



     fun mapCategoryForApi(category: String): String {
        return when (category) {
            "Politika","politics" -> "politics"
            "Sport","sports" -> "sports"
            "Nauka/tehnologija","science" -> "science"
            "Biznis" -> "business"
            "Zdravlje" -> "health"
            "Kultura","entertainment" -> "entertainment"
            "Hrana" -> "food"
            "Putovanja" -> "travel"
            "tech" -> "science"
            "business" -> "business"
            "health" -> "health"
            "food" -> "food"
            "travel" -> "travel"
            "general" -> "general"
            else -> "general"
        }
    }

    private val featuredCache = ConcurrentHashMap<String, List<NewsItem>>()

    suspend fun getTopStoriesByCategory(category: String): List<NewsItem> {
        val now = System.currentTimeMillis()
        val mappedCategory = mapCategoryForApi(category)
        val recentCall = cacheTime[mappedCategory]?.let { now - it < 30_000 } ?: false
        Log.d("NewsDAO", "CACHE MISS/EXPIRATION: Making API call for category: $mappedCategory")
        if (recentCall) {
            Log.d("NewsDAO", "CACHE HIT: Returning cached featured news for category: $mappedCategory")
            return featuredCache[mappedCategory] ?: NewsData.getByCategory(mappedCategory)
        }

        val result = apiService.searchNews(API_TOKEN, mappedCategory).data.map { it.toNewsItem() }
       val top3 = result.take(3).map { it.copy(category = mappedCategory, isFeatured = true) }

        mutex.withLock {
            NewsData.addAllIfNew(top3)
            cacheTime[mappedCategory] = now
            featuredCache[mappedCategory] = top3
            Log.d("NewsDAO", "API CALL SUCCESS: Fetched and cached new top 3 stories for category: $mappedCategory")
        }

        return top3
    }


    fun getAllStories(): List<NewsItem> {  return NewsData.getAllNews() }

    suspend fun getSimilarStories(uuid: String): List<NewsItem> {
        try {
            UUID.fromString(uuid)
        } catch (e: IllegalArgumentException) {
            Log.e("NewsDAO", "UUID nije validan: $uuid")
            throw InvalidUUIDException("Invalid UUID format: $uuid")
        }

        uuidCache[uuid]?.let {

            return it
        }

        return try {
            val response = apiService.getSimilarStories(uuid,API_TOKEN)


            val fetched = response.data
                .map { it.toNewsItem().copy(isFeatured = false) }
                .filter { it.uuid != uuid }
                .take(2)

            Log.d("NewsDAO", "Nakon filtracije ostaje: ${fetched.size}")

            val newItems = fetched.filter { f -> NewsData.getAllNews().none { it.uuid == f.uuid } }

            mutex.withLock {
                uuidCache[uuid] = fetched
                NewsData.addAllIfNew(newItems)
                Log.d("NewsDAO", "Dodano u cache: ${newItems.size} novih vijesti")
            }

            fetched
        } catch (e: Exception) {
            Log.e("NewsDAO", "Greška u API pozivu za $uuid: ${e.message}", e)
            emptyList()
        }
    }
}
