package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.ui.components.ArrivalCard
import com.example.ui.components.CompassBearingHud
import com.example.ui.components.GolfCartMapView
import com.example.ui.components.ParkCartDialog
import com.example.ui.components.ThemeChooserDialog
import com.example.ui.theme.LocalFloridaTheme
import com.example.ui.viewmodel.CartFinderViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CartFinderViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val palette = uiState.floridaTheme.palette

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showParkDialog by remember { mutableStateOf(false) }
    var showConfirmClearDialog by remember { mutableStateOf(false) }
    var showPhotoArrivalDialog by remember { mutableStateOf(false) }
    var showFullscreenPhoto by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    // Location Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.onPermissionResult(fineGranted || coarseGranted)
    }

    LaunchedEffect(Unit) {
        val fineCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineCheck || coarseCheck) {
            viewModel.onPermissionResult(true)
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Show snackbar messages
    LaunchedEffect(uiState.message) {
        uiState.message?.let { msg ->
            scope.launch {
                snackbarHostState.showSnackbar(msg)
                viewModel.clearMessage()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(palette.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = palette.emoji,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "CartFinder",
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                color = palette.onSurface
                            )
                            Text(
                                text = "The Villages, FL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.primary
                            )
                        }
                    }
                },
                actions = {
                    // Compact Theme Selector Button (neat pill with active emoji & palette icon, keeping the title centered)
                    Surface(
                        onClick = { showThemeDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        color = palette.primaryContainer.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, palette.primary),
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(36.dp)
                            .testTag("open_theme_chooser_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = palette.emoji,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Change Color Theme",
                                tint = palette.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = palette.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Permission Rationale Banner (if denied)
            if (!uiState.hasLocationPermission) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("permission_warning")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Precise Location Required",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "CartFinder needs GPS location permission to pinpoint where you parked your golf cart in town squares, plaza lots, and streets, and lead you straight back.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("grant_permission_button")
                            ) {
                                Text("Grant GPS Permission")
                            }
                        }
                    }
                }
            }

            // GPS Signal Quality Pill
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(palette.primaryContainer.copy(alpha = 0.5f))
                        .border(1.dp, palette.cardBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val hasLock = uiState.currentLocation != null
                        Icon(
                            imageVector = if (hasLock) Icons.Default.GpsFixed else Icons.Default.GpsNotFixed,
                            contentDescription = null,
                            tint = if (hasLock) Color(0xFF16A34A) else Color(0xFFE76F51),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val accText = uiState.gpsAccuracyMeters?.let { "±${it.toInt()}m" } ?: "Searching..."
                        Text(
                            text = "GPS Status: $accText",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.onSurface
                        )
                    }

                    if (uiState.currentLocation != null) {
                        Text(
                            text = String.format(
                                Locale.US,
                                "%.4f, %.4f",
                                uiState.currentLocation!!.latitude,
                                uiState.currentLocation!!.longitude
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // STATE A: Golf Cart is NOT Parked
            if (uiState.parkedLocation == null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = palette.surface,
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, palette.cardBorder, RoundedCornerShape(24.dp))
                            .testTag("park_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Florida Sunshine Cart Badge
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(palette.secondaryContainer, palette.primaryContainer)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🛺",
                                    fontSize = 46.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "Parked in the Square or Plaza?",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                textAlign = TextAlign.Center,
                                color = palette.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Stepping into a town square, grocery store, restaurant, or doctor's office? Tap below to lock your exact GPS spot so you can walk straight back to your cart in crowded parking lots.",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF475569)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Giant "Park Golf Cart Here" Button
                            Button(
                                onClick = { showParkDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .shadow(6.dp, RoundedCornerShape(16.dp))
                                    .testTag("park_cart_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = palette.primary,
                                    contentColor = palette.onPrimary
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PinDrop,
                                    contentDescription = null,
                                    tint = palette.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Park Golf Cart Here",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = palette.onPrimary
                                )
                            }
                        }
                    }
                }
            } else {
                // STATE B: Cart IS Parked! (Locate / Return Mode)
                val parked = uiState.parkedLocation!!
                val timeParked = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(parked.timestamp))

                // Parked Status Banner Card
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = palette.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parked_status_banner")
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(palette.pinAccent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PinDrop,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = parked.spotLabel.ifBlank { "Golf Cart Parked" },
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = palette.onPrimary
                                        )
                                        Text(
                                            text = "Marked at $timeParked • GPS ±${parked.accuracyMeters.toInt()}m",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 13.sp,
                                            color = palette.onPrimary.copy(alpha = 0.85f)
                                        )
                                    }
                                }
                            }

                            if (parked.note.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Note: ${parked.note}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 14.sp,
                                    color = palette.onPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Google Maps walking navigation external intent button
                            OutlinedButton(
                                onClick = {
                                    val gmmIntentUri = Uri.parse(
                                        "google.navigation:q=${parked.latitude},${parked.longitude}&mode=w"
                                    )
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    try {
                                        context.startActivity(mapIntent)
                                    } catch (e: Exception) {
                                        val browserIntent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${parked.latitude},${parked.longitude}&travelmode=walking")
                                        )
                                        context.startActivity(browserIntent)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = palette.onPrimary
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.7f), Color.White))
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("open_external_maps_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsWalk,
                                    contentDescription = null,
                                    tint = palette.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Open Turn-by-Turn in Google Maps",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.onPrimary
                                )
                            }
                        }
                    }
                }

                // Optional Spot Photo Card (if user snapped a picture)
                if (parked.photoUriString != null) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = palette.surface,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, palette.cardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { showFullscreenPhoto = true }
                                .testTag("spot_photo_preview_card")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = null,
                                            tint = palette.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "SPOT PHOTO",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = palette.primary,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                    Text(
                                        text = "Tap to enlarge 🔍",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = palette.secondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                AsyncImage(
                                    model = parked.photoUriString,
                                    contentDescription = "Spot Photo Preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(190.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }
                        }
                    }
                }

                // Interactive Map View with clear golf cart pin & path
                item {
                    Text(
                        text = "MAP VIEW",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    GolfCartMapView(
                        currentLocation = uiState.currentLocation,
                        parkedLocation = uiState.parkedLocation,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    )
                }

                // Compass & Live Distance HUD
                item {
                    CompassBearingHud(
                        distanceMeters = uiState.distanceMeters,
                        bearingDegrees = uiState.bearingDegrees,
                        needleAngle = uiState.relativeNeedleAngle,
                        unit = uiState.preferredUnit,
                        onUnitClick = { viewModel.setPreferredUnit(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Arrival & Forget Location Card
                item {
                    ArrivalCard(
                        isArrived = uiState.isArrived,
                        onClearLocation = {
                            if (uiState.parkedLocation?.photoUriString != null) {
                                showPhotoArrivalDialog = true
                            } else {
                                showConfirmClearDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Bottom breathing room
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Theme Chooser Modal Dialog
    if (showThemeDialog) {
        ThemeChooserDialog(
            currentTheme = uiState.floridaTheme,
            onSelectTheme = { theme ->
                viewModel.setFloridaTheme(theme)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    // Park Cart Dialog
    if (showParkDialog) {
        ParkCartDialog(
            accuracyMeters = uiState.gpsAccuracyMeters,
            onRequestPhotoUri = { viewModel.createTempPhotoUri() },
            onConfirm = { label, note, photoUri ->
                viewModel.markCurrentLocationAsParked(label, note, photoUri)
                showParkDialog = false
            },
            onDismiss = { showParkDialog = false }
        )
    }

    // Fullscreen Photo Viewer Dialog
    if (showFullscreenPhoto && uiState.parkedLocation?.photoUriString != null) {
        Dialog(
            onDismissRequest = { showFullscreenPhoto = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
            ) {
                AsyncImage(
                    model = uiState.parkedLocation!!.photoUriString,
                    contentDescription = "Full Size Spot Photo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
                IconButton(
                    onClick = { showFullscreenPhoto = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(24.dp)
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.85f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Black
                    )
                }
            }
        }
    }

    // Standard Confirm Clear Dialog (when NO photo is attached)
    if (showConfirmClearDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showConfirmClearDialog = false },
            title = {
                Text(
                    text = "Forget This Parking Spot?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.onSurface
                )
            },
            text = {
                Text(
                    "This will clear your saved golf cart coordinates so you are ready to mark your next spot.",
                    fontSize = 16.sp,
                    color = Color(0xFF374151)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearParkedLocation()
                        showConfirmClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC2626),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("confirm_clear_button")
                ) {
                    Text("Forget Spot", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showConfirmClearDialog = false },
                    modifier = Modifier.testTag("cancel_clear_button")
                ) {
                    Text("Cancel", fontSize = 16.sp, color = Color(0xFF374151))
                }
            }
        )
    }

    // Photo Arrival Dialog (when a photo IS attached: choose to Delete or Save)
    if (showPhotoArrivalDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showPhotoArrivalDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp)),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = palette.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "You're at your cart!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.onSurface
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "You have a photo attached to this parking spot. Would you like to delete the photo or save a copy to your phone's photo gallery?",
                        fontSize = 15.sp,
                        color = Color(0xFF374151)
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    // Option 1: Delete photo (default/recommended for privacy and zero storage clutter)
                    Button(
                        onClick = {
                            viewModel.clearParkedLocation(savePhotoToGallery = false)
                            showPhotoArrivalDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDC2626),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 54.dp)
                            .testTag("delete_photo_arrival_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Delete Photo",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Permanently remove & clear spot",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 2: Save photo to gallery
                    Button(
                        onClick = {
                            viewModel.clearParkedLocation(savePhotoToGallery = true)
                            showPhotoArrivalDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = palette.primary,
                            contentColor = palette.onPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 54.dp)
                            .testTag("save_photo_arrival_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = palette.onPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Save Photo to Photos",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.onPrimary
                                )
                                Text(
                                    text = "Keep in phone gallery & clear spot",
                                    fontSize = 12.sp,
                                    color = palette.onPrimary.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showPhotoArrivalDialog = false },
                    modifier = Modifier.testTag("cancel_photo_dialog_button")
                ) {
                    Text("Keep Spot Active", fontSize = 16.sp, color = Color(0xFF374151))
                }
            }
        )
    }
}
