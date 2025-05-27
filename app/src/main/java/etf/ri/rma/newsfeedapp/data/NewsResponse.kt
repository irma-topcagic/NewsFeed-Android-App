package etf.ri.rma.newsfeedapp.data

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @SerializedName("data")
    val data: List<NewsItemDTO>
)
