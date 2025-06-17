package com.equationl.common.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
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
import com.equationl.common.dataModel.ScienceHistoryData
import com.equationl.shared.generated.resources.Res
import com.equationl.shared.generated.resources.delete
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

/**
 * @param onDelete 如果 item 为 null 则表示删除所有历史记录，否则删除指定的 item
 * */
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
            for (item in historyList) {
                item(key = item.id) {
                    HistoryItem(
                        id = item.id,
                        createTime = item.createTime,
                        showText = item.showText,
                        result = item.result,
                        onClick = { onClick(item) },
                        onDelete = { onDelete(item) }
                    )
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
                contentDescription = stringResource(Res.string.delete),
                Modifier
                    .fillMaxHeight()
                    .clickable {
                        onDelete(null)
                    })
        }
    }
}

/**
 * @param onDelete 如果 item 为 null 则表示删除所有历史记录，否则删除指定的 item
 * */
@Composable
fun ScienceHistoryWidget(
    historyList: List<ScienceHistoryData>,
    onClick: (item: ScienceHistoryData) -> Unit,
    onDelete: (item: ScienceHistoryData?) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)) {
        LazyColumn(modifier = Modifier.weight(8f)) {
            for (item in historyList) {
                item(key = item.id) {
                    HistoryItem(
                        id = item.id,
                        createTime = item.createTime,
                        showText = item.showText,
                        result = item.result,
                        onClick = { onClick(item) },
                        onDelete = { onDelete(item) }
                    )
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
                contentDescription = stringResource(Res.string.delete),
                Modifier
                    .fillMaxHeight()
                    .clickable {
                        onDelete(null)
                    })
        }
    }
}

@Composable
private fun LazyItemScope.HistoryItem(
    id: Int,
    createTime: Long,
    showText: String,
    result: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxWidth()
            .animateItem()
            .padding(8.dp)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onDelete() }
            )) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 8.dp), horizontalArrangement = Arrangement.Start) {
            val instant = Instant.fromEpochSeconds(createTime)
            // val simpleDateFormat = SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINA)
            Text(text = instant.toLocalDateTime(TimeZone.currentSystemDefault()).toString())
        }
        Text(text = showText,fontSize = 22.sp, fontWeight = FontWeight.Light)
        Text(text = result, fontSize = 32.sp, fontWeight = FontWeight.Bold)
    }

}