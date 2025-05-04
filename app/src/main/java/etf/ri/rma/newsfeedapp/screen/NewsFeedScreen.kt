package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.data.NewsData
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NewsFeedScreen(navController: NavController? = null) {
    val filters = navController?.currentBackStackEntry?.savedStateHandle
    var savedCategory by remember { mutableStateOf(filters?.get<String>("filters_category") ?: "Sve") }
    var savedDateFrom by remember { mutableStateOf(filters?.get<String>("filters_dateFrom")) }
    var savedDateTo by remember { mutableStateOf(filters?.get<String>("filters_dateTo")) }
    var savedUnwantedWords by remember { mutableStateOf(filters?.get<List<String>>("filters_unwantedWords") ?: emptyList()) }

    val allNews = NewsData.getAllNews()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    val filteredNews = remember(savedCategory, savedDateFrom, savedDateTo, savedUnwantedWords) {
        var result = if (savedUnwantedWords.isNotEmpty()) {
            allNews.filter { item ->
                savedUnwantedWords.none { word ->
                    item.title?.contains(word, ignoreCase = true) == true ||
                            item.snippet?.contains(word, ignoreCase = true) == true
                }
            }
        } else allNews

        if (savedCategory != "Sve") {
            result = result.filter { it.category.equals(savedCategory, true) }
        }

        if (savedDateFrom != null && savedDateTo != null) {
            val from = runCatching { dateFormat.parse(savedDateFrom!!) }.getOrNull()
            val to = runCatching { dateFormat.parse(savedDateTo!!) }.getOrNull()
            if (from != null && to != null) {
                result = result.filter {
                    runCatching { dateFormat.parse(it.publishedDate) }.getOrNull()
                        ?.let { d -> d in from..to } ?: false
                }
            }
        }

        result
    }

    val listState = rememberLazyListState()
    LaunchedEffect(filteredNews) {
        listState.scrollToItem(0)
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

        if (filteredNews.isEmpty()) {
            MessageCard("Nema pronađenih vijesti u kategoriji \"$savedCategory\"")
        } else {
            NewsList(
                newsItems = filteredNews,
                listState = listState,
                onItemClick = { id -> navController?.navigate("details/$id") }
            )
        }
    }
}
