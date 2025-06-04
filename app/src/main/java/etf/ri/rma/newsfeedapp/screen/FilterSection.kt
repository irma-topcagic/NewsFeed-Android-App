package etf.ri.rma.newsfeedapp.screen
import androidx.compose.foundation.layout.*
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterSection(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onMoreFiltersClicked: () -> Unit,
    showMoreFiltersChip: Boolean = true
) {
    val categories = listOf("Sve", "Politika", "Sport", "Nauka","Tehnologija","Kultura")
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val fontSize = if (category == "Sve") 16.sp else 16.sp
            FilterChip(
                selected = selectedCategory == category,
                onClick = { if (selectedCategory != category) onCategorySelected(category) },
                label = {
                    Text(
                        text = category,
                        style = TextStyle(fontSize = fontSize)
                    )
                },
                modifier = Modifier.testTag(getChipTag(category))
            )
        }
        if (showMoreFiltersChip) {
            FilterChip(
                selected = false,
                onClick = { onMoreFiltersClicked() },
                label = { Text("Više filtera ...") },
                modifier = Modifier.testTag("filter_chip_more")
            )
        }
    }
}
fun getChipTag(category: String): String {
    return when (category) {
        "Politika" -> "filter_chip_pol"
        "Sport" -> "filter_chip_spo"
        "Nauka" -> "filter_chip_sci"
        "Kultura" -> "filter_chip_cul"
        "Tehnologija" -> "filter_chip_tech"
        "Sve" -> "filter_chip_all"
        else -> "filter_chip_unknown"
    }
}