package etf.ri.rma.newsfeedapp.data

data class ImageResponse(
    val result: TagResult?,
    val status: Status?
)

data class TagResult(
    val tags: List<TagEntry>?
)

data class TagEntry(
    val confidence: Double?,
    val tag: TagLanguage?
)

data class TagLanguage(
    val en: String?
)

data class Status(
    val text: String?,
    val type: String?
)
