package com.example.data.repository

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.data.model.DistanceUnit
import com.example.data.model.GolfCartLocation
import com.example.ui.theme.FloridaTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class CartLocationRepository(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("golf_cart_finder_prefs", Context.MODE_PRIVATE)

    private val _currentParkedLocation = MutableStateFlow<GolfCartLocation?>(null)
    val currentParkedLocation: StateFlow<GolfCartLocation?> = _currentParkedLocation.asStateFlow()

    private val _preferredUnit = MutableStateFlow(DistanceUnit.YARDS)
    val preferredUnit: StateFlow<DistanceUnit> = _preferredUnit.asStateFlow()

    private val _floridaTheme = MutableStateFlow(FloridaTheme.CITRUS)
    val floridaTheme: StateFlow<FloridaTheme> = _floridaTheme.asStateFlow()

    init {
        // Clear any legacy history to respect user privacy preference
        if (prefs.contains("history_json")) {
            prefs.edit().remove("history_json").apply()
        }
        loadFromPrefs()
    }

    private fun loadFromPrefs() {
        val hasActive = prefs.getBoolean(KEY_HAS_ACTIVE, false)
        if (hasActive) {
            val id = prefs.getString(KEY_ID, "") ?: ""
            val lat = prefs.getString(KEY_LAT, "0.0")?.toDoubleOrNull() ?: 0.0
            val lng = prefs.getString(KEY_LNG, "0.0")?.toDoubleOrNull() ?: 0.0
            val acc = prefs.getFloat(KEY_ACC, 0f)
            val time = prefs.getLong(KEY_TIME, 0L)
            val label = prefs.getString(KEY_LABEL, "") ?: ""
            val note = prefs.getString(KEY_NOTE, "") ?: ""
            val photoUri = prefs.getString(KEY_PHOTO_URI, null)
            if (lat != 0.0 && lng != 0.0) {
                _currentParkedLocation.value = GolfCartLocation(
                    id = id,
                    latitude = lat,
                    longitude = lng,
                    accuracyMeters = acc,
                    timestamp = time,
                    spotLabel = label,
                    note = note,
                    photoUriString = photoUri
                )
            }
        } else {
            _currentParkedLocation.value = null
        }

        val unitName = prefs.getString(KEY_UNIT, DistanceUnit.YARDS.name) ?: DistanceUnit.YARDS.name
        _preferredUnit.value = try {
            DistanceUnit.valueOf(unitName)
        } catch (e: Exception) {
            DistanceUnit.YARDS
        }

        val themeId = prefs.getString(KEY_THEME, FloridaTheme.CITRUS.palette.id)
        _floridaTheme.value = FloridaTheme.fromId(themeId)
    }

    fun saveParkedLocation(location: GolfCartLocation) {
        _currentParkedLocation.value = location
        prefs.edit()
            .putBoolean(KEY_HAS_ACTIVE, true)
            .putString(KEY_ID, location.id)
            .putString(KEY_LAT, location.latitude.toString())
            .putString(KEY_LNG, location.longitude.toString())
            .putFloat(KEY_ACC, location.accuracyMeters)
            .putLong(KEY_TIME, location.timestamp)
            .putString(KEY_LABEL, location.spotLabel)
            .putString(KEY_NOTE, location.note)
            .putString(KEY_PHOTO_URI, location.photoUriString)
            .apply()
    }

    fun clearParkedLocation(deletePhoto: Boolean = true) {
        val currentPhoto = _currentParkedLocation.value?.photoUriString
        if (deletePhoto && !currentPhoto.isNullOrBlank()) {
            deletePhotoFile(currentPhoto)
        }

        _currentParkedLocation.value = null
        prefs.edit()
            .putBoolean(KEY_HAS_ACTIVE, false)
            .remove(KEY_ID)
            .remove(KEY_LAT)
            .remove(KEY_LNG)
            .remove(KEY_ACC)
            .remove(KEY_TIME)
            .remove(KEY_LABEL)
            .remove(KEY_NOTE)
            .remove(KEY_PHOTO_URI)
            .apply()
    }

    fun setPreferredUnit(unit: DistanceUnit) {
        _preferredUnit.value = unit
        prefs.edit().putString(KEY_UNIT, unit.name).apply()
    }

    fun setFloridaTheme(theme: FloridaTheme) {
        _floridaTheme.value = theme
        prefs.edit().putString(KEY_THEME, theme.palette.id).apply()
    }

    fun deletePhotoFile(photoUriString: String?) {
        if (photoUriString.isNullOrBlank()) return
        try {
            val uri = Uri.parse(photoUriString)
            // If file scheme or content scheme resolved to cache
            val file = if (uri.scheme == "file") {
                File(uri.path ?: "")
            } else {
                // Look in cacheDir/cart_photos/
                val lastSegment = uri.lastPathSegment ?: ""
                val photosDir = File(context.cacheDir, "cart_photos")
                File(photosDir, lastSegment)
            }
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            Log.e("CartLocationRepo", "Error deleting photo file", e)
        }
    }

    fun savePhotoToGallery(photoUriString: String?): Boolean {
        if (photoUriString.isNullOrBlank()) return false
        return try {
            val uri = Uri.parse(photoUriString)
            val inputStream = context.contentResolver.openInputStream(uri) ?: return false

            val filename = "CartFinder_${System.currentTimeMillis()}.jpg"
            val resolver = context.contentResolver

            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CartFinder")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val itemUri = resolver.insert(collection, values) ?: return false

            resolver.openOutputStream(itemUri)?.use { out ->
                inputStream.use { input ->
                    input.copyTo(out)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(itemUri, values, null, null)
            }
            true
        } catch (e: Exception) {
            Log.e("CartLocationRepo", "Error saving photo to gallery", e)
            false
        }
    }

    companion object {
        private const val KEY_HAS_ACTIVE = "has_active_location"
        private const val KEY_ID = "active_id"
        private const val KEY_LAT = "active_lat"
        private const val KEY_LNG = "active_lng"
        private const val KEY_ACC = "active_acc"
        private const val KEY_TIME = "active_time"
        private const val KEY_LABEL = "active_label"
        private const val KEY_NOTE = "active_note"
        private const val KEY_PHOTO_URI = "active_photo_uri"
        private const val KEY_UNIT = "pref_distance_unit"
        private const val KEY_THEME = "pref_florida_theme"
    }
}
