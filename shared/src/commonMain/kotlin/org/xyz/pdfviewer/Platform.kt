package org.xyz.pdfviewer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

@Composable
expect fun ProgressBarCircle(modifier: Modifier = Modifier)

