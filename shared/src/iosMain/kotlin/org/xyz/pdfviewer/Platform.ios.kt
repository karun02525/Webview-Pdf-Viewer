package org.xyz.pdfviewer

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIActivityIndicatorView
import platform.UIKit.UIActivityIndicatorViewStyleMedium
import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun ProgressBarCircle(
    modifier: Modifier
) {
    UIKitView(
        factory = {
            UIActivityIndicatorView(
                activityIndicatorStyle = UIActivityIndicatorViewStyleMedium
            ).apply {
                translatesAutoresizingMaskIntoConstraints = true
                hidesWhenStopped = false
                startAnimating()
            }
        },
        modifier = modifier.size(32.dp),
        update = { view ->
            if (!view.isAnimating()) {
                view.startAnimating()
            }
        },
    )
}