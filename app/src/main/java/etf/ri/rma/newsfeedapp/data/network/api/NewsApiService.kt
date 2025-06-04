package etf.ri.rma.newsfeedapp.data.network.api


import etf.ri.rma.newsfeedapp.data.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsApiService {
    @GET("news/top")
    suspend fun searchNews(
        @Query("api_token") apiToken: String,
        @Query("categories") category: String
    ):  NewsResponse

    @GET("news/similar/{uuid}")
    suspend fun getSimilarStories(
        @Path("uuid") uuid: String,
        @Query("api_token") apiToken: String
    ): NewsResponse
}