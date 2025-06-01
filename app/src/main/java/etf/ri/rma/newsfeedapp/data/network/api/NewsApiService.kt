package etf.ri.rma.newsfeedapp.data.network.api


import etf.ri.rma.newsfeedapp.data.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("top")
    suspend fun searchNews(
        @Query("api_token") apiToken: String,
        @Query("categories") category: String
    ):  NewsResponse

    @GET("news/similar")
    suspend fun getSimilarStories(
        @Query("api_token") apiToken: String,
        @Query("uuid") uuid: String,
        ): NewsResponse
}