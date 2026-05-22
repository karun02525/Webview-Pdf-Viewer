package org.xyz.pdfviewer.webview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PdfViewerToolbar(
    showBackButton: Boolean,
    title: String?,
    zoomScale: Float,
    onBack: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showBackButton) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        }

        if (title != null)
            Text(
                text = title,
                maxLines = 1,
                fontWeight = FontWeight.SemiBold,
            )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            // Zoom Out (−)
            IconButton(
                onClick = onZoomOut,
                enabled = zoomScale > MIN_ZOOM,
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
            }
            // Zoom In (+)
            IconButton(
                onClick = onZoomIn,
                enabled = zoomScale < MAX_ZOOM,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In")
            }
        }
    }
}