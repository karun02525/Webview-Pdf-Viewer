package org.xyz.pdfviewer.webview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.delay
import platform.CoreGraphics.CGRectGetHeight
import platform.CoreGraphics.CGRectGetWidth
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.UIKit.UIScreen
import platform.UIKit.UIScrollViewContentInsetAdjustmentBehavior
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.javaScriptEnabled
import kotlin.math.ceil

@OptIn(ExperimentalComposeUiApi::class, ExperimentalForeignApi::class)
@Composable
internal actual fun OpenWebView(
    title: String?,
    modifier: Modifier,
    url: String,
    isLoading: (Boolean) -> Unit,
) {
    val screen = UIScreen.mainScreen.applicationFrame
    val screenWidth = CGRectGetWidth(screen)
    val screenHeight = CGRectGetHeight(screen)

    var canGoBack by remember { mutableStateOf(false) }
    var currentPageUrl by remember { mutableStateOf("") }
    var zoomScale by remember { mutableStateOf(1.0f) }
  /*  var currentPage by remember { mutableStateOf(1) }
    var totalPages by remember { mutableStateOf(0) }
*/
    val isPdfOpen = remember(currentPageUrl) {
        currentPageUrl.lowercase().let { lower ->
            lower.endsWith(".pdf") || lower.contains(".pdf?") || lower.contains(".pdf#")
        }
    }

    LaunchedEffect(isPdfOpen) {
        if (!isPdfOpen) {
            zoomScale = 1.0f
         //   currentPage = 1
           // totalPages = 0
        }
    }

    val navigationDelegate = remember {
        WKNavigationDelegate(
            isLoading = isLoading,
            onNavigationChange = { canGoBack = it },
            onUrlChange = { currentPageUrl = it },
        )
    }

    val config = remember {
        WKWebViewConfiguration().apply {
            allowsInlineMediaPlayback = true
            defaultWebpagePreferences.allowsContentJavaScript = true
            preferences.javaScriptEnabled = true
        }
    }

    // ── WKWebView ──────────────────────────────────────────────────────────
    val webView = remember {
        WKWebView(
            frame = CGRectMake(0.0, 0.0, screenWidth, screenHeight),
            configuration = config,
        ).apply {
            // Hides the native PDF "1 of X" bubble by disabling indicators
            scrollView.showsVerticalScrollIndicator = false
            scrollView.showsHorizontalScrollIndicator = false

            scrollView.contentInsetAdjustmentBehavior =
                UIScrollViewContentInsetAdjustmentBehavior
                    .UIScrollViewContentInsetAdjustmentAutomatic
            scrollView.minimumZoomScale = MIN_ZOOM.toDouble()
            scrollView.maximumZoomScale = MAX_ZOOM.toDouble()
            clipsToBounds = false
            allowsBackForwardNavigationGestures = true
        }
    }

    LaunchedEffect(Unit) {
        webView.navigationDelegate = navigationDelegate
        webView.UIDelegate = navigationDelegate
    }

    LaunchedEffect(zoomScale) {
        webView.scrollView.setZoomScale(zoomScale.toDouble(), animated = true)
    }

    // ── Page indicator polling (fixed for Zoom) ───────────────────────────
/*
    LaunchedEffect(isPdfOpen) {
        if (isPdfOpen) {
            while (true) {
                delay(200L)
                val currentZoom = webView.scrollView.zoomScale
                val offsetY = webView.scrollView.contentOffset.useContents { y }
                val contentH = webView.scrollView.contentSize.useContents { height }
                val viewportH = webView.scrollView.bounds.useContents { size.height }

                if (contentH > 0.0 && viewportH > 0.0) {
                    // Correct for zoom to keep page count accurate
                    val pageHeight = viewportH * currentZoom
                    val total = ceil(contentH / pageHeight).toInt().coerceAtLeast(1)
                    val current = (offsetY / pageHeight).toInt() + 1

                    totalPages = total
                    currentPage = current.coerceIn(1, total)
                }
            }
        }
    }
*/

    Column(modifier = modifier.fillMaxSize()) {
        if (isPdfOpen) {
            PdfViewerToolbar(
                title = title,
                showBackButton = canGoBack,
                zoomScale = zoomScale,
                onBack = { webView.goBack() },
                onZoomIn = { zoomScale = (zoomScale + ZOOM_STEP).coerceAtMost(MAX_ZOOM) },
                onZoomOut = { zoomScale = (zoomScale - ZOOM_STEP).coerceAtLeast(MIN_ZOOM) },
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.TopStart,
        ) {
            UIKitView(
                factory = { isLoading.invoke(true); webView },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    if (view.URL?.absoluteString != url) {
                        view.loadRequest(NSURLRequest(NSURL(string = url)))
                    }
                },
                properties = UIKitInteropProperties(
                    interactionMode = UIKitInteropInteractionMode.NonCooperative,
                    isNativeAccessibilityEnabled = true,
                ),
            )

           /* if (isPdfOpen) {
                PageIndicator(
                    currentPage = currentPage,
                    totalPages = totalPages,
                    visible = true,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp, top = 12.dp),
                )
            }*/
        }
    }
}