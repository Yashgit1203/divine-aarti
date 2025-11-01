package com.example.divineaarti

import android.app.Application
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache

class DivineAartiApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Coil for optimal image loading
        val imageLoader = ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .respectCacheHeaders(false)
            .build()

        coil.Coil.setImageLoader(imageLoader)
    }
}