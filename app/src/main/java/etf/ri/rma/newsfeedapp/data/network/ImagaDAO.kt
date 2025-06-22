package etf.ri.rma.newsfeedapp.data.network

import android.content.Context
import android.util.Log
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.data.NewsDatabase
import etf.ri.rma.newsfeedapp.data.SavedNewsDAO
import etf.ri.rma.newsfeedapp.data.network.api.ImagaApiService
import etf.ri.rma.newsfeedapp.data.network.api.ImageRetrofit
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidImageURLException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock



class ImagaDAO(private val context: Context) {
    private val savedNewsDAO: SavedNewsDAO by lazy {
        NewsDatabase.getDatabase(context).savedNewsDAO()
    }
    private val mutex = Mutex()

    private var apiService: ImagaApiService = ImageRetrofit.api
    fun setApiService(apiService: ImagaApiService) {
        this.apiService = apiService
    }

    private fun isValidUrl(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://")
    }

    suspend fun getTags(imageUrl: String): List<String> {
        if (!isValidUrl(imageUrl)) {
            throw InvalidImageURLException("Izuzetak u url: $imageUrl")
        }

        val newsId = savedNewsDAO.getNewsIdByImageUrl(imageUrl)
            ?: return emptyList()

        val existingTags = savedNewsDAO.getTags(newsId.toInt())
        if (existingTags.isNotEmpty()) {
            return existingTags
        }

        val response = apiService.getImageTags(imageUrl,"acc_b5c20d5f670ac3d")
        val tags = response.result?.tags?.mapNotNull { it.tag?.en }?.take(10)  ?: emptyList()

        mutex.withLock {

            NewsCache.tagCache[imageUrl] = tags
            NewsData.updateTagsForImageUrl(imageUrl, tags)
            savedNewsDAO.addTags(tags, newsId.toInt())
        }

        return tags
    }
}