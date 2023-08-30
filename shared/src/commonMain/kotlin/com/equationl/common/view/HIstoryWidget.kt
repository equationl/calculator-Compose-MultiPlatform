package com.equationl.common.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.equationl.common.dataModel.HistoryData
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * @param onDelete 如果 item 为 null 则表示删除所有历史记录，否则删除指定的 item
 * */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryWidget(
    historyList: List<HistoryData>,
    onClick: (item: HistoryData) -> Unit,
    onDelete: (item: HistoryData?) -> Unit
) {

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)) {
        LazyColumn(modifier = Modifier.weight(8f)) {
            items(
                items = historyList,
                key = { it.id },
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItemPlacement()
                        .padding(8.dp)
                        .combinedClickable(
                            onClick = { onClick(it) },
                            onLongClick = { onDelete(it) }
                        )) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp), horizontalArrangement = Arrangement.Start) {
                        val instant = Instant.fromEpochSeconds(it.createTime)
                        // val simpleDateFormat = SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINA)
                        Text(text = instant.toLocalDateTime(TimeZone.currentSystemDefault()).toString())
                    }
                    Text(text = it.showText,fontSize = 22.sp, fontWeight = FontWeight.Light)
                    Text(text = it.result, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Row(
            Modifier
                .fillMaxSize()
                .weight(2f)
                .padding(16.dp),
            horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.Bottom
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "delete",
                Modifier
                    .fillMaxHeight()
                    .clickable {
                        onDelete(null)
                    })
        }
    }
}