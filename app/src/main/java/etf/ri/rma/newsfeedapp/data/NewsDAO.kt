package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.data.api.RetrofitInstance
import etf.ri.rma.newsfeedapp.model.NewsItem
import kotlinx.coroutines.delay // Potrebno za simulaciju kašnjenja, ako se koristi.
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.Collections // Import za Collections.synchronizedList

// Custom exception for invalid UUID format
class InvalidUUIDException(message: String) : Exception(message)

object NewsDAO {

    // Korištenje ConcurrentHashMap za brzu provjeru postojanja i MutaleList (sinhronizovane) za redoslijed
    private val allStoriesMap: ConcurrentHashMap<String, NewsItem> = ConcurrentHashMap()
    private val _allStoriesList: MutableList<NewsItem> = Collections.synchronizedList(mutableListOf())

    // Javno dostupan read-only pogled na listu
    private val allStoriesList: List<NewsItem> get() = _allStoriesList.toList()

    // Cache za vrijeme posljednjeg dohvaćanja po API kategoriji (engleski naziv)
    private val lastFetchTimeByCategory: ConcurrentHashMap<String, Long> = ConcurrentHashMap()

    // API Token - OBVEZNO ZAMIJENI SA SVOJIM API TOKENOM!
    private const val API_TOKEN = "eNOHHXF1gTSvpJM78iDBK7If6VS6TJaEF6k6NLdq"

    init {
        // Popuni sa početnim vijestima iz NewsData samo jednom pri inicijalizaciji
        if (_allStoriesList.isEmpty()) {
            val initial = NewsData.getAllNews()
            initial.forEach {
                allStoriesMap[it.uuid] = it
                _allStoriesList.add(it)
            }
        }
    }

    /**
     * Pomaže pri mapiranju kategorija iz lokalnog jezika na API format.
     * "Sve" kategorija se ne mapira na API kategoriju jer se za nju dohvaćaju sve lokalne vijesti.
     */
    private fun mapCategoryForApi(category: String): String {
        return when (category) {
            "Politika" -> "politics"
            "Sport" -> "sports"
            "Nauka/tehnologija" -> "science" // "tech" je također opcija
            "Biznis" -> "business"
            "Zdravlje" -> "health"
            "Zabava" -> "entertainment"
            "Hrana" -> "food"
            "Putovanja" -> "travel"
            "Kultura" -> "general" // Nema direktnog API mappinga, mapiramo na "general"
            else -> "general" // Defaultna kategorija za nepoznate ili "Sve" ako se slučajno proslijedi
        }
    }

    /**
     * Dohvaća 3 vijesti iz navedene kategorije. Ako metoda nije pozvana u prethodnih 30 sekundi,
     * poziva web servis. Inače, vraća sve vijesti iz navedene kategorije koje su već dohvaćene.
     * Za kategoriju "Sve", vraća sve dohvaćene vijesti iz keša.
     * Nove vijesti sa web servisa se dodaju na vrh liste i označavaju kao featured.
     *
     * @param category Kategorija vijesti (npr. "Politika", "Sport", "Sve").
     * @return Lista vijesti iz navedene kategorije ili svih vijesti ako je kategorija "Sve".
     */
    suspend fun getTopStoriesByCategory(category: String): List<NewsItem> {
        val currentTime = System.currentTimeMillis()

        // Ako je kategorija "Sve", vraćamo sve vijesti iz keša odmah
        if (category == "Sve") {
            // Vijesti se vraćaju kao standardne (ne-featured) za ovu globalnu listu
            return allStoriesList.map { it.copy(isFeatured = false) }.distinctBy { it.uuid }
        }

        val apiCategory = mapCategoryForApi(category)
        val lastFetchTime = lastFetchTimeByCategory[apiCategory] ?: 0L

        // Dohvati postojeće vijesti iz keša za ovu SPECIFIČNU kategoriju
        // (bilo da je kategorija iz originalnog NewsData ili iz API poziva, mi ih filtriramo po kategoriji iz NewsItem-a)
        val cachedNewsForCategory = _allStoriesList
            .filter { it.category.equals(category, ignoreCase = true) || it.category.equals(apiCategory, ignoreCase = true) }
            .map { it.copy(isFeatured = false) } // Ensure existing are not featured for this return
            .toMutableList()


        if (currentTime - lastFetchTime > 3 * 1000L) { // 30 sekundi prošlo, pozovi web servis
            println("Pozivam web servis za kategoriju: $category (API: $apiCategory)")
            try {
                val newsResponse = RetrofitInstance.api.searchNews(API_TOKEN, apiCategory)
                val newStoriesDTO = newsResponse.data
                val newStoriesFromApi = newStoriesDTO.map { it.toNewsItem() }

                lastFetchTimeByCategory[apiCategory] = currentTime // Ažuriraj vrijeme dohvaćanja za API kategoriju

                val resultListForUI = mutableListOf<NewsItem>()

                // Dodaj nove vijesti sa API-ja na vrh i ažuriraj keš
                newStoriesFromApi.forEach { newStory ->
                    val existingStory = allStoriesMap[newStory.uuid]
                    if (existingStory != null) {
                        // Vijest već postoji, označi je kao featured za prikaz i dodaj u resultListForUI
                        val updatedExisting = existingStory.copy(isFeatured = true)
                        resultListForUI.add(updatedExisting)
                        // Ažuriraj postojeci element u _allStoriesList da odražava potencijalnu promjenu kategorije
                        // if (existingStory.category != newStory.category) {
                        //      val index = _allStoriesList.indexOfFirst { it.uuid == existingStory.uuid }
                        //      if (index != -1) _allStoriesList[index] = existingStory.copy(category = newStory.category)
                        // }
                    } else {
                        // Nova vijest, dodaj je u keš i označi kao featured za prikaz
                        val featuredNewStory = newStory.copy(isFeatured = true)
                        allStoriesMap[featuredNewStory.uuid] = featuredNewStory
                        _allStoriesList.add(0, featuredNewStory) // Dodaj na početak globalne liste
                        resultListForUI.add(featuredNewStory)
                    }
                }

                // Dodaj preostale keširane vijesti za ovu kategoriju koje nisu dio novih featured vijesti
                cachedNewsForCategory.forEach { existingCachedNews ->
                    // Provjeri da li je vijest već dodana kao featured u resultListForUI
                    if (resultListForUI.none { it.uuid == existingCachedNews.uuid }) {
                        resultListForUI.add(existingCachedNews.copy(isFeatured = false)) // Dodaj kao standardnu vijest
                    }
                }

                // Osiguraj jedinstvenost i tačan redoslijed
                return resultListForUI.distinctBy { it.uuid }

            } catch (e: Exception) {
                println("Greška prilikom dohvaćanja vijesti sa web servisa za kategoriju $category: ${e.message}")
                // Ako API poziv ne uspije, vrati postojeće keširane vijesti za tu kategoriju
                return cachedNewsForCategory.distinctBy { it.uuid }
            }
        } else {
            println("Vraćam keširane vijesti za kategoriju: $category (unutar 30 sekundi)")
            // Ako nije prošlo 30 sekundi, vrati sve keširane vijesti za tu kategoriju
            return cachedNewsForCategory.distinctBy { it.uuid }
        }
    }

    /**
     * Vraća listu svih vijesti koje su dohvaćene sa web servisa tokom trenutnog korištenja aplikacije.
     * Ova metoda ne poziva direktno web servis.
     *
     * @return Lista svih dohvaćenih vijesti.
     */
    fun getAllStories(): List<NewsItem> {
        // Vraća sve vijesti iz keša kao standardne (ne-featured)
        return allStoriesList.map { it.copy(isFeatured = false) }.toList()
    }

    /**
     * Vraća 2 najsličnije vijesti sa proslijeđenim UUID-em iz iste kategorije.
     * Ukoliko uuid nije u ispravnom formatu baca izuzetak InvalidUUIDException.
     *
     * @param uuid UUID vijesti za koju tražimo slične.
     * @return Lista 2 najsličnije vijesti.
     * @throws InvalidUUIDException Ako UUID nije u ispravnom formatu.
     */
    fun getSimilarStories(uuid: String): List<NewsItem> {
        try {
            UUID.fromString(uuid) // Validacija UUID formata
        } catch (e: IllegalArgumentException) {
            throw InvalidUUIDException("Invalid UUID format: $uuid")
        }

        val originalStory = allStoriesMap[uuid]
            ?: return emptyList() // Ako originalna vijest nije pronađena, vrati praznu listu

        val similarStories = allStoriesList
            .filter { it.category == originalStory.category && it.uuid != originalStory.uuid }
            .shuffled() // Simulacija sličnosti, uzima nasumično 2 iz iste kategorije
            .take(2)

        return similarStories
    }
}