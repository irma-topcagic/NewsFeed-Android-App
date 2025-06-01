package etf.ri.rma.newsfeedapp.data.network

import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService
import etf.ri.rma.newsfeedapp.data.network.api.RetrofitInstance
import etf.ri.rma.newsfeedapp.data.toNewsItem
import etf.ri.rma.newsfeedapp.model.NewsItem
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.Collections
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidUUIDException

class NewsDAO {

    private var apiService: NewsApiService = RetrofitInstance.api

    fun setApiService(service: NewsApiService) {
        apiService = service
    }

    private val allStoriesMap: ConcurrentHashMap<String, NewsItem> = ConcurrentHashMap()
    private val _allStoriesList: MutableList<NewsItem> = Collections.synchronizedList(mutableListOf())
    private val allStoriesList: List<NewsItem> get() = _allStoriesList.toList()
    private val lastFetch: ConcurrentHashMap<String, Long> = ConcurrentHashMap()

    private val API_TOKEN = "eNOHHXF1gTSvpJM78iDBK7If6VS6TJaEF6k6NLdq"

    init {
        if (_allStoriesList.isEmpty()) {
            val initial = NewsData.getAllNews()
            initial.forEach {
                allStoriesMap[it.uuid] = it
                _allStoriesList.add(it)
            }
        }
    }

    private fun mapCategoryForApi(category: String): String {
        return when (category) {
            "Politika" -> "politics"
            "Sport" -> "sports"
            "Nauka/tehnologija" -> "science"
            "Biznis" -> "business"
            "Zdravlje" -> "health"
            "Zabava" -> "entertainment"
            "Hrana" -> "food"
            "Putovanja" -> "travel"
            "Kultura" -> "general"
            "science", "tech" -> "science"
            "sports" -> "sports"
            "business" -> "business"
            "health" -> "health"
            "entertainment" -> "entertainment"
            "food" -> "food"
            "travel" -> "travel"
            "general" -> "general"
            else -> "general"
        }
    }

    suspend fun getTopStoriesByCategory(category: String): List<NewsItem> {
        val currentTime = System.currentTimeMillis()
        val apiCategory = mapCategoryForApi(category)
        val lastFetchTime = lastFetch[apiCategory] ?: 0L

        val cachedNewsForCategory = _allStoriesList
            .filter { mapCategoryForApi(it.category) == apiCategory }
            .distinctBy { it.uuid }

        // Ako je poziv bio unutar 30 sekundi — vrati keširane vijesti (ne kao featured)
        if (currentTime - lastFetchTime < 30_000L) {
            return cachedNewsForCategory.map { it.copy(isFeatured = false) }
        }

        return try {
            val response = apiService.searchNews(apiToken = API_TOKEN, category = category)
            val fetchedNews = response.data.map { it.toNewsItem() }

            val newFeaturedNews = mutableListOf<NewsItem>()

            for (news in fetchedNews) {
                val existing = allStoriesMap[news.uuid]
                if (existing != null) {
                    // Vijest postoji, samo je označi kao featured
                    val updated = existing.copy(isFeatured = true)
                    newFeaturedNews.add(updated)
                } else {
                    // Nova vijest, dodaj je i označi kao featured
                    val featured = news.copy(isFeatured = true)
                    allStoriesMap[featured.uuid] = featured
                    _allStoriesList.add(featured)
                    newFeaturedNews.add(featured)
                }

                // Samo 3 featured vijesti
                if (newFeaturedNews.size == 3) break
            }

            // Ažuriraj vrijeme poziva
            lastFetch[apiCategory] = currentTime

            // Vrati samo 3 featured vijesti
            return newFeaturedNews
        } catch (e: Exception) {
            println("API error: ${e.message}")
            // Ako je došlo do greške, vrati keširane vijesti
            return cachedNewsForCategory.map { it.copy(isFeatured = false) }
        }
    }


    fun getAllStories(): List<NewsItem> {
        return allStoriesList.map { it.copy(isFeatured = false) }.toList()
    }

    suspend fun getSimilarStories(uuid: String): List<NewsItem> {
        try {
            UUID.fromString(uuid)
        } catch (e: IllegalArgumentException) {
            throw InvalidUUIDException("Invalid UUID format: $uuid")
        }

        return try {
            val response = apiService.getSimilarStories(apiToken = API_TOKEN, uuid = uuid)
            val fetchedNews = response.data.map { it.toNewsItem() }

            val result = mutableListOf<NewsItem>()

            for (news in fetchedNews) {
                if (!allStoriesMap.containsKey(news.uuid)) {
                    allStoriesMap[news.uuid] = news
                    _allStoriesList.add(news)
                }
                result.add(news)
                if (result.size == 2) break
            }

            return result
        } catch (e: Exception) {
            println("API error: ${e.message}")
            return emptyList()
        }
    }
}
