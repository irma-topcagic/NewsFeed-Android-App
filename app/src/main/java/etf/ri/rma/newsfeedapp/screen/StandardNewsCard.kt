// etf.ri.rma.newsfeedapp.screen/StandardNewsCard.kt
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
// OBRISI OVO: import androidx.compose.ui.res.painterResource // Ne treba ti više za statičku sliku
import androidx.compose.ui.text.font.FontWeight
// OBRISI OVO: import etf.ri.rma.newsfeedapp.R // Ne treba ti R ako ne referenciraš R.drawable.slikarma
// DODAJ OVO:
import coil.compose.rememberAsyncImagePainter
import etf.ri.rma.newsfeedapp.model.NewsItem

@Composable
fun StandardNewsCard(newsItem: NewsItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    // Uklonjen je suvišni Box koji je omotavao Card,
    // jer Card već ima modificatore za popunjavanje širine, klikabilnost i padding.
    Card(
        modifier = modifier // Koristi proslijeđeni modifier, koji već ima padding ako dolazi iz NewsList
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("standard_news_card"),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            // Provjera da li imageUrl postoji i nije prazan prije prikaza slike
            val imageUrl = newsItem.imageUrl
            if (!imageUrl.isNullOrEmpty()) { // Dodato provjeru za null i prazan string
                Image(
                    painter = rememberAsyncImagePainter(imageUrl), // OVDJE JE PROMJENA
                    contentDescription = "Slika vijesti: ${newsItem.title}", // Bolji contentDescription
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 16.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Alternativni prikaz ako nema slike (npr. prazan prostor ili placeholder ikona)
                Spacer(modifier = Modifier.size(100.dp).padding(end = 16.dp))
                // Ili možete ovdje dodati Text("Nema slike") ili Icon()
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = newsItem.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = newsItem.snippet,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${newsItem.source} • ${newsItem.publishedDate}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}