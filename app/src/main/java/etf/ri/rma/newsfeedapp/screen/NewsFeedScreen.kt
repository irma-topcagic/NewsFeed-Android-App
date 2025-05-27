package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.data.NewsDAO // Import NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*



@Composable
fun NewsFeedScreen(navController: NavController? = null) {
    val filters = navController?.currentBackStackEntry?.savedStateHandle
    var savedCategory by remember { mutableStateOf(filters?.get<String>("filters_category") ?: "Sve") }
    var savedDateFrom by remember { mutableStateOf(filters?.get<String>("filters_dateFrom")) }
    var savedDateTo by remember { mutableStateOf(filters?.get<String>("filters_dateTo")) }
    var savedUnwantedWords by remember { mutableStateOf(filters?.get<List<String>>("filters_unwantedWords") ?: emptyList()) }

    val displayedNews = remember { mutableStateListOf<NewsItem>() }
    val coroutineScope = rememberCoroutineScope()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    // Okidač za dohvaćanje vijesti kada se promijeni kategorija ili drugi filteri
    LaunchedEffect(savedCategory, savedDateFrom, savedDateTo, savedUnwantedWords) {
        coroutineScope.launch {
            val newsSourceList = if (savedCategory == "Sve") {
                NewsDAO.getAllStories() // Dohvati sve vijesti iz keša (sve kategorije)
            } else {
                // Za specifične kategorije, pozovi getTopStoriesByCategory, koja se brine o API pozivu i 30s kešu
                NewsDAO.getTopStoriesByCategory(savedCategory)
            }

            // Sada primijenite dodatne filtere (datum, neželjene riječi) na dobijenu listu
            var filteredResult = newsSourceList

            if (savedUnwantedWords.isNotEmpty()) {
                filteredResult = filteredResult.filter { item ->
                    savedUnwantedWords.none { word ->
                        item.title.contains(word, ignoreCase = true) ||
                                item.snippet.contains(word, ignoreCase = true)
                    }
                }
            }

            if (savedDateFrom != null && savedDateTo != null) {
                val from = runCatching { dateFormat.parse(savedDateFrom!!) }.getOrNull()
                val to = runCatching { dateFormat.parse(savedDateTo!!) }.getOrNull()
                if (from != null && to != null) {
                    filteredResult = filteredResult.filter {
                        runCatching { dateFormat.parse(it.publishedDate) }.getOrNull()
                            ?.let { d -> d in from..to } ?: false
                    }
                }
            }

            displayedNews.clear()
            displayedNews.addAll(filteredResult)
        }
    }

    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("NewsFeedApp", modifier = Modifier.testTag("news_header"))
        // Pozivaš FilterSection BEZ categories parametra
        FilterSection(
            selectedCategory = savedCategory,
            onCategorySelected = { cat ->
                savedCategory = cat
                filters?.set("filters_category", cat)
            },
            onMoreFiltersClicked = {
                navController?.navigate("filters")
            }
            // categories parametar se VISE NE PROSLEDJUJE
        )
        Spacer(Modifier.height(16.dp))

        if (displayedNews.isEmpty()) {
            MessageCard("Nema pronađenih vijesti u kategoriji \"$savedCategory\"")
        } else {
            NewsList(
                newsItems = displayedNews,
                listState = listState,
                onItemClick = { id -> navController?.navigate("details/$id") }
            )
        }
    }
}

