package org.xyz.pdfviewer.webview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun OpenWebView(
    title: String?=null,
    modifier: Modifier,
    url: String,
    isLoading: (Boolean) -> Unit,
)