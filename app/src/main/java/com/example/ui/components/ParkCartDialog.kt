package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.theme.LocalFloridaTheme

@Composable
fun ParkCartDialog(
    accuracyMeters: Float?,
    onRequestPhotoUri: () -> Uri,
    onConfirm: (spotLabel: String, note: String, photoUriString: String?) -> Unit,
    onDismiss: () -> Unit
) {
    val palette = LocalFloridaTheme.current.palette

    var spotLabel by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingPhotoUri != null) {
            photoUri = pendingPhotoUri
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .clip(RoundedCornerShape(24.dp)),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(palette.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PinDrop,
                            contentDescription = null,
                            tint = palette.primary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Save Parking Spot",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = palette.onSurface
                        )
                        Text(
                            text = "Pins your exact spot in the square or lot",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp,
                            color = Color(0xFF4B5563)
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_park_dialog")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF4B5563)
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // High-visibility GPS Status Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.primaryContainer)
                        .border(1.5.dp, palette.cardBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = null,
                        tint = palette.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "GPS Signal: ±${(accuracyMeters ?: 3f).toInt()}m High Accuracy",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = palette.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Spot Label Input
                Text(
                    text = "Spot or Plaza Area:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = palette.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = spotLabel,
                    onValueChange = { spotLabel = it },
                    placeholder = {
                        Text(
                            "e.g. Row 3 outside Publix, Lake Sumter Square",
                            fontSize = 15.sp,
                            color = Color(0xFF6B7280)
                        )
                    },
                    textStyle = TextStyle(fontSize = 16.sp, color = palette.onSurface, fontWeight = FontWeight.Medium),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = palette.primary,
                        unfocusedBorderColor = Color(0xFF9CA3AF),
                        focusedContainerColor = Color(0xFFF9FAFB),
                        unfocusedContainerColor = Color(0xFFF9FAFB)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("spot_label_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Optional Cart details / row note
                Text(
                    text = "Row / Cart Note (Optional):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = palette.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = {
                        Text(
                            "e.g. Near light pole 12, red rental 4-seater",
                            fontSize = 15.sp,
                            color = Color(0xFF6B7280)
                        )
                    },
                    textStyle = TextStyle(fontSize = 16.sp, color = palette.onSurface),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = palette.primary,
                        unfocusedBorderColor = Color(0xFF9CA3AF),
                        focusedContainerColor = Color(0xFFF9FAFB),
                        unfocusedContainerColor = Color(0xFFF9FAFB)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cart_note_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Optional Photo Section
                Text(
                    text = "Spot Photo (Optional):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = palette.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (photoUri != null) {
                    // Photo attached card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0FDF4),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF86EFAC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = "Captured spot photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF16A34A), RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Photo Attached",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF15803D)
                                    )
                                }
                                Text(
                                    text = "Will be shown on your Find screen",
                                    fontSize = 12.sp,
                                    color = Color(0xFF374151)
                                )
                            }
                            // Retake button
                            IconButton(
                                onClick = {
                                    val uri = onRequestPhotoUri()
                                    pendingPhotoUri = uri
                                    cameraLauncher.launch(uri)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Retake Photo",
                                    tint = palette.primary
                                )
                            }
                            // Remove photo button
                            IconButton(onClick = { photoUri = null }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove Photo",
                                    tint = Color(0xFFDC2626)
                                )
                            }
                        }
                    }
                } else {
                    // Outlined button to take photo
                    OutlinedButton(
                        onClick = {
                            val uri = onRequestPhotoUri()
                            pendingPhotoUri = uri
                            cameraLauncher.launch(uri)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("take_photo_button"),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, palette.primary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = palette.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = palette.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Take Photo of Spot (Optional)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = palette.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(spotLabel, note, photoUri?.toString()) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.primary,
                    contentColor = palette.onPrimary
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("confirm_park_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = palette.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Save Parking Spot",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    color = palette.onPrimary
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE5E7EB),
                    contentColor = Color(0xFF111827)
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF9CA3AF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("cancel_park_button")
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
            }
        }
    )
}
