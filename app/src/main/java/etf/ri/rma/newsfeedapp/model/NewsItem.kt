package etf.ri.rma.newsfeedapp.model

import androidx.room.Embedded
import androidx.room.Relation
import androidx.room.Junction

data class NewsItem(
    @Embedded val news: News,

    @Relation(
        parentColumn = "id",
        entity = Tags::class,
        entityColumn = "id",
        associateBy = Junction(
            value = NewsTags::class,
            parentColumn = "newsId",
            entityColumn = "tagsId"
        )
    )
    val imageTags: List<Tags>
) {
    val id get() = news.id
    val uuid get() = news.uuid
    val title get() = news.title
    val snippet get() = news.snippet
    val imageUrl get() = news.imageUrl
    val category get() = news.category
    val isFeatured get() = news.isFeatured
    val source get() = news.source
    val publishedDate get() = news.publishedDate

    constructor(
        uuid: String,
        title: String,
        snippet: String,
        imageUrl: String?,
        category: String,
        isFeatured: Boolean,
        source: String,
        publishedDate: String
    ) : this(
        news = News(
            uuid = uuid,
            title = title,
            snippet = snippet,
            imageUrl = imageUrl,
            category = category,
            isFeatured = isFeatured,
            source = source,
            publishedDate = publishedDate
        ),
        imageTags = listOf()
    )
}

