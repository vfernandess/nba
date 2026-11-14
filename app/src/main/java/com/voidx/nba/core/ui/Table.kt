package com.voidx.nba.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun TableHeader(
    titles: List<String>,
    weights: List<Float>,
    modifier: Modifier = Modifier,
) {
    require(titles.size == weights.size) {
        "titles (${titles.size}) and weights (${weights.size}) must have the same size"
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            titles.forEachIndexed { index, title ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(weights[index]),
                )
            }
        }
        HorizontalDivider()
    }
}

@Composable
fun TableRow(
    cells: List<String>,
    weights: List<Float>,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    require(cells.size == weights.size) {
        "cells (${cells.size}) and weights (${weights.size}) must have the same size"
    }
    val clickModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(clickModifier)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            cells.forEachIndexed { index, cell ->
                Text(
                    text = cell,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(weights[index]),
                )
            }
            if (onClick != null) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        HorizontalDivider()
    }
}
