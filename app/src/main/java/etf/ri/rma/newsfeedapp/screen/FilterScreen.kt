package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FilterScreen(
    navController: NavController
) {

    val savedHandle = navController.previousBackStackEntry?.savedStateHandle
    val initCategory = savedHandle?.get<String>("filters_category") ?: "Sve"
    val initUnwantedWords = savedHandle?.get<List<String>>("filters_unwantedWords") ?: emptyList()
    val initDateFrom = savedHandle?.get<String>("filters_dateFrom")
    val initDateTo = savedHandle?.get<String>("filters_dateTo")


    var selectedCategory by rememberSaveable { mutableStateOf(initCategory) }
    var unwantedWords by rememberSaveable { mutableStateOf(initUnwantedWords) }
    var unwantedInput by rememberSaveable { mutableStateOf("") }

    var dateFrom by rememberSaveable { mutableStateOf(initDateFrom) }
    var dateTo by rememberSaveable { mutableStateOf(initDateTo) }
    var dateRangeText by rememberSaveable {
        mutableStateOf(
            if (initDateFrom != null && initDateTo != null) "$initDateFrom;$initDateTo"
            else "Odaberite datum"
        )
    }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }

    if (showDatePicker) {
        DateRangePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateRangeSelected = { startMillis, endMillis ->
                showDatePicker = false
                if (startMillis != null && endMillis != null) {
                    dateFrom = dateFormatter.format(Date(startMillis))
                    dateTo = dateFormatter.format(Date(endMillis))
                    dateRangeText = "$dateFrom;$dateTo"
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Filteri",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        FilterSection(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
            onMoreFiltersClicked = {},
            showMoreFiltersChip = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date Range Section
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = dateRangeText,
                modifier = Modifier
                    .weight(1f)
                    .testTag("filter_daterange_display")
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.testTag("filter_daterange_button")
            ) {
                Text("Odaberi")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Row {
            TextField(
                value = unwantedInput,
                onValueChange = { unwantedInput = it },
                label = { Text("Neželjena riječ") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("filter_unwanted_input")
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val cleaned = unwantedInput.trim()
                    if (cleaned.isNotEmpty() && unwantedWords.none { it.equals(cleaned, ignoreCase = true) }) {
                        unwantedWords = unwantedWords + cleaned
                        unwantedInput = ""
                    }
                },
                modifier = Modifier.testTag("filter_unwanted_add_button")
            ) {
                Text("Dodaj")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Prikaz scrollable liste neželjenih reči
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .testTag("filter_unwanted_list")
        ) {
            items(unwantedWords) { word ->
                Text(word)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                savedHandle?.apply {
                    set("filters_category", selectedCategory)
                    set("filters_unwantedWords", unwantedWords)
                    set("filters_dateFrom", dateFrom)


                }
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("filter_apply_button")
        ) {
            Text("Primijeni filtere")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
    onDismissRequest: () -> Unit,
    onDateRangeSelected: (Long?, Long?) -> Unit
) {
    val state = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onDateRangeSelected(state.selectedStartDateMillis, state.selectedEndDateMillis)
                onDismissRequest()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(state = state)
    }
}