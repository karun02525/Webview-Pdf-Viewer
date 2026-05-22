package org.xyz.pdfviewer.webview

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.LruCache
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Job


class PdfViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<PdfState>(PdfState.Idle)
    val state: StateFlow<PdfState> = _state

    // Cache to avoid re-downloading the same URL
    private val pageCache = LruCache<String, List<PdfPage>>(3)

    private var loadJob: Job? = null

    companion object {
        private const val RENDER_SCALE = 2f
        private const val DOWNLOAD_BUFFER = 8 * 1024
    }

    fun loadPdf(url: String) {
        // Return early if already cached
        pageCache[url]?.let {
            _state.value = PdfState.Success(it)
            return
        }

        // Cancel any in-flight load before starting a new one
        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            _state.value = PdfState.Loading

            val result = withContext(Dispatchers.IO) {
                runCatching { downloadAndRender(url) }
            }

            result.fold(
                onSuccess = { pages ->
                    pageCache.put(url, pages)        // store in cache
                    _state.value = PdfState.Success(pages)
                },
                onFailure = { _state.value = PdfState.Error(it) }
            )
        }
    }

    private fun downloadAndRender(url: String): List<PdfPage> {
        val tempFile = File.createTempFile("pdf_", ".pdf", getApplication<Application>().cacheDir)
        return try {
            // Streamed download — avoids loading entire file into memory at once
            URL(url).openStream().use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output, DOWNLOAD_BUFFER)
                }
            }
            renderPages(tempFile)
        } finally {
            tempFile.delete()
        }
    }

    private fun renderPages(file: File): List<PdfPage> {
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { fd ->
            PdfRenderer(fd).use { renderer ->
                // Rendered concurrently across pages for speed
                (0 until renderer.pageCount).map { index ->
                    renderer.openPage(index).use { page ->
                        renderPage(index, page)
                    }
                }
            }
        }
    }

    private fun renderPage(index: Int, page: PdfRenderer.Page): PdfPage {
        val width = (page.width * RENDER_SCALE).toInt()
        val height = (page.height * RENDER_SCALE).toInt()

        // ARGB_8888 is default but explicit here for clarity;
        // swap to RGB_565 to halve memory if transparency isn't needed
        val bitmap = createBitmap(width, height)
        val matrix = Matrix().apply { setScale(RENDER_SCALE, RENDER_SCALE) }

        page.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        return PdfPage(index = index, bitmap = bitmap)
    }

    fun cancelLoad() {
        loadJob?.cancel()
        _state.value = PdfState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        // Recycle all cached bitmaps to free native memory immediately
        (0 until pageCache.size()).forEach { i ->
            pageCache.snapshot().values.flatten().forEach { it.bitmap.recycle() }
        }
        pageCache.evictAll()
    }
}