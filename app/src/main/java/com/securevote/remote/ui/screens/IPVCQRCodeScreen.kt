package com.securevote.remote.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.securevote.remote.crypto.IPVCQRGenerator
import com.securevote.remote.ui.theme.*
import kotlinx.coroutines.delay

/**
 * IPVC QR Code Verification Screen.
 *
 * Displayed when biometric verification fails on-device.
 * Shows a rotating QR code that an IPVC admin must scan while
 * physically present with the voter.
 *
 * SECURITY:
 *   - QR regenerates every 10 seconds with fresh nonce + HMAC
 *   - Countdown timer shows remaining validity
 *   - FLAG_SECURE prevents screenshots (set on Activity window)
 *   - Admin must be physically present to scan before expiry
 *   - Even a photo of the QR expires in ≤10 seconds
 */
@Composable
fun IPVCQRCodeScreen(
    voterDocumentHash: String,
    electionId: String,
    deviceKeyMaterial: ByteArray,
    onVerificationComplete: () -> Unit,
    onCancel: () -> Unit,
) {
    var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var countdown by remember { mutableIntStateOf(10) }
    var qrSequence by remember { mutableLongStateOf(0L) }

    // Collect rotating QR codes from the generator flow
    LaunchedEffect(Unit) {
        IPVCQRGenerator.generateRotatingQRCodes(
            voterDocumentHash = voterDocumentHash,
            electionId = electionId,
            deviceKeyMaterial = deviceKeyMaterial,
        ).collect { frame ->
            currentBitmap = frame.bitmap
            qrSequence = frame.sequenceNumber
            countdown = 10
        }
    }

    // Countdown timer (ticks every second)
    LaunchedEffect(qrSequence) {
        for (i in 10 downTo 0) {
            countdown = i
            delay(1000)
        }
    }

    // Countdown progress animation
    val progress by animateFloatAsState(
        targetValue = countdown / 10f,
        animationSpec = tween(durationMillis = 900, easing = LinearEasing),
        label = "countdown"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Text(
            text = "In-Person Verification Required",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Show this QR code to the verification agent.\nThey must scan it while standing with you.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // QR Code Container with countdown ring
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(300.dp),
        ) {
            // Countdown ring background
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.size(300.dp),
                color = LightGray,
                strokeWidth = 4.dp,
            )

            // Countdown ring foreground (depletes as time runs out)
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(300.dp),
                color = when {
                    countdown > 5 -> CivicBlue
                    countdown > 2 -> Amber
                    else -> Crimson
                },
                strokeWidth = 4.dp,
            )

            // QR Code
            if (currentBitmap != null) {
                Image(
                    bitmap = currentBitmap!!.asImageBitmap(),
                    contentDescription = "IPVC Verification QR Code",
                    modifier = Modifier
                        .size(260.dp)
                        .clip(RoundedCornerShape(12.dp)),
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = CivicBlue,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Countdown display
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            countdown > 5 -> ForestGreen
                            countdown > 2 -> Amber
                            else -> Crimson
                        }
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "New code in ${countdown}s",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    color = when {
                        countdown > 5 -> ForestGreen
                        countdown > 2 -> Amber
                        else -> Crimson
                    }
                ),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Security notice
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AmberLight),
            shape = RoundedCornerShape(12.dp),
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text("🔒", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "This code rotates every 10 seconds and cannot be screenshotted. The verification agent must be physically present to scan it.",
                    style = MaterialTheme.typography.bodySmall.copy(color = DarkGray),
                    lineHeight = 16.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // IPVC locations hint
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CivicLight),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Verification available at:",
                    style = MaterialTheme.typography.labelLarge.copy(color = Navy),
                )
                Spacer(modifier = Modifier.height(4.dp))
                val locations = listOf(
                    "🏦 Banks",
                    "📮 Post Offices",
                    "🏛️ County Election Offices",
                    "📚 Public Libraries",
                    "🏢 US Embassies & Consulates"
                )
                locations.forEach { loc ->
                    Text(
                        text = loc,
                        style = MaterialTheme.typography.bodySmall.copy(color = Navy),
                        modifier = Modifier.padding(vertical = 1.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Cancel button
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = "Cancel — I'll vote in person",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
    }
}
