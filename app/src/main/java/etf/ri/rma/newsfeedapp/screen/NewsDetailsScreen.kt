// etf.ri.rma.newsfeedapp.screen/NewsDetailsScreen.kt
package etf.ri.rma.newsfeedapp.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter // Za učitavanje slika
import etf.ri.rma.newsfeedapp.data.network.ImagaDAO // Vaš ImageDAO
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidImageURLException // Vaš custom izuzetak
import etf.ri.rma.newsfeedapp.data.network.NewsDAO // Pretpostavljam da NewsDAO.getAllStories() postoji
import etf.ri.rma.newsfeedapp.model.NewsItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow

// Keš za slične vijesti po UUID-u originalne vijesti
private val similarNewsCache: ConcurrentHashMap<String, List<NewsItem>> = ConcurrentHashMap()

@OptIn(ExperimentalLayoutApi::class) // Potrebno za FlowRow
@Composable
fun NewsDetailsScreen(navController: NavController, newsId: String) {
    val newsItem = remember { mutableStateOf<NewsItem?>(null) }
    val similarNews = remember { mutableStateListOf<NewsItem>() }
    val imageUrlTags = remember { mutableStateListOf<String>() } // Lista za tagove slike
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current // Za prikaz Toast poruka
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) // Za poređenje datuma
    val newsDAO = NewsDAO()
    val imagaDAO = ImagaDAO()
    // Dohvati vijest, slične vijesti i tagove za sliku
    LaunchedEffect(newsId) {
        // Resetuj stanja prilikom promjene vijesti (npr. ako se navigira na detalje druge vijesti)
        newsItem.value = null
        similarNews.clear()
        imageUrlTags.clear()

        // Dohvati vijest iz NewsDAO (pretpostavljamo da je tamo keširana, npr. iz getAllStories)

        val foundNews = newsDAO.getAllStories().find { it.uuid == newsId }
        newsItem.value = foundNews

        if (foundNews == null) {
            Log.w("NewsDetailsScreen", "Vijest sa ID-om $newsId nije pronađena.")
            Toast.makeText(context, "Vijest nije pronađena.", Toast.LENGTH_SHORT).show()
            return@LaunchedEffect // Prekini dalje izvršavanje ako vijest nije pronađena
        }

        // 1. Dohvaćanje sličnih vijesti sa keširanjem
        val cachedSimilar = similarNewsCache[foundNews.uuid]
        if (cachedSimilar != null) {
            Log.d("NewsDetailsScreen", "Učitavam slične vijesti iz keša za ${foundNews.uuid}")
            similarNews.addAll(cachedSimilar)
        } else {
            Log.d("NewsDetailsScreen", "Dohvaćam slične vijesti za ${foundNews.uuid}")
            try {
                val fetchedSimilar = newsDAO.getAllStories()
                    .filter { it.category == foundNews.category && it.uuid != foundNews.uuid }
                    .sortedWith(compareBy(
                        {
                            val currentDate = try {
                                dateFormat.parse(foundNews.publishedDate)?.time
                            } catch (e: Exception) {
                                null
                            }
                            val otherDate = try {
                                dateFormat.parse(it.publishedDate)?.time
                            } catch (e: Exception) {
                                null
                            }
                            if (currentDate != null && otherDate != null) {
                                kotlin.math.abs(otherDate - currentDate)
                            } else {
                                Long.MAX_VALUE
                            }
                        },
                        { it.title }
                    ))
                    .take(2) // Uzimamo samo 2 slične vijesti
                similarNews.addAll(fetchedSimilar)
                similarNewsCache[foundNews.uuid] = fetchedSimilar // Keširaj rezultate
                Log.d("NewsDetailsScreen", "Slične vijesti dohvaćene i keširane: ${fetchedSimilar.size}")
            } catch (e: Exception) {
                Log.e("NewsDetailsScreen", "Greška prilikom dohvaćanja sličnih vijesti: ${e.message}", e)
                Toast.makeText(context, "Greška pri učitavanju sličnih vijesti.", Toast.LENGTH_SHORT).show()
            }
        }


        // 2. Dohvaćanje tagova za sliku sa keširanjem (ImageDAO već kešira interno)
        // Provjeri da li URL slike postoji i nije prazan prije pokušaja dohvaćanja tagova
        val imageUrl = foundNews.imageUrl // Dobavlja imageUrl koji je String?
        if (!imageUrl.isNullOrEmpty()) { // Provjera i za null i za prazan string
            coroutineScope.launch {
                try {
                    val tags = imagaDAO.getTags(imageUrl) // Prosljeđujemo non-null String
                    imageUrlTags.addAll(tags)
                    if (tags.isEmpty()) {
                        Log.w("NewsDetailsScreen", "Nema pronađenih tagova za sliku: $imageUrl")
                    }
                } catch (e: InvalidImageURLException) {
                    Log.e("NewsDetailsScreen", "Neispravan URL slike za tagove: $imageUrl - ${e.message}")
                    Toast.makeText(context, "Greška sa URL-om slike za tagove: ${e.message}", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Log.e("NewsDetailsScreen", "Greška prilikom dohvaćanja tagova: $imageUrl - ${e.message}")
                    Toast.makeText(context, "Greška prilikom dohvaćanja tagova. Provjeri internet ili API ključ.", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Log.d("NewsDetailsScreen", "URL slike je null ili prazan za vijest: ${foundNews.uuid}. Tagovi neće biti dohvaćeni.")
        }
    }

    val currentNewsItem = newsItem.value

    // Prikaz loading stanja dok se vijest ne učita
    if (currentNewsItem == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Učitavanje detalja vijesti...", modifier = Modifier.testTag("loading_details"))
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("news_details_screen")
    ) {
        item {
            // Naslov vijesti
            Text(
                text = currentNewsItem.title, // Nema više ?: "N/A" ako je title String, ne String?
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp).testTag("details_title")
            )

            // Slika vijesti (prikazana samo ako imageUrl nije null ili prazan)
            val imageUrl = currentNewsItem.imageUrl
            if (!imageUrl.isNullOrEmpty()) {
                val painter = rememberAsyncImagePainter(imageUrl)
                Image(
                    painter = painter,
                    contentDescription = currentNewsItem.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .testTag("news_image"),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Text("Slika nije dostupna", modifier = Modifier.padding(bottom = 8.dp).testTag("no_image_text"))
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Snippet/Opis vijesti
            Text(
                text = currentNewsItem.snippet, // Nema više ?: "N/A" ako je snippet String, ne String?
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp).testTag("details_snippet")
            )

            // Datum objave
            Text(
                text = "Objavljeno: ${currentNewsItem.publishedDate}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp).testTag("details_published_date")
            )

            // Izvori
            Text(
                text = "Izvor: ${currentNewsItem.source}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp).testTag("details_source")
            )

            // URL originalnog članka - UKLONJENO JER NIJE DOSTUPNO U NEWSITEM
            // Slično, nema prikaza imageTags ako nije u NewsItem

            // Prikaz tagova slike (izdvojenih pozivom ImageDAO.getTags)
            if (imageUrlTags.isNotEmpty()) {
                Text(
                    text = "Tagovi slike:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp).testTag("tags_header")
                )
                FlowRow( // Koristimo FlowRow za tagove da se automatski prelamaju
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    imageUrlTags.forEach { tag ->
                        Card(
                            // Test tag prilagođen za tag stringove
                            modifier = Modifier.testTag("image_tag_${tag.replace(" ", "_").lowercase(Locale.ROOT)}")
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else if (!imageUrl.isNullOrEmpty()) { // Prikazati poruku ako nema tagova, ali je URL slike prisutan
                Text(
                    text = "Nema tagova za ovu sliku.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp).testTag("no_tags_message")
                )
                Spacer(modifier = Modifier.height(16.dp))
            }


            // Sekcija sličnih vijesti
            Text(
                text = "Povezane vijesti iz iste kategorije",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp).testTag("similar_news_header")
            )
        }

        // Prikaz svake slične vijesti (samo Text komponente, bez kartica)
        // Ako je lista prazna, ovdje se neće ništa renderirati
        items(similarNews) { news ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = news.title,
                modifier = Modifier
                    .testTag("related_news_title_${news.uuid}")
                    .clickable {
                        navController.navigate("details/${news.uuid}") {
                            popUpTo("newsFeed") { inclusive = false } // zadrzi originalnu navigaciju
                        }
                    }
            )
            Spacer(modifier = Modifier.height(8.dp)) // Dodaj spacer i nakon zadnje vijesti
        }


        item {
            // Button za zatvaranje detalja
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.popBackStack() }, // Vraća na prethodni ekran (NewsFeedScreen)
                modifier = Modifier.fillMaxWidth().testTag("details_close_button")
            ) {
                Text("Zatvori detalje")
            }
        }
    }
}