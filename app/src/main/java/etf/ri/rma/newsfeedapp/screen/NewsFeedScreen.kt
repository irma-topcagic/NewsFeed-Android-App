package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.data.NewsData

@Composable
fun NewsFeedScreen() {
    val allNews = NewsData.getAllNews()
    var selectedCategory by remember { mutableStateOf("Sve") }
    val clickedNewsIds = remember { mutableStateListOf<String>() }

    fun handleCardClick(newsId: String) {
        if (!clickedNewsIds.contains(newsId)) {
            clickedNewsIds.add(newsId)
        }
    }

    val filteredNews = remember(selectedCategory, allNews) {
        if (selectedCategory == "Sve") allNews
        else allNews.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    val listState = rememberLazyListState()

    LaunchedEffect(filteredNews) {
        listState.scrollToItem(0)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        FilterSection(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (filteredNews.isEmpty()) {
            MessageCard("Nema pronađenih vijesti u kategoriji \"$selectedCategory\"")
        } else {
            NewsList(
                newsItems = filteredNews,
                listState = listState,
                clickedNewsIds = clickedNewsIds,
                onCardClick = { handleCardClick(it) }
            )
        }
    }
}
