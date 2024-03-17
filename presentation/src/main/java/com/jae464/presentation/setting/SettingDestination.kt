package com.jae464.presentation.setting

enum class SettingDestination(val title: String, val route: String) {
    ThemeSetting(title = "화면테마", route = themeSettingRoute),
    CategorySetting(title = "카테고리", route = categorySettingRoute)
}