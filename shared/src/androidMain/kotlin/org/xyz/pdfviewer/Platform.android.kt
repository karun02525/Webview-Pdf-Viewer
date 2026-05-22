package org.xyz.pdfviewer

import android.os.Build
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

@Composable
actual fun ProgressBarCircle(
    modifier: Modifier
) {
    CircularProgressIndicator(modifier = modifier)
}