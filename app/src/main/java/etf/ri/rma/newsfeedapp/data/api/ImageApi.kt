package etf.ri.rma.newsfeedapp.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import etf.ri.rma.newsfeedapp.data.ImageResponse // VAŽNO: Provjeri da li je putanja tačna

interface ImageApi { // Preimenovano iz ImmagaApi u ImageApi kako bi odgovaralo tvom ImmageRetrofit objektu
    @GET("tags") // Endpoint za tagove je "tags"
    suspend fun getTags(
        @Query("image_url") imageUrl: String, // Parametar za URL slike
        @Query("limit") limit: Int = 10, // Opciono: Ograniči broj tagova
        @Query("threshold") threshold: Float = 0.3f // Opciono: Minimalna sigurnost taga
    ): ImageResponse // Vraća ImmagaResponse objekat koji mapira JSON odgovor
}