package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.model.NewsItem

@Composable
fun NewsList(
    newsItems: List<NewsItem>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier.testTag("news_list"),
        state = listState,
    ) {
        items(newsItems, key = { it.news.uuid ?: it.hashCode() }) { news ->
            if (news.news.isFeatured) {
                FeaturedNewsCard(
                    newsItem = news,
                    modifier = Modifier.padding(8.dp),
                    onClick = { news.news.uuid?.let { onItemClick(it) } }
                )
            } else {
                StandardNewsCard(
                    newsItem = news,
                    modifier = Modifier.padding(8.dp),
                    onClick = { news.news.uuid?.let { onItemClick(it) } }
                )
            }
        }
    }
}