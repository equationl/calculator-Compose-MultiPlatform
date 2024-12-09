package com.equationl.common.view

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
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
import com.equationl.common.dataModel.MemoryData
import com.equationl.shared.generated.resources.Res
import com.equationl.shared.generated.resources.delete
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

/**
 * @param onDelete 如果 item 为 null 则表示删除所有历史记录，否则删除指定的 item
 * */
@OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun MemoryDataWidget(
    dataList: List<MemoryData>,
    onClick: (item: MemoryData) -> Unit,
    onAdd: (item: MemoryData) -> Unit,
    onMinus: (item: MemoryData) -> Unit,
    onDelete: (item: MemoryData?) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        LazyColumn(modifier = Modifier.weight(8f)) {
            items(
                items = dataList,
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
                        )
                ) {
                    Text(text = it.inputValue, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        //modifier = Modifier.fillMaxWidth(0.3f)
                    ) {
                        Card(
                            onClick = {
                                onDelete(it)
                            },
                        ) {
                            Text("MC", modifier = Modifier.padding(4.dp))
                        }
                        Card(
                            onClick = {
                                onAdd(it)
                            },
                        ) {
                            Text("M+", modifier = Modifier.padding(4.dp))
                        }
                        Card(
                            onClick = {
                                onMinus(it)
                            },
                        ) {
                            Text("M-", modifier = Modifier.padding(4.dp))
                        }
                    }
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