package etf.ri.rma.newsfeedapp.data.api

import okhttp3.Credentials
import retrofit2.Retrofit
import okhttp3.Interceptor
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
object ImmageRetrofit {
    private const val IMMAGA_BASE_URL = "https://api.imagga.com/v2/"

    // TVOJI STVARNI IMMAGA API KLJUČ I SECRET KEY
    private const val IMMAGA_API_KEY = "acc_b5c20d5f670ac3d" // Ovo je tvoj API ključ
    private const val IMMAGA_SECRET_KEY = "a7f0dba669d9e93a1df118711658da67" // Ovo je tvoj Secret ključ

    // Ovdje je ključna logika: Interceptor
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        // Credentials.basic() automatski Base64 kodira "API_KEY:SECRET_KEY" string
        val authString = Credentials.basic(IMMAGA_API_KEY, IMMAGA_SECRET_KEY)
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", authString) // Dodaje Authorization header
            .build()
        chain.proceed(newRequest) // Nastavlja sa obradom zahtjeva sa dodanim headerom
    }

    // OkHttpClient koji koristi ovaj interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor) // Dodaje interceptor u OkHttpClient
        .build()

    // Retrofit instanca koristi OkHttpClient
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(IMMAGA_BASE_URL)
            .client(okHttpClient) // Retrofit će koristiti ovaj client koji dodaje header
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ImageApi by lazy {
        retrofit.create(ImageApi::class.java)
    }
}