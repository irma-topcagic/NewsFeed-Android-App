package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.model.NewsItem
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NewsDetailsScreen(navController: NavController, newsId: String) {
    val allNews = NewsData.getAllNews()
    val currentNews = allNews.find { it.id == newsId }

    if (currentNews == null) {

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Vijest nije pronađena", modifier = Modifier.testTag("details_error"))
        }
        return
    }

    val relatedNews = remember(currentNews) {
        allNews.filter { it.category == currentNews.category && it.id != currentNews.id }
            .sortedWith(compareBy(
                {
                    val currentDate = try {
                        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(currentNews.publishedDate)?.time
                    } catch (e: Exception) {
                        null
                    }
                    val otherDate = try {
                        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(it.publishedDate)?.time
                    } catch (e: Exception) {
                        null
                    }
                    if (currentDate != null && otherDate != null) {
                        kotlin.math.abs(otherDate - currentDate)
                    } else {
                        Long.MAX_VALUE
                    }
                },
                { it.title }
            ))
            .take(2)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // News details
        Text(text = currentNews.title ?: "N/A", modifier = Modifier.testTag("details_title"))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = currentNews.snippet ?: "N/A", modifier = Modifier.testTag("details_snippet"))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Kategorija: ${currentNews.category}", modifier = Modifier.testTag("details_category"))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Izvor: ${currentNews.source}", modifier = Modifier.testTag("details_source"))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Datum objave: ${currentNews.publishedDate}", modifier = Modifier.testTag("details_date"))
        Spacer(modifier = Modifier.height(16.dp))


        Text(text = "Povezane vijesti iz iste kategorije")
        relatedNews.forEachIndexed { index, news ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = news.title ?: "N/A",
                modifier = Modifier
                    .testTag("related_news_title_${index + 1}")
                    .clickable {
                        navController.navigate("details/${news.id}") {

                            popUpTo("newsFeed") { inclusive = false }
                        }
                    }
            )
        }

        Spacer(modifier = Modifier.weight(1f))


        Button(
            onClick = { navController.popBackStack("newsFeed", inclusive = false) },
            modifier = Modifier.fillMaxWidth().testTag("details_close_button")
        ) {
            Text("Zatvori detalje")
        }
    }
}