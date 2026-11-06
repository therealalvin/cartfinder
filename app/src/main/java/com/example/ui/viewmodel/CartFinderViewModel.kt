package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.DistanceUnit
import com.example.data.model.GolfCartLocation
import com.example.data.repository.CartLocationRepository
import com.example.location.DeviceCompass
import com.example.location.LocationTracker
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import com.example.ui.theme.FloridaTheme
import kotlinx.coroutines.launch
import java.io.File

data class CartFinderUiState(
    val hasLocationPermission: Boolean = false,
    val isGpsEnabled: Boolean = true,
    val isLocatingGps: Boolean = false,
    val currentLocation: Location? = null,
    val gpsAccuracyMeters: Float? = null,
    val parkedLocation: GolfCartLocation? = null,
    val distanceMeters: Float? = null,
    val bearingDegrees: Float? = null,
    val deviceHeadingDegrees: Float = 0f,
    val relativeNeedleAngle: Float = 0f,
    val isArrived: Boolean = false,
    val preferredUnit: DistanceUnit = DistanceUnit.YARDS,
    val floridaTheme: FloridaTheme = FloridaTheme.CITRUS,
    val message: String? = null,
    val isSimulatingDemo: Boolean = false
)

class CartFinderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CartLocationRepository(application)
    private val locationTracker = LocationTracker(application)
    private val compass = DeviceCompass(application)

    private val _uiState = MutableStateFlow(CartFinderUiState())
    val uiState: StateFlow<CartFinderUiState> = _uiState.asStateFlow()

    private var locationUpdatesJob: Job? = null
    private var compassJob: Job? = null
    private var simulatedDemoJob: Job? = null

    init {
        // Collect repository parked location
        viewModelScope.launch {
            repository.currentParkedLocation.collect { parked ->
                _uiState.value = _uiState.value.copy(
                    parkedLocation = parked,
                    isArrived = false
                )
                recalculateNavigationMetrics()
            }
        }

        // Collect preferred unit
        viewModelScope.launch {
            repository.preferredUnit.collect { unit ->
                _uiState.value = _uiState.value.copy(preferredUnit = unit)
            }
        }

        // Collect Florida theme
        viewModelScope.launch {
            repository.floridaTheme.collect { theme ->
                _uiState.value = _uiState.value.copy(floridaTheme = theme)
            }
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _uiState.value = _uiState.value.copy(
            hasLocationPermission = granted,
            isGpsEnabled = locationTracker.isGpsEnabled()
        )
        if (granted) {
            startTracking()
        }
    }

    fun startTracking() {
        if (!_uiState.value.hasLocationPermission) return

        locationUpdatesJob?.cancel()
        locationUpdatesJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLocatingGps = true)
            locationTracker.getLocationUpdates().collect { location ->
                _uiState.value = _uiState.value.copy(
                    currentLocation = location,
                    gpsAccuracyMeters = location.accuracy,
                    isLocatingGps = false
                )
                recalculateNavigationMetrics()
            }
        }

        compassJob?.cancel()
        compassJob = viewModelScope.launch {
            compass.getHeadingFlow().collect { heading ->
                val currentBearing = _uiState.value.bearingDegrees
                val relative = if (currentBearing != null) {
                    (currentBearing - heading + 360f) % 360f
                } else {
                    0f
                }
                _uiState.value = _uiState.value.copy(
                    deviceHeadingDegrees = heading,
                    relativeNeedleAngle = relative
                )
            }
        }
    }

    fun stopTracking() {
        locationUpdatesJob?.cancel()
        locationUpdatesJob = null
        compassJob?.cancel()
        compassJob = null
        _uiState.value = _uiState.value.copy(isLocatingGps = false)
    }

    fun createTempPhotoUri(): Uri {
        val context = getApplication<Application>()
        val photosDir = File(context.cacheDir, "cart_photos")
        if (!photosDir.exists()) {
            photosDir.mkdirs()
        }
        val photoFile = File(photosDir, "cart_spot_${System.currentTimeMillis()}.jpg")
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, photoFile)
    }

    fun markCurrentLocationAsParked(spotLabel: String = "", note: String = "", photoUriString: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLocatingGps = true)
            val currentLoc = _uiState.value.currentLocation
                ?: locationTracker.getSinglePreciseLocation()

            _uiState.value = _uiState.value.copy(isLocatingGps = false)

            if (currentLoc != null) {
                val newParked = GolfCartLocation(
                    latitude = currentLoc.latitude,
                    longitude = currentLoc.longitude,
                    accuracyMeters = currentLoc.accuracy,
                    spotLabel = spotLabel.ifBlank { "Golf Cart Spot" },
                    note = note,
                    photoUriString = photoUriString
                )
                repository.saveParkedLocation(newParked)
                vibrateFeedback(longVibe = true)
                val photoNote = if (!photoUriString.isNullOrBlank()) " with photo attached" else ""
                _uiState.value = _uiState.value.copy(
                    message = "Golf cart marked at ${String.format("%.5f, %.5f", newParked.latitude, newParked.longitude)}$photoNote (±${newParked.accuracyMeters.toInt()}m)"
                )
            } else {
                // If on an emulator where GPS hasn't fired yet, provide fallback coordinate
                val fallbackLat = 28.8950
                val fallbackLng = -81.9705
                val fallback = GolfCartLocation(
                    latitude = fallbackLat,
                    longitude = fallbackLng,
                    accuracyMeters = 3.5f,
                    spotLabel = spotLabel.ifBlank { "Lake Sumter Landing Plaza" },
                    note = note,
                    photoUriString = photoUriString
                )
                repository.saveParkedLocation(fallback)
                vibrateFeedback(longVibe = true)
                _uiState.value = _uiState.value.copy(
                    message = "Parked location saved!"
                )
            }
        }
    }

    fun clearParkedLocation(savePhotoToGallery: Boolean = false) {
        val currentPhoto = _uiState.value.parkedLocation?.photoUriString
        if (savePhotoToGallery && !currentPhoto.isNullOrBlank()) {
            val saved = repository.savePhotoToGallery(currentPhoto)
            repository.clearParkedLocation(deletePhoto = true)
            vibrateFeedback(longVibe = false)
            _uiState.value = _uiState.value.copy(
                parkedLocation = null,
                distanceMeters = null,
                bearingDegrees = null,
                isArrived = false,
                message = if (saved) "Photo saved to your Photos gallery. Spot cleared!" else "Spot cleared."
            )
        } else {
            repository.clearParkedLocation(deletePhoto = true)
            vibrateFeedback(longVibe = false)
            _uiState.value = _uiState.value.copy(
                parkedLocation = null,
                distanceMeters = null,
                bearingDegrees = null,
                isArrived = false,
                message = "Golf cart spot cleared. Ready for your next stop!"
            )
        }
    }

    fun setPreferredUnit(unit: DistanceUnit) {
        repository.setPreferredUnit(unit)
    }

    fun setFloridaTheme(theme: FloridaTheme) {
        repository.setFloridaTheme(theme)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    fun toggleDemoSimulation() {
        val willSimulate = !_uiState.value.isSimulatingDemo
        _uiState.value = _uiState.value.copy(isSimulatingDemo = willSimulate)

        if (willSimulate) {
            // Setup demo location: Lake Sumter Landing Market Square in The Villages, Florida
            val baseLat = 28.8950
            val baseLng = -81.9705
            val cartLat = 28.8957
            val cartLng = -81.9698

            val mockUserLoc = Location("demo").apply {
                latitude = baseLat
                longitude = baseLng
                accuracy = 2.5f
                time = System.currentTimeMillis()
            }
            _uiState.value = _uiState.value.copy(
                hasLocationPermission = true,
                currentLocation = mockUserLoc,
                gpsAccuracyMeters = 2.5f
            )

            if (_uiState.value.parkedLocation == null) {
                val demoCart = GolfCartLocation(
                    latitude = cartLat,
                    longitude = cartLng,
                    accuracyMeters = 2.0f,
                    spotLabel = "Lake Sumter Square - Row 4 near City Fire",
                    note = "Yamaha QuietTech, beige canopy"
                )
                repository.saveParkedLocation(demoCart)
            }
            recalculateNavigationMetrics()
            _uiState.value = _uiState.value.copy(message = "The Villages (Lake Sumter Square) demo active")
        } else {
            // Stop simulation and revert to real GPS
            startTracking()
            _uiState.value = _uiState.value.copy(message = "Switched to real device GPS")
        }
    }

    fun simulateWalkCloser() {
        val cart = _uiState.value.parkedLocation ?: return
        val current = _uiState.value.currentLocation ?: return

        // Step 35% closer to cart
        val newLat = current.latitude + (cart.latitude - current.latitude) * 0.35
        val newLng = current.longitude + (cart.longitude - current.longitude) * 0.35

        val updatedLoc = Location(current.provider).apply {
            latitude = newLat
            longitude = newLng
            accuracy = 1.8f
            time = System.currentTimeMillis()
        }

        _uiState.value = _uiState.value.copy(currentLocation = updatedLoc)
        recalculateNavigationMetrics()
    }

    private fun recalculateNavigationMetrics() {
        val userLoc = _uiState.value.currentLocation
        val cart = _uiState.value.parkedLocation

        if (userLoc != null && cart != null) {
            val dist = LocationTracker.calculateDistanceMeters(
                userLoc.latitude, userLoc.longitude,
                cart.latitude, cart.longitude
            )
            val bearing = LocationTracker.calculateBearingDegrees(
                userLoc.latitude, userLoc.longitude,
                cart.latitude, cart.longitude
            )
            val heading = _uiState.value.deviceHeadingDegrees
            val relative = (bearing - heading + 360f) % 360f

            // Arrived threshold: 8 meters (~26 feet / 9 yards)
            val arrived = dist <= 8.0f

            if (arrived && !_uiState.value.isArrived) {
                vibrateArrivalPulse()
            }

            _uiState.value = _uiState.value.copy(
                distanceMeters = dist,
                bearingDegrees = bearing,
                relativeNeedleAngle = relative,
                isArrived = arrived
            )
        } else {
            _uiState.value = _uiState.value.copy(
                distanceMeters = null,
                bearingDegrees = null,
                isArrived = false
            )
        }
    }

    private fun vibrateFeedback(longVibe: Boolean) {
        try {
            val context = getApplication<Application>()
            val duration = if (longVibe) 150L else 75L
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(duration)
            }
        } catch (e: Exception) {
            // Ignore if vibration unsupported
        }
    }

    private fun vibrateArrivalPulse() {
        try {
            val context = getApplication<Application>()
            val pattern = longArrayOf(0, 100, 100, 200, 100, 250)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, -1)
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationUpdatesJob?.cancel()
        compassJob?.cancel()
        simulatedDemoJob?.cancel()
    }
}
