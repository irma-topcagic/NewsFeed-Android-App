package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.data.NewsDatabase
import etf.ri.rma.newsfeedapp.data.network.hasInternetConnection

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsDetailsScreen(navController: NavController, newsId: String) {

    val context = LocalContext.current
    val imagaDAO = remember { ImagaDAO(context) }
    val newsDAO = remember { NewsDAO(context) }
    val savedNewsDAO = remember { NewsDatabase.getDatabase(context).newsDAO() }
    var allNews by remember { mutableStateOf<List<NewsItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        if (!hasInternetConnection(context)) {
            allNews = savedNewsDAO.getAllNewsItems()
        } else {
            allNews = NewsData.getAllNews()
        }
    }
    val currentNews = allNews.find { it.news.uuid == newsId }
    var tags by remember { mutableStateOf<List<String>>(emptyList()) }
    var similar by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var tagError by remember { mutableStateOf<String?>(null) }
    var similarError by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(newsId) {
        allNews = newsDAO.getAllStories()
    }
    LaunchedEffect(currentNews) {
        currentNews?.let { news ->
            if (news.tags.isEmpty() && !news.news.imageUrl.isNullOrBlank()) {
                runCatching { imagaDAO.getTags(news.news.imageUrl) }
                    .onSuccess { tags = it }
                    .onFailure { tagError = "Nema dostupnih tagova." }
            } else {
                tags = news.tags.map { it.value }
            }

            if (similar.isEmpty()) {
                runCatching { newsDAO.getSimilarStories(news.news.uuid) }
                    .onSuccess { similar = it }
                    .onFailure { similarError = "Nema sličnih vijesti." }
            }
        }
    }

    if (currentNews == null) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Text("Vijest nije pronađena", modifier = Modifier.testTag("details_error"))
        }
        return
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        AsyncImage(
            model = currentNews.news.imageUrl,
            contentDescription = currentNews.news.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = currentNews.news.title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.testTag("details_title"))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = currentNews.news.snippet, modifier = Modifier.testTag("details_snippet"))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Kategorija: ${currentNews.news.category}", modifier = Modifier.testTag("details_category"))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Izvor: ${currentNews.news.source}", modifier = Modifier.testTag("details_source"))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Datum objave: ${currentNews.news.publishedDate}", modifier = Modifier.testTag("details_date"))
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
                    text = item.news.title,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable {
                            navController.navigate("details/${item.news.uuid}") {
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
            modifier = Modifier
                .fillMaxWidth()
                .testTag("details_close_button")
        ) {
            Text("Zatvori detalje")
        }
    }
}
