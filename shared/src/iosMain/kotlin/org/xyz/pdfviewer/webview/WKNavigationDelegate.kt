package org.xyz.pdfviewer.webview

import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.WebKit.WKDownload
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKUIDelegateProtocol
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.WKWindowFeatures
import platform.darwin.NSObject

/**
 * WKNavigationDelegate is delegate class that help to update all ui, navigation and scroll view related callbacks
 *
 * @param isLoading          this is used for handle loader while open url in webview
 * @param onNavigationChange this is used for handle back button visibility
 * @param onUrlChange        this is used for tracking current page url (e.g. PDF detection)
 */
class WKNavigationDelegate(
    val isLoading: (Boolean) -> Unit,
    val onNavigationChange: (Boolean) -> Unit,
    val onUrlChange: (String) -> Unit = {},
) : NSObject(), WKNavigationDelegateProtocol, WKUIDelegateProtocol {

    override fun webView(
        webView: WKWebView,
        navigationAction: WKNavigationAction,
        didBecomeDownload: WKDownload
    ) {
        isLoading.invoke(true)
        onNavigationChange(webView.canGoBack)
        onUrlChange(webView.URL?.absoluteString ?: "")
    }

    override fun webView(
        webView: WKWebView,
        didFailNavigation: WKNavigation?,
        withError: NSError
    ) {
        isLoading.invoke(false)
        onNavigationChange(webView.canGoBack)
        onUrlChange(webView.URL?.absoluteString ?: "")
    }

    override fun webView(
        webView: WKWebView,
        decidePolicyForNavigationAction: WKNavigationAction,
        decisionHandler: (WKNavigationActionPolicy) -> Unit,
    ) {
        decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
    }

    override fun webView(
        webView: WKWebView,
        didFinishNavigation: WKNavigation?
    ) {
        isLoading.invoke(false)
        onNavigationChange(webView.canGoBack)
        onUrlChange(webView.URL?.absoluteString ?: "")
    }

    override fun webView(
        webView: WKWebView,
        createWebViewWithConfiguration: WKWebViewConfiguration,
        forNavigationAction: WKNavigationAction,
        windowFeatures: WKWindowFeatures
    ): WKWebView? {

        if (forNavigationAction.targetFrame == null) {
            webView.loadRequest(
                NSURLRequest(
                    forNavigationAction.request.URL ?: NSURL()
                )
            )
        }

        onNavigationChange(webView.canGoBack)
        onUrlChange(webView.URL?.absoluteString ?: "")

        return null
    }
}