package etf.ri.rma.newsfeedapp.data.network

import etf.ri.rma.newsfeedapp.model.NewsItem
import java.util.concurrent.ConcurrentHashMap

object NewsCache {

    val uuidCache = ConcurrentHashMap<String, List<NewsItem>>() // For similar stories by UUID
    val tagCache = ConcurrentHashMap<String, List<String>>() // For image tags by URL
}