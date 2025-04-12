package ba.etfrma.newsfeedapp.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ba.etfrma.newsfeedapp.data.getAllNews


@SuppressLint("RememberReturnType")
@Composable
fun NewsFeedScreen() {
    val allNews = remember { getAllNews() }
    var selectedCategory by remember { mutableStateOf("Sve") }

    val filteredNews = remember(selectedCategory) {
        if (selectedCategory == "Sve") allNews
        else allNews.filter { it.category == selectedCategory }
    }

    val listState = rememberLazyListState()

    LaunchedEffect(selectedCategory) {
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
            NewsList(newsItems = filteredNews, listState = listState)
        }
    }
}





