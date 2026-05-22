package org.xyz.pdfviewer.webview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PageIndicator(
    currentPage: Int,
    totalPages: Int,
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible  = visible && totalPages > 0,
        enter    = fadeIn(),
        exit     = fadeOut(),
        modifier = modifier,
    ) {
        Text(
            text       = "$currentPage of $totalPages",
            fontSize   = 12.sp,
            fontWeight = FontWeight.Bold,
            color      = Color.Black,
            modifier   = Modifier
                // 1. Apply shadow first so it follows the shape
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(10.dp),
                    clip = false
                )
                // 2. Apply background
                .background(
                    color = Color.White.copy(alpha = 0.90f),
                    shape = RoundedCornerShape(10.dp),
                )
                .padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}