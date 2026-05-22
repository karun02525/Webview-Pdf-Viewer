package org.xyz.pdfviewer.webview

import android.graphics.Bitmap

data class PdfPage(
    val index: Int,
    val bitmap: Bitmap,
)
sealed class PdfState {
    object Idle : PdfState()
    object Loading : PdfState()
    data class Success(val pages: List<PdfPage>) : PdfState()
    data class Error(val cause: Throwable) : PdfState()
}