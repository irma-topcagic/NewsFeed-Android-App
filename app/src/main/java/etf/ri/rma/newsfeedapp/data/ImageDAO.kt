// etf.ri.rma.newsfeedapp.data/ImageDAO.kt
package etf.ri.rma.newsfeedapp.data

import android.util.Log
import etf.ri.rma.newsfeedapp.data.api.ImmageRetrofit // Pretpostavljam da je ovo vaš ImmagaRetrofit objekt
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

// Custom exception za neispravan URL slike
class InvalidImageURLException(message: String) : Exception(message)

object ImageDAO {

    // Keš za tagove po URL-u slike
    // Kljuc: imageURL (String)
    // Vrijednost: Lista tagova (List<String>)
    private val imageTagsCache: ConcurrentHashMap<String, List<String>> = ConcurrentHashMap()

    /**
     * Dohvaća tagove za sliku sa proslijeđenim URL-om.
     * Kešira rezultate za brži pristup i izbjegavanje ponovljenih API poziva.
     *
     * @param imageURL URL slike za koju se traže tagovi.
     * @return Lista stringova koji predstavljaju tagove slike.
     * @throws InvalidImageURLException Ako URL slike nije u ispravnom formatu ili protokolu.
     */
    suspend fun getTags(imageURL: String): List<String> {
        // 1. Provjeri keš
        if (imageTagsCache.containsKey(imageURL)) {
            Log.d("ImageDAO", "Vraćam tagove iz keša za URL: $imageURL")
            return imageTagsCache[imageURL] ?: emptyList()
        }

        // 2. Validacija URL-a
        try {
            val url = URL(imageURL)
            // Dodatna provjera protokola da se osiguramo da je http/https
            if (url.protocol.isNullOrEmpty() || !(url.protocol.equals("http", true) || url.protocol.equals("https", true))) {
                throw MalformedURLException("URL mora imati 'http' ili 'https' protokol.")
            }
        } catch (e: MalformedURLException) {
            Log.e("ImageDAO", "Neispravan URL slike: $imageURL - ${e.message}", e)
            throw InvalidImageURLException("Neispravan format URL-a slike: $imageURL. Detalji: ${e.message}")
        } catch (e: Exception) {
            Log.e("ImageDAO", "Opća greška pri validaciji URL-a slike: $imageURL - ${e.message}", e)
            throw InvalidImageURLException("Opća greška pri validaciji URL-a slike: $imageURL. Detalji: ${e.message}")
        }

        // 3. Pozovi Immaga API
        Log.d("ImageDAO", "Pozivam Immaga API za tagove slike: $imageURL")
        try {
            // ImmageRetrofit instanca koristi OkHttpClient sa interceptorom za autentikaciju
            // Ime 'api' se poziva sa ImmageRetrofit.api, a ne ImmagaRetrofitInstance.api
            val response = ImmageRetrofit.api.getTags(imageUrl = imageURL)

            val tags = response.result?.tags?.mapNotNull { it.tag?.en } ?: emptyList()

            // 4. Keširaj rezultate
            imageTagsCache[imageURL] = tags
            Log.d("ImageDAO", "Tagovi dohvaćeni i keširani za URL: $imageURL, Tagovi: $tags")
            return tags

        } catch (e: Exception) {
            Log.e("ImageDAO", "Greška prilikom dohvaćanja tagova sa Immaga API-ja za URL: $imageURL - ${e.message}", e)
            // U slučaju greške, vrati praznu listu. Toast poruka u UI će obavijestiti korisnika.
            return emptyList()
        }
    }
}