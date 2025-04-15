package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.model.NewsItem

@Composable
fun NewsList(
    newsItems: List<NewsItem>,
    selectedCategory: String,
    selectedNewsIds: Set<String> = emptySet(),
    onNewsSelect: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("news_list"),
        contentAlignment = Alignment.TopCenter
    ) {
        if (newsItems.isEmpty()) {
            Text(
                text = "Nema vijesti u kategoriji '$selectedCategory'",
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(newsItems) { newsItem ->
                    val isSelected = selectedNewsIds.contains(newsItem.id)
                    if (newsItem.isFeatured) {
                        FeaturedNewsCard(
                            newsItem = newsItem,
                            modifier = Modifier.clickable { onNewsSelect(newsItem.id) },
                            onClick = { onNewsSelect(newsItem.id) },
                            jeLiKlikIzvrsen = isSelected
                        )
                    } else {
                        StandardNewsCard(
                            newsItem = newsItem,
                            modifier = Modifier.clickable { onNewsSelect(newsItem.id) },
                            onClick = { onNewsSelect(newsItem.id) },
                            jeLiKlikIzvrsen = isSelected
                        )
                    }
                }
            }
        }
    }
}

