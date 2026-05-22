package org.xyz.pdfviewer.webview

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.xyz.pdfviewer.ProgressBarCircle

// Detects if 2+ fingers are down, then tracks zoom + pan deltas.
// Separated from single-finger scroll so LazyColumn still handles flings.
private fun Modifier.pinchToZoom(
    onZoom: (scaleDelta: Float) -> Unit,
    onPanX: (pixelDelta: Float) -> Unit,
): Modifier = this.pointerInput(Unit) {
    awaitEachGesture {
        // Wait for the first finger
        awaitFirstDown(requireUnconsumed = false)

        do {
            val event = awaitPointerEvent()
            val activePointers = event.changes.filter { it.pressed }

            if (activePointers.size >= 2) {
                // Two-finger gesture: handle zoom + horizontal pan
                val zoomDelta = event.calculateZoom()
                val panDelta  = event.calculatePan()

                onZoom(zoomDelta)
                onPanX(panDelta.x)

                // Consume only on multi-touch so single-finger scroll still works
                event.changes.forEach { it.consume() }
            }
        } while (event.changes.any { it.pressed })
    }
}

@Composable
fun PdfViewerScreen(
    title: String? = null,
    pdfUrl: String,
    onBack: () -> Unit,
    viewModel: PdfViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    var zoomScale by remember { mutableFloatStateOf(1f) }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val listState           = rememberLazyListState()
    val horizontalScrollState = rememberScrollState()
    val scope               = rememberCoroutineScope()

    val currentPage by remember {
        derivedStateOf { listState.firstVisibleItemIndex + 1 }
    }

    var indicatorVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is PdfState.Success) {
            indicatorVisible = true
            delay(2_000L)
            if (!listState.isScrollInProgress) indicatorVisible = false
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            indicatorVisible = true
        } else {
            delay(1_500L)
            indicatorVisible = false
        }
    }

    LaunchedEffect(zoomScale) {
        horizontalScrollState.scrollTo(0)
    }

    LaunchedEffect(Unit) { viewModel.loadPdf(pdfUrl) }

    Column(modifier = Modifier.fillMaxSize()) {

        PdfViewerToolbar(
            title         = title,
            showBackButton = true,
            zoomScale     = zoomScale,
            onBack        = onBack,
            onZoomIn  = { zoomScale = (zoomScale + ZOOM_STEP).coerceAtMost(MAX_ZOOM) },
            onZoomOut = { zoomScale = (zoomScale - ZOOM_STEP).coerceAtLeast(MIN_ZOOM) },
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            when (val s = state) {

                is PdfState.Loading, PdfState.Idle -> ProgressBarCircle()

                is PdfState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text  = "Failed to load PDF",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { viewModel.loadPdf(pdfUrl) }) {
                            Text("Retry")
                        }
                    }
                }

                is PdfState.Success -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .horizontalScroll(horizontalScrollState)
                            // ── Pinch-to-zoom layer ────────────────────────
                            // Sits above horizontalScroll so two-finger events
                            // are consumed here before the scroll sees them.
                            .pinchToZoom(
                                onZoom = { scaleDelta ->
                                    zoomScale = (zoomScale * scaleDelta)
                                        .coerceIn(MIN_ZOOM, MAX_ZOOM)
                                },
                                onPanX = { pixelDelta ->
                                    // Pan horizontally in sync with the pinch gesture
                                    scope.launch {
                                        val target = (horizontalScrollState.value - pixelDelta)
                                            .toInt()
                                            .coerceIn(0, horizontalScrollState.maxValue)
                                        horizontalScrollState.scrollTo(target)
                                    }
                                },
                            ),
                    ) {
                        LazyColumn(
                            state    = listState,
                            modifier = Modifier
                                .width(screenWidth * zoomScale)
                                .fillMaxHeight(),
                        ) {
                            items(
                                items = s.pages,
                                key   = { it.index },
                            ) { page ->
                                Image(
                                    bitmap             = page.bitmap.asImageBitmap(),
                                    contentDescription = "Page ${page.index + 1} of ${s.pages.size}",
                                    contentScale       = ContentScale.FillWidth,
                                    modifier           = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp),
                                )
                            }
                        }
                    }

                    PageIndicator(
                        currentPage = currentPage,
                        totalPages  = s.pages.size,
                        visible     = indicatorVisible,
                        modifier    = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 16.dp, top = 12.dp),
                    )
                }
            }
        }
    }
}