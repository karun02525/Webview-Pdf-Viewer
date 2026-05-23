package org.xyz.pdfviewer.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

private const val TAG = "WEBVIEW_PDF"

private fun String.isPdfUrl(): Boolean =
    substringBefore("?")
        .substringBefore("#")
        .lowercase()
        .endsWith(".pdf")

private fun isPdfDownload(
    url: String,
    mimeType: String,
    contentDisposition: String
): Boolean =
    url.isPdfUrl()
            || mimeType.contains("pdf", ignoreCase = true)
            || (contentDisposition.contains("attachment", ignoreCase = true)
            && contentDisposition.contains(".pdf", ignoreCase = true))

private val PDF_JS_INTERCEPTOR = """
(function() {
    if (window.__pdfBridgeReady) return;
    window.__pdfBridgeReady = true;
    document.addEventListener('click', function(e) {
        var el = e.target;
        for (var i = 0; i < 10; i++) {
            if (!el || el === document.body) break;
            if (el.tagName === 'A') {
                var absHref = el.href || '';
                if (absHref.toLowerCase().indexOf('.pdf') !== -1) {
                    if (typeof AndroidPdfBridge !== 'undefined') {
                        AndroidPdfBridge.onPdfLinkClicked(absHref);
                        e.preventDefault();
                        e.stopPropagation();
                    }
                }
                return;
            }
            el = el.parentElement;
        }
    }, true);
})();
""".trimIndent()

@SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
@Composable
internal actual fun OpenWebView(
    title: String?,
    modifier: Modifier,
    url: String,
    isLoading: (Boolean) -> Unit,
) {
    var showPdfViewer by rememberSaveable { mutableStateOf(false) }
    var pdfUrl by rememberSaveable { mutableStateOf("") }
    var lastLoadedUrl by remember { mutableStateOf("") }
    var backEnabled by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    val webView = remember {
        WebView(context).apply {
            val mainWebView = this

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                loadsImagesAutomatically = true
                useWideViewPort = true
                loadWithOverviewMode = true
                builtInZoomControls = false
                displayZoomControls = false
                javaScriptCanOpenWindowsAutomatically = true
                setSupportMultipleWindows(true)
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }

            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            setBackgroundColor(android.graphics.Color.TRANSPARENT)

            addJavascriptInterface(
                object : Any() {
                    @JavascriptInterface
                    fun onPdfLinkClicked(link: String) {
                        mainHandler.post {
                            pdfUrl = link
                            showPdfViewer = true
                        }
                    }
                },
                "AndroidPdfBridge"
            )

            setDownloadListener { downloadUrl, _, contentDisposition, mimeType, _ ->
                if (isPdfDownload(downloadUrl, mimeType, contentDisposition)) {
                    pdfUrl = downloadUrl
                    showPdfViewer = true
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onCreateWindow(
                    view: WebView?,
                    isDialog: Boolean,
                    isUserGesture: Boolean,
                    resultMsg: Message?
                ): Boolean {
                    val newWebView = WebView(context).apply {
                        settings.javaScriptEnabled = true
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(v: WebView?, pageUrl: String?, f: Bitmap?) {
                                if (pageUrl == null || pageUrl == "about:blank") return
                                v?.stopLoading()
                                if (pageUrl.isPdfUrl()) {
                                    pdfUrl = pageUrl
                                    showPdfViewer = true
                                } else {
                                    mainWebView.loadUrl(pageUrl)
                                }
                            }
                        }
                    }
                    val transport = resultMsg?.obj as? WebView.WebViewTransport ?: return false
                    transport.webView = newWebView
                    resultMsg.sendToTarget()
                    return true
                }
            }

            webViewClient = object : WebViewClient() {
                // Critical for back-button reliability:
                override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                    backEnabled = view?.canGoBack() ?: false
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val clickedUrl = request?.url?.toString() ?: return false
                    return if (clickedUrl.isPdfUrl()) {
                        pdfUrl = clickedUrl
                        showPdfViewer = true
                        true
                    } else false
                }

                override fun onPageStarted(view: WebView?, pageUrl: String?, favicon: Bitmap?) {
                    if (pageUrl != null && pageUrl.isPdfUrl()) {
                        view?.stopLoading()
                        isLoading.invoke(false)
                        pdfUrl = pageUrl
                        showPdfViewer = true
                        return
                    }
                    isLoading.invoke(true)
                    backEnabled = view?.canGoBack() ?: false
                }

                override fun onPageFinished(view: WebView?, pageUrl: String?) {
                    isLoading.invoke(false)
                    backEnabled = view?.canGoBack() ?: false
                    view?.evaluateJavascript(PDF_JS_INTERCEPTOR, null)
                }

                override fun onReceivedError(v: WebView?, r: WebResourceRequest?, e: WebResourceError?) {
                    isLoading.invoke(false)
                }
            }
        }
    }

    DisposableEffect(webView) {
        onDispose {
            webView.destroy()
        }
    }

    /**
     * GLOBAL BACK HANDLER
     * Placed outside the if/else so it is always present in the composition.
     */
    BackHandler(enabled = showPdfViewer || backEnabled) {
        when {
            showPdfViewer -> {
                showPdfViewer = false
            }
            webView.canGoBack() -> {
                webView.goBack()
                // Update local state immediately to ensure UI reflects history change
                backEnabled = webView.canGoBack()
            }
        }
    }

    if (showPdfViewer) {
        PdfViewerScreen(
            title = title,
            pdfUrl = pdfUrl,
            onBack = { showPdfViewer = false }
        )
    } else {
        AndroidView(
            modifier = modifier,
            factory = { webView },
            update = {
                if (lastLoadedUrl != url) {
                    lastLoadedUrl = url
                    webView.loadUrl(url)
                }
            }
        )
    }
}