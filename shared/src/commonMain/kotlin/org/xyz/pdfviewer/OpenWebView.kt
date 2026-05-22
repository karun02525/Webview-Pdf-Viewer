package org.xyz.pdfviewer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.xyz.pdfviewer.webview.OpenWebView

@Composable
fun OpenWebView(
    modifier: Modifier,
    url: String,
    title: String? = null,
) {
    Box(modifier = modifier.fillMaxSize()) {
        val isLoading = rememberSaveable { mutableStateOf(true) }
        OpenWebView(
            title = title,
            modifier = modifier.fillMaxSize(), url = url,
            isLoading = {
                isLoading.value = it
            },
        )

        if (isLoading.value) {
            ProgressBarCircle(modifier = Modifier.align(Alignment.Center))
        }
    }
}