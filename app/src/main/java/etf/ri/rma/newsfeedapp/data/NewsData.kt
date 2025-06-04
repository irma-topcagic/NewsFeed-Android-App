package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.model.NewsItem

object NewsData {
    private val initialNews = mutableListOf<NewsItem>()
    private val fetchedNews = mutableListOf<NewsItem>()
    private var initialized = false

    fun initializeHardcodedIfNeeded() {
        if (initialized) return
        initialized = true

        val hardcoded =listOf(
            NewsItem(
                uuid = "793b24b8-4e9b-42ee-aaf7-5c39cf6874e5",
                title = "Xabi Alonso officially takes over as Real Madrid's coach",
                snippet = "MADRID — Xabi Alonso officially took over Real Madrid's coaching job on Monday, " +
                        "vowing to follow in the footsteps of Carlo Ancelotti and saying he has “a go...",
                imageUrl = "https://www.sportsnet.ca/wp-content/uploads/2025/05/alonso_xabi_1280.jpg",
                category = "sports",
                isFeatured = false,
                source = "sportsnet.ca",
                publishedDate = "26-05-2025",
                imageTags = arrayListOf()
            ),
            NewsItem(
                uuid= "7c588b4d-2d15-4776-ac55-4e9bbdd3fe79",
                title = "Swiatek, Alcaraz off to good starts in French Open title defences",
                snippet = "PARIS — Iga Swiatek, who has struggled lately, and Carlos Alcaraz, who has not, " +
                        "got off to good starts in their French Open title defences Monday, recording s",
                imageUrl =" https://www.sportsnet.ca/wp-content/uploads/2025/05/Alcaraz-1.jpg",
                category = "sports",
                isFeatured = false,

                source = "sportsnet.ca",
                publishedDate = "26-05-2025",
                imageTags = arrayListOf()
            ),
            NewsItem(
                uuid = "69778c36-67b4-4f0b-adf6-98ff9cd1d3de",
                title = "Scott McLaughlin's Indy 500 hopes dashed as he crashes before green flag waves",
                snippet = "NEW You can now listen to Fox News articles!\\n\\nScott McLaughlin’s Indianapolis " +
                        "500 was finished before the race went green.\\n\\nMcLaughlin and the rest of the fie",
                imageUrl = "https://static.foxnews.com/foxnews.com/content/uploads/2025/05/scott-mclaughlin6.jpg",
                category = "politics",
                isFeatured = false,
                source = "foxnews.com",
                publishedDate = "25-05-2025",
                imageTags = arrayListOf()
            ),
            NewsItem(
                uuid = "36a76ec2-af2b-4534-8988-7154f3295601",
                title = "Kyle Larson's attempt at 'Double' comes to end as he wrecks at Indy 500\",\n" +
                        "                        \"description\": \"Kyle Larson failed at his opportunity to complete the Indianapolis 500 on Sunday " +
                        "as he and two others wrecked on Lap 91 of the race. He will now race at the Coca-Cola 600.",
                snippet = "NEW You can now listen to Fox News articles!\\n\\nKyle Larson’s " +
                        "attempt to complete \\\"The Double\\\" ended abruptly on Sunday.",
                imageUrl = "https://static.foxnews.com/foxnews.com/content/uploads/2025/05/kyle-larson2.jpg",
                category = "politics",
                isFeatured = false,
                source = "foxnews.com",
                publishedDate = "25-05-2025",
                imageTags = arrayListOf("")
            ),
            NewsItem(
                uuid = "f9010a85-3c81-43fd-bb7d-04ea40f15e02",
                title = "NOAA reveals 2025 Atlantic hurricane season forecast today",
                snippet = "Ten days before start of the 2025 Atlantic hurricane season, officials " +
                        "at the National Oceanic and Atmospheric Administration are announcing their forecast for",
                imageUrl = "https://assets1.cbsnewsstatic.com/hub/i/r/2024/10/08/f5bcab34-e73c-457d-98af-31dc2afbb097/thumbnail/1200x630/c8e1c291b7ae70671d278f0dddd8af2f/hurricane-milton-20241008-1300.jpg?v=f80504b4a2a31dbf7fe3bb9ae688e3d2",
                category = "science",
                isFeatured = false,
                source = "cbsnews.com",
                publishedDate = "22-05-2025",
                imageTags = arrayListOf()
            ),
            NewsItem(
                uuid = "8e726cdf-c443-4c8e-9f5e-518d472eab64",
                title = "World's first successful tailor-made gene therapy saves baby born with rare disorder",
                snippet = "World's first successful tailor-made gene therapy saves baby born with rare disorder Baby KJ Muldoon was born with a rare genetic condition that is often fatal,",
                imageUrl = "https://assets3.cbsnewsstatic.com/hub/i/r/2025/05/16/a7901dc4-fd17-42f5-937e-3fcdfd9239b9/thumbnail/1200x630/f416223879d24bf92accbef0ad0adaa6/0516-cmo-lapook.jpg?v=6df9366690ed146f169dd0670c453f91",
                category = "science",
                isFeatured = false,
                source = "cbsnews.com",
                publishedDate = "16-05-2025",
                imageTags = arrayListOf()
            ),
            NewsItem(
                uuid = "8062c531-e519-425a-80f3-947f192fb472",
                title = "A peek inside Hunter Biden's art studio | May 26 editorial cartoons",
                snippet = "Sign up for The Week's Free Newsletters\\n\\nFrom our morning news briefing to a weekly Good News Newsletter, get the best of The Week delivered directly to your in",
                imageUrl = "https://cdn.mos.cms.futurecdn.net/wc9p6aPqgbPQ2SMTfqFezg.jpg",
                category = "politics",
                isFeatured = false,
                source = "theweek.com",
                publishedDate = "26-05-2025",
                imageTags = arrayListOf()
            ),
            NewsItem(
                uuid = "8e554d34-115f-4172-a467-bf19c7da05a1",
                title = "Military veterans of US' 'toxic soup' " +
                        "Uzbekistan base fighting for proper care 20 years after its shutter",
                snippet = "At the former Soviet base-turned-CIA black site and U.S. military base in Uzbekistan, " +
                        "researchers knew early on danger lingered not just from the enemy but from",
                imageUrl = "https://static.foxnews.com/foxnews.com/content/uploads/2025/05/uzbekistan-preview.jpg",
                category = "politics",
                isFeatured = false,
                source = "foxnews.com",
                publishedDate = "26-05-2025",
                imageTags = arrayListOf()
            ),
            NewsItem(
                uuid = "3b10ce24-581d-4d0b-9c5a-86a7dbbe7c05",
                title = "Free Rafael Nadal tribute T-shirts spark \$500 resale frenzy",
                snippet = "Rafael Nadal expresses his emotions after being honored at the French Open for his retirement from tennis." +
                        " (0:57)\\n\\nOpen Extended Reactions\\n\\nPARIS -- A day after",
                imageUrl = "https://a.espncdn.com/combiner/i?img=%2Fphoto%2F2025%2F0526%2Fr1498524_1296x729_16%2D9.jpg",
                category = "sports",
                isFeatured = false,
                source = "espn.com",
                publishedDate = "26-05-2025",
                imageTags = arrayListOf()
            ),
            NewsItem(
                uuid = "40497223-4917-4101-b9a6-24d745252047",
                title = "This machine can solve a Rubik's cube faster than most people blink",
                snippet = "Blink and you'll miss it: A Purdue University student engineering team has built a robot that can " +
                        "solve a Rubik's cube in one-tenth of a second — faster than",
                imageUrl = "https://media-cldnry.s-nbcnews.com/image/upload/t_nbcnews-fp-1200-630,f_auto,q_auto:best/rockcms/2025-05/250515-robot-Rubiks-cube-ew-518p-952641.jpg",
                category = "science",
                isFeatured = false,
                source = "nbcnews.com",
                publishedDate = "15-05-2025",
                imageTags = arrayListOf()
            )
        )
        initialNews.addAll(hardcoded)
        }

/**
 * ✅ Vraća sve vijesti: hardkodirane + dohvaćene sa API-ja
 */
fun getAllNews(): List<NewsItem> {
    initializeHardcodedIfNeeded()
    return (initialNews + fetchedNews).distinctBy { it.uuid + it.category }
}

/**
 * ✅ Samo vijesti sa API-ja, bez hardkodiranih
 */
fun getOnlyWebItems(): List<NewsItem> = fetchedNews.toList()

fun getByCategory(category: String): List<NewsItem> {
    return getAllNews().filter { it.category.equals(category, ignoreCase = true) }
}

fun addItem(news: NewsItem, toCategory: String) {
    val exists = fetchedNews.any { it.uuid == news.uuid && it.category == toCategory }
    if (!exists) {
        fetchedNews.add(news.copy(category = toCategory, isFeatured = false))
    }
}

fun addAllIfNew(newItems: List<NewsItem>) {
    newItems.forEach { newItem ->
        val exists = fetchedNews.any { it.uuid == newItem.uuid && it.category == newItem.category }
        if (!exists) {
            fetchedNews.add(newItem.copy(isFeatured = false))
        }
    }
}

fun promoteToFeatured(uuid: String, category: String) {
    val updated = fetchedNews.map {
        if (it.uuid == uuid && it.category == category) it.copy(isFeatured = true)
        else it
    }
    fetchedNews.clear()
    fetchedNews.addAll(updated)
}

fun updateTagsForImageUrl(url: String, tags: List<String>) {
    (initialNews + fetchedNews).filter { it.imageUrl == url }.forEach { item ->
        item.imageTags.clear()
        item.imageTags.addAll(tags)
    }
}
}