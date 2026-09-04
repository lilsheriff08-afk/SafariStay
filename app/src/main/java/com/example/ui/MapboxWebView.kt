package com.example.ui

import android.annotation.SuppressLint
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.SafariItem
import com.example.data.StayItem
import org.json.JSONArray
import org.json.JSONObject

class MapInterface(private val onMarkerClick: (String, String) -> Unit) {
    @JavascriptInterface
    fun onMarkerClick(id: String, type: String) {
        onMarkerClick.invoke(id, type)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CatalogMapboxWebView(
    modifier: Modifier = Modifier,
    stays: List<StayItem> = emptyList(),
    safaris: List<SafariItem> = emptyList(),
    onMarkerClick: (String, String) -> Unit = { _, _ -> }
) {
    // Generate JSON for markers
    val markersJsonArray = remember(stays, safaris) {
        val array = JSONArray()
        stays.filter { it.lat != null && it.lng != null }.forEach { stay ->
            val obj = JSONObject()
            obj.put("id", stay.id)
            obj.put("title", stay.title)
            obj.put("lat", stay.lat)
            obj.put("lng", stay.lng)
            obj.put("price", stay.pricePerNight.toInt())
            obj.put("type", "Stay")
            array.put(obj)
        }
        safaris.filter { it.lat != null && it.lng != null }.forEach { safari ->
            val obj = JSONObject()
            obj.put("id", safari.id)
            obj.put("title", safari.title)
            obj.put("lat", safari.lat)
            obj.put("lng", safari.lng)
            obj.put("price", safari.price.toInt())
            obj.put("type", "Safari")
            array.put(obj)
        }
        array.toString()
    }

    val htmlContent = remember(markersJsonArray) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <title>Catalog Map</title>
            <meta name="viewport" content="initial-scale=1,maximum-scale=1,user-scalable=no,width=device-width">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                body, html { margin: 0; padding: 0; width: 100%; height: 100%; overflow: hidden; background: #e0ded8; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; }
                #map { width: 100%; height: 100%; }
                .custom-marker {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    border-radius: 20px;
                    padding: 4px 8px;
                    color: white;
                    font-size: 11px;
                    font-weight: 700;
                    box-shadow: 0 3px 6px rgba(0,0,0,0.3);
                    border: 2px solid white;
                    cursor: pointer;
                    white-space: nowrap;
                    transition: transform 0.2s ease;
                }
                .marker-stay {
                    background: linear-gradient(135deg, #1B5E20, #2E7D32);
                }
                .marker-safari {
                    background: linear-gradient(135deg, #E65100, #F57C00);
                }
                .leaflet-popup-content-wrapper {
                    border-radius: 12px;
                    padding: 4px;
                    box-shadow: 0 4px 12px rgba(0,0,0,0.2);
                }
                .popup-btn {
                    background: #1B5E20;
                    color: white;
                    border: none;
                    border-radius: 6px;
                    padding: 6px 12px;
                    font-size: 12px;
                    font-weight: 600;
                    cursor: pointer;
                    margin-top: 6px;
                    width: 100%;
                }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                const map = L.map('map', {
                    zoomControl: false,
                    attributionControl: false
                }).setView([-0.5, 37.5], 6);

                L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
                    maxZoom: 19,
                    subdomains: 'abcd'
                }).addTo(map);

                const markersData = $markersJsonArray;

                if (markersData.length > 0) {
                    const bounds = [];
                    markersData.forEach(function(item) {
                        const isStay = item.type === 'Stay';
                        const bgClass = isStay ? 'marker-stay' : 'marker-safari';
                        const iconHtml = '<div class="custom-marker ' + bgClass + '">' + (isStay ? '🏨 $' : '🦁 $') + item.price + '</div>';
                        
                        const customIcon = L.divIcon({
                            html: iconHtml,
                            className: '',
                            iconSize: [60, 24],
                            iconAnchor: [30, 12]
                        });

                        const marker = L.marker([item.lat, item.lng], { icon: customIcon }).addTo(map);
                        
                        const popupContent = '<div style="font-size:12px; font-weight:600; margin-bottom:4px;">' + 
                            item.title + 
                            '</div><div style="color:#666; font-size:11px;">' + (isStay ? 'Lodge / Resort' : 'Safari Experience') + ' - $' + item.price + '</div>' +
                            '<button class="popup-btn" onclick="triggerMarkerClick(\'' + item.id + '\', \'' + item.type + '\')">View Details</button>';
                        
                        marker.bindPopup(popupContent);
                        
                        bounds.push([item.lat, item.lng]);
                    });

                    if (bounds.length > 0) {
                        map.fitBounds(bounds, { padding: [30, 30] });
                    }
                }

                function triggerMarkerClick(id, type) {
                    if (window.Android) {
                        window.Android.onMarkerClick(id, type);
                    }
                }
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        cacheMode = WebSettings.LOAD_DEFAULT
                        useWideViewPort = true
                        loadWithOverviewMode = true
                    }
                    addJavascriptInterface(MapInterface(onMarkerClick), "Android")
                    webViewClient = object : WebViewClient() {
                        override fun onRenderProcessGone(
                            view: WebView?,
                            detail: RenderProcessGoneDetail?
                        ): Boolean {
                            return true // Gracefully handle renderer crashes and prevent app exit
                        }
                    }
                    setOnTouchListener { v, event ->
                        when (event.action) {
                            android.view.MotionEvent.ACTION_DOWN -> {
                                v.parent?.requestDisallowInterceptTouchEvent(true)
                            }
                            android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                                v.parent?.requestDisallowInterceptTouchEvent(false)
                            }
                        }
                        false
                    }
                    loadDataWithBaseURL("https://unpkg.com/", htmlContent, "text/html", "UTF-8", null)
                }
            },
            update = { webView ->
                // Only update content if changed
                if (webView.tag != markersJsonArray) {
                    webView.tag = markersJsonArray
                    webView.loadDataWithBaseURL("https://unpkg.com/", htmlContent, "text/html", "UTF-8", null)
                }
            }
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 2.dp
        ) {
            Text(
                text = "📍 Interactive Map",
                fontSize = 11.sp,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

