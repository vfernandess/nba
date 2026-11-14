package com.voidx.nba.feature.home.view

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.voidx.nba.R
import com.voidx.nba.core.ui.theme.NBATheme
import com.voidx.nba.domain.model.TeamSort

@StringRes
internal fun TeamSort.labelRes(): Int = when (this) {
    TeamSort.Name -> R.string.home_sort_name
    TeamSort.City -> R.string.home_sort_city
    TeamSort.Conference -> R.string.home_sort_conference
}

@Composable
fun SortDialog(
    selected: TeamSort,
    onSortSelected: (TeamSort) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.home_sort_dialog_title)) },
        text = {
            Column(Modifier.selectableGroup()) {
                TeamSort.entries.forEach { sort ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = sort == selected,
                                onClick = { onSortSelected(sort) },
                                role = Role.RadioButton,
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = sort == selected, onClick = null)
                        Text(
                            text = stringResource(sort.labelRes()),
                            modifier = Modifier.padding(start = 12.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.home_sort_dialog_cancel)) }
        },
    )
}

@Preview
@Composable
private fun SortDialogPreview() {
    NBATheme(dynamicColor = false) {
        SortDialog(selected = TeamSort.City, onSortSelected = {}, onDismiss = {})
    }
}
