package etf.ri.rma.newsfeedapp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "News", indices = [Index(value = ["uuid"], unique = true)])
data class News(
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


@Entity(tableName = "Tags")
data class Tags(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val value: String
)

@Entity(
    tableName = "NewsTags",
    foreignKeys = [
        ForeignKey(
            entity = News::class,
            parentColumns = ["id"],
            childColumns = ["newsId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tags::class,
            parentColumns = ["id"],
            childColumns = ["tagsId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["newsId"]), Index(value = ["tagsId"])]
)
data class NewsTags(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val newsId: Int,
    val tagsId: Int
)




