package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.data.NewsData


@Composable
fun NewsFeedScreen() {
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedNewsIds by remember { mutableStateOf(setOf<String>()) }

    val newsItems = NewsData.getAllNews()
    val filteredNews = if (selectedCategory == "All") newsItems else newsItems.filter { it.category == selectedCategory }

    Column {
        FilterSection(selectedCategory) { category ->
            selectedCategory = category
        }
        NewsList(
            newsItems = filteredNews,
            selectedCategory = selectedCategory,
            selectedNewsIds = selectedNewsIds,
            onNewsSelect = { id ->
                selectedNewsIds = if (selectedNewsIds.contains(id)) {
                    selectedNewsIds - id
                } else {
                    selectedNewsIds + id
                }
            }
        )
    }
}