// etf.ri.rma.newsfeedapp.data/ImagaDAO.kt
package etf.ri.rma.newsfeedapp.data.network

import etf.ri.rma.newsfeedapp.data.ImageResponse
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.data.network.api.ImagaApiService
import etf.ri.rma.newsfeedapp.data.network.api.ImageRetrofit
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidImageURLException
import java.net.URL


class ImagaDAO {
    private val tagCache = mutableMapOf<String, List<String>>() // Cache tagova
    private var apiService: ImagaApiService = ImageRetrofit.api // Initialize apiService as null

    // The setApiService method will allow setting the apiService externally
    fun setApiService(apiService: ImagaApiService) {
        this.apiService = apiService
    }

    fun isValidUrl(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://")
    }

    suspend fun getTags(imageUrl: String): List<String> {
        if (!isValidUrl(imageUrl)) {
            throw InvalidImageURLException("Izuzetak u url: $imageUrl")
        }
        if (tagCache.containsKey(imageUrl)) return tagCache[imageUrl] ?: emptyList()

        val response = apiService.getImageTags(imageUrl,"acc_b5c20d5f670ac3d")
        val tags = response.result?.tags?.mapNotNull { it.tag?.en }?.take(10)  ?: emptyList()

        tagCache[imageUrl] = tags


        NewsData.updateTagsForImageUrl(imageUrl, tags)

        return tags
    }
}