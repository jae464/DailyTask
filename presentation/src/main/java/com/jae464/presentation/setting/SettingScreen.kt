package com.jae464.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SettingScreen(
    onClickTestScreen: () -> Unit,
    onClickPreference: (String) -> Unit = {},
) {
    val settingDestinations = SettingDestination.values().toList()

    Surface(
        modifier = Modifier
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
            )
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Column {
                SettingList(
                    destinations = settingDestinations,
                    onClickItem = onClickPreference
                )
            }
        }
    }
}

@Composable
fun SettingList(
    destinations: List<SettingDestination>,
    onClickItem: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        destinations.map {
            SettingItem(destination = it, onClickItem = onClickItem)
        }
    }
}

@Composable
fun SettingItem(
    destination: SettingDestination,
    onClickItem: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClickItem(destination.route) },
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = destination.title,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider(
            color = MaterialTheme.colorScheme.surface,
            thickness = 1.dp
        )
    }
}


