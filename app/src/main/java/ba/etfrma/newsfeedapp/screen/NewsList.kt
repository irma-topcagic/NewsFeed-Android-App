package ba.etfrma.newsfeedapp.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ba.etfrma.newsfeedapp.model.NewsItem

@Composable
fun NewsList(newsItems: List<NewsItem>, listState: LazyListState, modifier: Modifier = Modifier) {

    LazyColumn(
            modifier = modifier.testTag("news_list"),
            state = listState,

        ) {
            items(newsItems, key = { it.id }) { news ->
                if (news.isFeatured)
                    FeaturedNewsCard(newsItem = news, modifier = Modifier.padding(8.dp))
                else
                    StandardNewsCard(newsItem = news, modifier = Modifier.padding(8.dp))
            }
    }
}


