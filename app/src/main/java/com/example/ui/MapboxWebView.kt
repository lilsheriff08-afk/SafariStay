package com.example.ui

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.BuildConfig
import com.example.data.SafariItem
import com.example.data.StayItem
import org.json.JSONArray
import org.json.JSONObject

import android.webkit.JavascriptInterface

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
    val mapboxToken = BuildConfig.MAPBOX_ACCESS_TOKEN
    
    // Generate JSON for markers
    val markersJsonArray = remember(stays, safaris) {
        val array = JSONArray()
        stays.filter { it.lat != null && it.lng != null }.forEach { stay ->
            val obj = JSONObject()
            obj.put("id", stay.id)
            obj.put("title", stay.title)
            obj.put("lat", stay.lat)
            obj.put("lng", stay.lng)
            obj.put("type", "Stay")
            array.put(obj)
        }
        safaris.filter { it.lat != null && it.lng != null }.forEach { safari ->
            val obj = JSONObject()
            obj.put("id", safari.id)
            obj.put("title", safari.title)
            obj.put("lat", safari.lat)
            obj.put("lng", safari.lng)
            obj.put("type", "Safari")
            array.put(obj)
        }
        array.toString()
    }

    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <title>Mapbox Map</title>
            <meta name="viewport" content="initial-scale=1,maximum-scale=1,user-scalable=no">
            <link href="https://api.mapbox.com/mapbox-gl-js/v3.1.2/mapbox-gl.css" rel="stylesheet">
            <script src="https://api.mapbox.com/mapbox-gl-js/v3.1.2/mapbox-gl.js"></script>
            <style>
                body { margin: 0; padding: 0; }
                #map { position: absolute; top: 0; bottom: 0; width: 100%; }
                .marker {
                    background-color: #D4AF37;
                    width: 15px;
                    height: 15px;
                    border-radius: 50%;
                    border: 2px solid white;
                    cursor: pointer;
                }
                .marker-safari {
                    background-color: #2E7D32;
                }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                mapboxgl.accessToken = '$mapboxToken';
                const map = new mapboxgl.Map({
                    container: 'map',
                    style: 'mapbox://styles/mapbox/outdoors-v12',
                    center: [36.8219, -1.2921], // Default center (Nairobi)
                    zoom: 5
                });
                
                const markersData = $markersJsonArray;
                
                if (markersData.length > 0) {
                    const bounds = new mapboxgl.LngLatBounds();
                    
                    markersData.forEach(function(marker) {
                        const el = document.createElement('div');
                        el.className = 'marker';
                        if (marker.type === 'Safari') {
                            el.classList.add('marker-safari');
                        }
                        
                        el.addEventListener('click', () => {
                            if (window.Android) {
                                window.Android.onMarkerClick(marker.id, marker.type);
                            }
                        });
                        
                        new mapboxgl.Marker(el)
                            .setLngLat([marker.lng, marker.lat])
                            .addTo(map);
                            
                        bounds.extend([marker.lng, marker.lat]);
                    });
                    
                    map.fitBounds(bounds, { padding: 50 });
                }
            </script>
        </body>
        </html>
    """.trimIndent()

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.cacheMode = WebSettings.LOAD_NO_CACHE
                    addJavascriptInterface(MapInterface(onMarkerClick), "Android")
                    webViewClient = WebViewClient()
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
                    loadDataWithBaseURL("https://api.mapbox.com/", htmlContent, "text/html", "UTF-8", null)
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL("https://api.mapbox.com/", htmlContent, "text/html", "UTF-8", null)
            }
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 2.dp
        ) {
            Text(
                text = "📍 Map View",
                fontSize = 11.sp,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
