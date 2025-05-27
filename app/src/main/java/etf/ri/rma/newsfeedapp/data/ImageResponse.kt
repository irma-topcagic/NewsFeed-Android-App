package etf.ri.rma.newsfeedapp.data

data class ImageResponse(
    val result: ImmagaResult?,
    val status: ImmagaStatus?
)

data class ImmagaResult(
    val tags: List<TagItem>? // Lista objekata tipa TagItem
)

// Pojedinačni tag sa sigurnošću (confidence) i nazivom (tag)
data class TagItem(
    val confidence: Double?, // Predstavlja povjerenje (confidence) u tag
    val tag: TagLanguage? // Objekat koji sadrži naziv taga na engleskom
)

// Naziv taga na engleskom jeziku
data class TagLanguage(
    val en: String? // Engleski naziv taga
)

// Status API odgovora (npr. "success")
data class ImmagaStatus(
    val text: String?, // Tekstualni opis statusa
    val type: String?  // Tip statusa (npr. "success")
)