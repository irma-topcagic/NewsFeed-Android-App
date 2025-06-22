package etf.ri.rma.newsfeedapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import etf.ri.rma.newsfeedapp.model.News
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.model.NewsTags
import etf.ri.rma.newsfeedapp.model.Tags

@Dao
interface SavedNewsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsEntity(news: News):Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTagsEntities(tags: List<Tags>): List<Long>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsTagCrossRef(refs: List<NewsTags>): List<Long>
    @Transaction
    @Query("SELECT * FROM news")
    suspend fun allNews(): List<NewsItem>

    @Transaction
    suspend fun saveNews(newsItem: NewsItem): Boolean {
        val existingNews = getNewsEntityByUuid(newsItem.news.uuid)
        return if (existingNews == null) {

            val newsId = insertNewsEntity(newsItem.news)


            if (newsId > 0 && newsItem.imageTags.isNotEmpty()) {

                val newTagsCount = addTags(newsItem.imageTags.map { it.value }, newsId.toInt())

            }
            true
        } else {
            false
        }
    }

    @Query("SELECT * FROM news WHERE uuid = :uuid")
    suspend fun getNewsEntityByUuid(uuid: String): News?


    @Transaction
    @Query("SELECT * FROM news")
    suspend fun getAllNewsItems(): List<NewsItem>


    @Transaction // Required for @Relation to work
    @Query("SELECT * FROM news WHERE category = :category ORDER BY publishedDate DESC") // Added sorting
    suspend fun getNewsWithCategory(category: String): List<NewsItem>

    @Transaction
    suspend fun addTags(tagValues: List<String>, newsId: Int): Int {
        var newTagsAddedCount = 0
        val existingTags = getTagEntitiesByValues(tagValues)
        val crossRefsToInsert = mutableListOf<NewsTags>()

        tagValues.forEach { tagValue ->
            val existingTag = existingTags.find { it.value == tagValue }
            val tagId: Int

            if (existingTag == null) {

                val newTagId = insertTagsEntities(listOf(Tags(value = tagValue))).first()
                tagId = newTagId.toInt()
                newTagsAddedCount++
            } else {

                tagId = existingTag.id
            }


            val isAlreadyLinked = getNewsTagCrossRef(newsId, tagId) != null
            if (!isAlreadyLinked) {
                crossRefsToInsert.add(NewsTags(newsId = newsId, tagsId = tagId))
            }
        }

        if (crossRefsToInsert.isNotEmpty()) {
            insertNewsTagCrossRef(crossRefsToInsert)
        }

        return newTagsAddedCount
    }
    @Query("SELECT id FROM news WHERE imageUrl = :imageUrl LIMIT 1")
    suspend fun getNewsIdByImageUrl(imageUrl: String): Long?

    @Transaction
    @Query("SELECT * FROM news WHERE uuid = :uuid LIMIT 1")
    suspend fun getNewsItemByUuid(uuid: String): NewsItem?
    @Query("SELECT * FROM tags WHERE value IN (:tagValues)")
    suspend fun getTagEntitiesByValues(tagValues: List<String>): List<Tags>

    @Query("SELECT * FROM NewsTags WHERE newsId = :newsId AND tagsId = :tagId LIMIT 1")
    suspend fun getNewsTagCrossRef(newsId: Int, tagId: Int): NewsTags?


    @Query("""
        SELECT T.value
        FROM tags AS T
        INNER JOIN NewsTags AS NT ON T.id = NT.tagsId
        WHERE NT.newsId = :newsId
    """)
    suspend fun getTags(newsId: Int): List<String>


    @Transaction
    @Query("""
        SELECT N.*
        FROM news AS N
        INNER JOIN NewsTags AS NT ON N.id = NT.newsId
        INNER JOIN tags AS T ON NT.tagsId = T.id
        WHERE T.value IN (:tagValues)
        GROUP BY N.id 
        ORDER BY N.publishedDate DESC
    """)
    suspend fun getSimilarNews(tagValues: List<String>): List<NewsItem>
    @Delete
    suspend fun deleteNewsEntity(news: News)
}