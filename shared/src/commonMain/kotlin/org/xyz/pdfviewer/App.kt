package org.xyz.pdfviewer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxSize(),
            ) {
            OpenWebView(
                title = "PDF Viewer",
                modifier = Modifier.fillMaxSize(),
                url = "https://www.princexml.com/samples/",
            )
        }
    }
}