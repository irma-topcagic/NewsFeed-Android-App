// etf.ri.rma.newsfeedapp.data/ImagaDAO.kt
package etf.ri.rma.newsfeedapp.data.network

import etf.ri.rma.newsfeedapp.data.network.api.ImagaApiService
import etf.ri.rma.newsfeedapp.data.network.api.ImageRetrofit
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService
import etf.ri.rma.newsfeedapp.data.network.api.RetrofitInstance
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidImageURLException



class ImagaDAO {
    private val tagCache = mutableMapOf<String, List<String>>() // Cache tagova
    private var apiService: ImagaApiService= ImageRetrofit.api // Initialize apiService as null

    // The setApiService method will allow setting the apiService externally
    fun setApiService(apiService: ImagaApiService) {
        this.apiService = apiService
    }
    fun isValidUrl(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://")
    }

    suspend fun getTags(imageUrl: String): List<String> {
        if (!isValidUrl(imageUrl)) {
            throw InvalidImageURLException("Invalid image URL: $imageUrl")
        }

        tagCache[imageUrl]?.let {
            return it
        }

        val tags = try {
            val response = apiService?.getImageTags(imageUrl, "acc_b5c20d5f670ac3d")
                ?: throw InvalidImageURLException("API service not initialized")
            response.result?.tags?.mapNotNull { it.tag?.en } ?: emptyList()
        } catch (e: Exception) {
            throw InvalidImageURLException("Error during API call: ${e.message}")
        }
        tagCache[imageUrl] = tags
        return tags
    }
}