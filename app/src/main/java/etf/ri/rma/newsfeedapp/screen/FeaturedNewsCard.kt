// etf.ri.rma.newsfeedapp.screen/FeaturedNewsCard.kt
package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.Alignment
// OBRISI OVO: import androidx.compose.ui.res.painterResource // Ne treba ti više za statičku sliku
// OBRISI OVO: import etf.ri.rma.newsfeedapp.R // Ne treba ti R ako ne referenciraš R.drawable.slikarma
// DODAJ OVO:
import coil.compose.rememberAsyncImagePainter
import etf.ri.rma.newsfeedapp.model.NewsItem

@Composable
fun FeaturedNewsCard(newsItem: NewsItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    // Uklonjen je suvišni Box koji je omotavao Card,
    // jer Card već ima modificatore za popunjavanje širine, klikabilnost i padding.
    Card(
        modifier = modifier // Koristi proslijeđeni modifier, koji već ima padding ako dolazi iz NewsList
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("featured_news_card"),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            // Provjera da li imageUrl postoji i nije prazan prije prikaza slike
            val imageUrl = newsItem.imageUrl
            if (!imageUrl.isNullOrEmpty()) { // Dodato provjeru za null i prazan string
                Image(
                    painter = rememberAsyncImagePainter(imageUrl), // OVDJE JE PROMJENA
                    contentDescription = "Slika vijesti: ${newsItem.title}", // Bolji contentDescription
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                // Alternativni prikaz ako nema slike
                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center // Dodajte import androidx.compose.ui.Alignment
                ) {
                    Text("Slika nije dostupna", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = newsItem.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = newsItem.snippet,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${newsItem.source} • ${newsItem.publishedDate}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}