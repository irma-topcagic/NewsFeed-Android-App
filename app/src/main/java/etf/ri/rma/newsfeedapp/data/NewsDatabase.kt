package etf.ri.rma.newsfeedapp.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase // Important import
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.model.NewsEntity
import etf.ri.rma.newsfeedapp.model.TagEntity
import etf.ri.rma.newsfeedapp.model.NewsTagCrossRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [NewsEntity::class, TagEntity::class, NewsTagCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDAO(): SavedNewsDAO

    companion object {
        @Volatile
        private var INSTANCE: NewsDatabase? = null

        fun getDatabase(context: Context): NewsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java,
                    "news-db"
                ).addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {

                                INSTANCE?.let { database ->
                                    val newsDao = database.newsDAO()
                                    val hardcodedNews = NewsData.getAllNews()

                                    hardcodedNews.forEach { newsItem ->
                                        newsDao.saveNews(newsItem)
                                    }
                                    Log.d("NewsDatabase", "Pre-populated database with ${hardcodedNews.size} hardcoded news items.")
                                }
                            }
                        } })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}