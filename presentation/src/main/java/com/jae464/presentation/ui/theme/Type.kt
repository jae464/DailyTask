package com.jae464.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jae464.presentation.R

val notosans = FontFamily(
    Font(R.font.notosans_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.notosans_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.notosans_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.notosans_light, FontWeight.Light, FontStyle.Normal),
    Font(R.font.notosans_semibold, FontWeight.Thin, FontStyle.Normal)
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = notosans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = notosans,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )

)