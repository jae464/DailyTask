package com.jae464.presentation.sampledata

import com.jae464.domain.model.Category
import com.jae464.domain.model.TaskType
import com.jae464.presentation.model.TaskUIModel
import com.jae464.domain.model.DayOfWeek

val taskUiModels = mutableListOf(
    TaskUIModel(
        id = "1",
        title = "안드로이드 개인 프로젝트",
        progressTime = 3700,
        taskType = TaskType.Regular,
        dayOfWeek = mutableListOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY),
        category = "학습",
        alarmHour = 15,
        alarmMinute = 0,
    ),
    TaskUIModel(
        id = "2",
        title = "홈 트레이닝",
        progressTime = 3700,
        taskType = TaskType.Regular,
        dayOfWeek = mutableListOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY),
        category = "운동",
        alarmHour = 8,
        alarmMinute = 0,
    ),
)

val categories = mutableListOf(
    Category(1L,"학습"),
    Category(2L, "운동")
)