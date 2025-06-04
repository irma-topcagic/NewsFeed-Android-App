package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsDetailsScreen(navController: NavController, newsId: String) {
    val allNews = remember { NewsData.getAllNews() }
    val currentNews = allNews.find { it.uuid == newsId }

    val imagaDAO = remember { ImagaDAO() }
    val newsDAO = remember { NewsDAO() }

    var tags by remember { mutableStateOf<List<String>>(emptyList()) }
    var similar by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var tagError by remember { mutableStateOf<String?>(null) }
    var similarError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentNews) {
        currentNews?.let { news ->
            if (news.imageTags.isEmpty() && !news.imageUrl.isNullOrBlank()) {
                runCatching { imagaDAO.getTags(news.imageUrl) }
                    .onSuccess { tags = it }
                    .onFailure { tagError = "Nema dostupnih tagova." }
            } else {
                tags = news.imageTags
            }

            if (similar.isEmpty()) {
                runCatching { newsDAO.getSimilarStories(news.uuid) }
                    .onSuccess { similar = it }
                    .onFailure { similarError = "Nema sličnih vijesti." }
            }
        }
    }

    if (currentNews == null) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Vijest nije pronađena", modifier = Modifier.testTag("details_error"))
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        AsyncImage(
            model = currentNews.imageUrl,
            contentDescription = currentNews.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = currentNews.title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.testTag("details_title"))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = currentNews.snippet, modifier = Modifier.testTag("details_snippet"))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Kategorija: ${currentNews.category}", modifier = Modifier.testTag("details_category"))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Izvor: ${currentNews.source}", modifier = Modifier.testTag("details_source"))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Datum objave: ${currentNews.publishedDate}", modifier = Modifier.testTag("details_date"))
        Spacer(modifier = Modifier.height(16.dp))

        Text("Tagovi slike:")
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                Text(
                    text = tag,
                    modifier = Modifier.testTag("details_tag_$tag")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Slične vijesti:")
        if (similarError != null) {
            Text(similarError!!)
        } else {
            similar.forEachIndexed { index, item ->
                Text(
                    text = item.title,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable {
                            navController.navigate("details/${item.uuid}") {
                                popUpTo("newsFeed") { inclusive = false }
                            }
                        }
                        .testTag("related_news_title_${index + 1}")
                )
            }
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
