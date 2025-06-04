package etf.ri.rma.newsfeedapp.data.network.api

import okhttp3.OkHttpClient // Dodaj import za OkHttpClient
import okhttp3.Credentials // Dodaj import za Credentials
import okhttp3.Interceptor // Dodaj import za Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ImageRetrofit {


    private const val IMAGGA_API_KEY = "acc_b5c20d5f670ac3d"
    private const val IMAGGA_API_SECRET = "a7f0dba669d9e93a1df118711658da67"

    // OkHttpClient s Interceptorom za Basic Authentication
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()

                // Ovdje se dodaje API ključ i tajni ključ u Authorization header
                val credential = Credentials.basic(IMAGGA_API_KEY, IMAGGA_API_SECRET)
                requestBuilder.header("Authorization", credential)

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }

    // Retrofit instanca koja koristi OkHttpClient s Interceptorom
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.imagga.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient) // <-- VAŽNO: Povezuje Retrofit sa OkHttpClient-om!
            .build()
    }

    val api: ImagaApiService by lazy {
        retrofit.create(ImagaApiService::class.java)
    }
}