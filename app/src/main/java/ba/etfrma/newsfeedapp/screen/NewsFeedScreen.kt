package ba.etfrma.newsfeedapp.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ba.etfrma.newsfeedapp.data.getAllNews
import ba.etfrma.newsfeedapp.model.NewsItem

@SuppressLint("RememberReturnType")
@Composable
fun NewsFeedScreen() {
    val allNews = remember { getAllNews() }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredNews = remember(selectedCategory) {
        if (selectedCategory == "All") allNews
        else allNews.filter { it.category == selectedCategory }
    }

    val listState = rememberLazyListState()

    LaunchedEffect(selectedCategory) {
        listState.scrollToItem(0)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        FilterChips(
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChips(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("All", "Politika", "Sport", "Nauka/tehnologija", "Kultura")

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { if (selectedCategory != category) onCategorySelected(category) },
                label = {
                    Text(
                        text = category,
                        style = TextStyle(
                            fontSize = if (category in listOf("All", "Politika", "Sport", "Nauka/tehnologija","Kultura" +
                                        "")) 20.sp else 14.sp
                        )
                    )
                },
                modifier = Modifier.testTag(getChipTag(category))
            )
        }
    }
}

fun getChipTag(category: String): String {
    return when (category) {
        "Politika" -> "filter_chip_pol"
        "Sport" -> "filter_chip_spo"
        "Nauka/tehnologija" -> "filter_chip_sci"
        "All" -> "filter_chip_all"
        else -> "filter_chip_unknown"
    }
}

@Composable
fun NewsList(newsItems: List<NewsItem>, listState: LazyListState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = listState
    ) {
        items(newsItems, key = { it.id }) { item ->
            if (item.isFeatured) {
                FeaturedNewsCard(newsItem = item)
            } else {
                StandardNewsCard(newsItem = item)
            }
        }
    }
}