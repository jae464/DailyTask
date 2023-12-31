package com.jae464.presentation.sampledata

import com.jae464.domain.model.Category
import com.jae464.domain.model.TaskType
import com.jae464.presentation.model.TaskUiModel
import com.jae464.domain.model.DayOfWeek

val taskUiModels = mutableListOf(
    TaskUiModel(
        id = "1",
        title = "안드로이드 개인 프로젝트",
        progressTime = 3700,
        taskType = TaskType.Regular,
        dayOfWeek = mutableListOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY),
        category = "학습",
        useAlarm = false,
        alarmHour = 15,
        alarmMinute = 0,
    ),
    TaskUiModel(
        id = "2",
        title = "홈 트레이닝",
        progressTime = 3700,
        taskType = TaskType.Regular,
        dayOfWeek = mutableListOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY),
        category = "운동",
        useAlarm = false,
        alarmHour = 8,
        alarmMinute = 0,
    ),
)

val categories = mutableListOf(
    Category(1L,"학습"),
    Category(2L, "운동")
)