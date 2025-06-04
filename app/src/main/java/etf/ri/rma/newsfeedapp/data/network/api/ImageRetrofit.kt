package etf.ri.rma.newsfeedapp.data.network.api

import okhttp3.OkHttpClient
import okhttp3.Credentials

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ImageRetrofit {


    private const val IMAGGA_API_KEY = "acc_8e21f8aa4cd2b2d"
    private const val IMAGGA_API_SECRET = "bc575df2275df26b4426986f4d51aae5"

    // Basic Authentication
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()


                val credential = Credentials.basic(IMAGGA_API_KEY, IMAGGA_API_SECRET)
                requestBuilder.header("Authorization", credential)

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }


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