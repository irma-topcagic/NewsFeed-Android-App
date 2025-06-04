package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
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

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var displayedNews by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var loadError by remember { mutableStateOf<String?>(null) }

    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val newsDAO = remember { NewsDAO() }
    val mappedCategory = newsDAO.mapCategoryForApi(savedCategory)
    LaunchedEffect( savedCategory) {

        scope.launch {
            runCatching {
                val allNews = newsDAO.getAllStories()
                val dateFrom = savedDateFrom?.let { runCatching { dateFormat.parse(it) }.getOrNull() }
                val dateTo = savedDateTo?.let { runCatching { dateFormat.parse(it) }.getOrNull() }


                val categoryNews = if (savedCategory == "Sve") {
                    allNews
                } else {
                    val topFetched = newsDAO.getTopStoriesByCategory(mappedCategory)
                    val topFetchedUuids = topFetched.map { it.uuid }.toSet()

                    val categoryNewsAll = NewsData.getByCategory(mappedCategory)

                    val featured = categoryNewsAll.filter { it.uuid in topFetchedUuids }
                        .map { it.copy(isFeatured = true) }

                    val others = categoryNewsAll.filter { it.uuid !in topFetchedUuids }
                        .map { it.copy(isFeatured = false) }

                    featured + others
                }

                var filtered = categoryNews

                if (savedUnwantedWords.isNotEmpty()) {
                    filtered = filtered.filter { item ->
                        savedUnwantedWords.none { word ->
                            item.title.contains(word, ignoreCase = true) ||
                                    item.snippet.contains(word, ignoreCase = true)
                        }
                    }
                }

                if (dateFrom != null && dateTo != null) {
                    filtered = filtered.filter {
                        runCatching { dateFormat.parse(it.publishedDate) }
                            .getOrNull()?.let { d -> d in dateFrom..dateTo } ?: false
                    }
                }

                displayedNews = filtered
                loadError = null
                listState.scrollToItem(0)
            }.onFailure {
                loadError = "Greška pri dohvaćanju vijesti."
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("NewsFeedApp", modifier = Modifier.testTag("news_header"))
        FilterSection(
            selectedCategory = savedCategory,
            onCategorySelected = { cat ->
                savedCategory = cat
                filters?.set("filters_category", cat)
            },
            onMoreFiltersClicked = {
                navController?.navigate("filters")
            }
        )
        Spacer(Modifier.height(16.dp))

        when {
            loadError != null -> MessageCard(loadError!!)
            displayedNews.isEmpty() -> MessageCard("Nema pronađenih vijesti u kategoriji \"$savedCategory\"")
            else -> NewsList(
                newsItems = displayedNews,
                listState = listState,
                onItemClick = { id -> navController?.navigate("details/$id") }
            )
        }
    }
}
