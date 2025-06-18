package etf.ri.rma.newsfeedapp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "news", indices = [Index(value = ["uuid"], unique = true)])
data class NewsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uuid: String,
    val title: String,
    val snippet: String,
    val imageUrl: String?,
    val category: String,
    val isFeatured: Boolean,
    val source: String,
    val publishedDate: String
)


@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val value: String
)

@Entity(
    tableName = "NewsTags",
    foreignKeys = [
        ForeignKey(
            entity = NewsEntity::class,
            parentColumns = ["id"],
            childColumns = ["newsId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagsId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["newsId"]), Index(value = ["tagsId"])]
)
data class NewsTagCrossRef(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val newsId: Int,
    val tagsId: Int
)




