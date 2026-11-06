package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalFloridaTheme

@Composable
fun ArrivalCard(
    isArrived: Boolean,
    onClearLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalFloridaTheme.current.palette

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                2.dp,
                if (isArrived) palette.primary else palette.cardBorder,
                RoundedCornerShape(20.dp)
            )
            .testTag("arrival_card"),
        color = if (isArrived) palette.primaryContainer else palette.surface,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isArrived) palette.primary else palette.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isArrived) Icons.Default.Check else Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = if (isArrived) palette.onPrimary else palette.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isArrived) "You're At Your Golf Cart!" else "Back at your golf cart?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.onSurface
                    )
                    Text(
                        text = if (isArrived)
                            "Clear this spot now so you can mark your next stop around town."
                        else
                            "Forget saved coordinates when you're done driving.",
                        fontSize = 14.sp,
                        color = Color(0xFF4B5563)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClearLocation,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("clear_parked_spot_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isArrived) palette.primary else Color(0xFFDC2626),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "I'm Here — Forget This Spot",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}
