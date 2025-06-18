package etf.ri.rma.newsfeedapp.model

import androidx.room.Embedded
import androidx.room.Relation
import androidx.room.Junction

data class NewsItem(
    @Embedded val news: NewsEntity,

    @Relation(
        parentColumn="id",
        entity= TagEntity::class,
        entityColumn="id",
        associateBy = Junction(
            value= NewsTagCrossRef::class,
            parentColumn = "newsId",
            entityColumn = "tagsId"
        )
    ) val tags: List<TagEntity>
)