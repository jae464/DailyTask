package com.jae464.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingScreen(
    onClickTestScreen: () -> Unit,
    onClickPreference: (String) -> Unit = {},
) {
    val settingDestinations = SettingDestination.entries

    SettingScreen(
        destinations = settingDestinations,
        onClickPreference = onClickPreference
    )
}

@Composable
fun SettingScreen(
    destinations: List<SettingDestination>,
    onClickPreference: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Column {
                SettingList(
                    destinations = destinations,
                    onClickPreference = onClickPreference
                )
            }
        }
    }
}

@Composable
fun SettingList(
    destinations: List<SettingDestination>,
    onClickPreference: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        destinations.map {
            SettingItem(destination = it, onClickPreference = onClickPreference)
        }
    }
}

@Composable
fun SettingItem(
    destination: SettingDestination,
    onClickPreference: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClickPreference(destination.route) },
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = destination.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { onClickPreference(destination.route) }) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "navigate_icon",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surface
        )
    }
}


