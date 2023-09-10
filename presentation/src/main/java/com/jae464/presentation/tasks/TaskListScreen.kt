package com.jae464.presentation.tasks

import android.os.Build
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.rounded.AccessTimeFilled
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jae464.presentation.R
import com.jae464.presentation.model.TaskUIModel
import com.jae464.presentation.sampledata.taskUiModels

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskListScreen(modifier: Modifier = Modifier) {
    val sampleDatas = taskUiModels
    Surface(
        modifier = Modifier
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
            ),
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(taskUiModels, key = { it.id }) { taskUiModel ->
                    TaskItem(
                        taskUIModel = taskUiModel,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
            IconButton(onClick = { /*TODO*/ },
                modifier = modifier.align(Alignment.BottomEnd)
            ) {
                Image(
                   imageVector = Icons.Rounded.AddCircle,
                    contentDescription = "add_task",
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }
}

@Composable
fun TaskItem(taskUIModel: TaskUIModel, modifier: Modifier) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),

        ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = taskUIModel.header,
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodySmall

            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                taskUIModel.dayOfWeek?.forEach { it ->
                    RoundedBackgroundText(text = it.day)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = taskUIModel.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccessTimeFilled,
                    contentDescription = "progress_time",
                    tint = Color.Black
                )
                Text(
                    text = taskUIModel.progressTime,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Timer,
                    contentDescription = "progress_time",
                    tint = Color.Black
                )
                Text(
                    text = taskUIModel.alarmTime,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun RoundedBackgroundText(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .size(24.dp)
            .background(MaterialTheme.colorScheme.surface, CircleShape)
            .padding(4.dp),
        contentAlignment = Alignment.Center // 내용을 가운데로 정렬
    ) {
        Text(text = text, style = MaterialTheme.typography.labelSmall)
    }
}



