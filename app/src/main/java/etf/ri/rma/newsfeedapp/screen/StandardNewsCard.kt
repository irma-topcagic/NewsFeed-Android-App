
package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.testTag

import androidx.compose.ui.text.font.FontWeight

import coil.compose.rememberAsyncImagePainter
import etf.ri.rma.newsfeedapp.model.NewsItem

@Composable
fun StandardNewsCard(newsItem: NewsItem, modifier: Modifier = Modifier, onClick: () -> Unit) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("standard_news_card"),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {

            val imageUrl = newsItem.news.imageUrl
            if (!imageUrl.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Slika vijesti: ${newsItem.news.title}",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 16.dp),
                    contentScale = ContentScale.Crop
                )
            } else {

                Spacer(modifier = Modifier.size(100.dp).padding(end = 16.dp))

            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = newsItem.news.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = newsItem.news.snippet,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${newsItem.news.source} • ${newsItem.news.publishedDate}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}