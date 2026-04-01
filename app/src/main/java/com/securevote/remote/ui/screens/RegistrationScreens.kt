package com.securevote.remote.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.securevote.remote.ui.theme.*

// ================================================================
// DURESS PIN SETUP SCREEN
// ================================================================

/**
 * Duress PIN Setup — part of the registration flow.
 *
 * The voter creates a secondary PIN that, if entered during voting,
 * makes the system APPEAR to accept the ballot normally while
 * internally flagging it as cast under duress.
 *
 * DESIGN DECISION (from ABSENTEE.md):
 *   The duress PIN is set at registration time, NOT later.
 *   If it could be set at any time, a coercer could demand the voter
 *   set it in front of them to prove it's not already set.
 *   By setting it during registration (before any coercion scenario),
 *   the voter can truthfully say "I don't remember setting anything
 *   like that" if confronted.
 */
@Composable
fun DuressPinSetupScreen(
    onPinSet: (pin: String) -> Unit,
    onSkipInfo: () -> Unit,
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var phase by remember { mutableIntStateOf(0) }  // 0=explain, 1=enter, 2=confirm
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StepIndicator(step = 3, total = 5, label = "Registration")

        Spacer(Modifier.height(24.dp))

        when (phase) {
            // ---- Explanation Phase ----
            0 -> {
                Icon(
                    Icons.Filled.Shield,
                    contentDescription = null,
                    tint = CivicBlue,
                    modifier = Modifier.size(56.dp)
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "Set Your Safety PIN",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = CivicLight),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "What is this?",
                            fontWeight = FontWeight.Bold,
                            color = Navy,
                            fontSize = 15.sp,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "This is a separate PIN from your Election PIN. " +
                            "If someone ever forces you to vote a certain way, " +
                            "enter this Safety PIN instead of your real Election PIN.",
                            color = Navy,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "What happens?",
                            fontWeight = FontWeight.Bold,
                            color = Navy,
                            fontSize = 15.sp,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "The system will appear to accept your vote completely normally. " +
                            "A success screen, a confirmation code, a receipt — everything looks real. " +
                            "But the ballot is secretly flagged and will not be counted. " +
                            "You can then revote later with your real PIN when you are safe.",
                            color = Navy,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        )
                        Spacer(Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AmberLight),
                            shape = RoundedCornerShape(10.dp),
                        ) {
                            Row(modifier = Modifier.padding(10.dp)) {
                                Text("⚠️", fontSize = 16.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Choose a PIN you'll remember but that is DIFFERENT from your Election PIN. " +
                                    "Do not share this PIN with anyone.",
                                    fontSize = 12.sp,
                                    color = DarkGray,
                                    lineHeight = 16.sp,
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { phase = 1 },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        "Set My Safety PIN",
                        modifier = Modifier.padding(vertical = 4.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // ---- Enter PIN Phase ----
            1 -> {
                Text(
                    "Choose Your Safety PIN",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "6 digits — must be different from your Election PIN",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(32.dp))

                PinDots(pinLength = pin.length, total = 6)

                Spacer(Modifier.height(24.dp))

                NumericKeypad(
                    onKey = { key ->
                        if (key == "del") {
                            pin = pin.dropLast(1)
                        } else if (pin.length < 6) {
                            pin += key
                            if (pin.length == 6) {
                                phase = 2  // Move to confirm
                            }
                        }
                    }
                )

                error?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, color = Crimson, fontSize = 13.sp)
                }
            }

            // ---- Confirm PIN Phase ----
            2 -> {
                Text(
                    "Confirm Your Safety PIN",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Enter the same 6-digit PIN again",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(32.dp))

                PinDots(pinLength = confirmPin.length, total = 6)

                Spacer(Modifier.height(24.dp))

                NumericKeypad(
                    onKey = { key ->
                        if (key == "del") {
                            confirmPin = confirmPin.dropLast(1)
                            error = null
                        } else if (confirmPin.length < 6) {
                            confirmPin += key
                            if (confirmPin.length == 6) {
                                if (confirmPin == pin) {
                                    onPinSet(pin)
                                } else {
                                    error = "PINs don't match. Try again."
                                    confirmPin = ""
                                }
                            }
                        }
                    }
                )

                error?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, color = Crimson, fontSize = 13.sp)
                }
            }
        }
    }
}

// ================================================================
// REGISTRATION PHOTOS SCREEN
// ================================================================

/**
 * Registration Photos — captures the three binding photos:
 *   1. Selfie (with liveness check)
 *   2. Front of ID
 *   3. Voter holding ID next to face (the critical binding photo)
 *
 * From ABSENTEE.md:
 *   "A stolen ID and a stolen selfie cannot be combined to fake
 *   the holding photo, because the composite scene must be spatially
 *   coherent (correct lighting, perspective, hand position, face
 *   angle relative to card)."
 */
@Composable
fun RegistrationPhotosScreen(
    onPhotosComplete: () -> Unit,
    onBack: () -> Unit,
) {
    var currentPhoto by remember { mutableIntStateOf(0) }  // 0=selfie, 1=ID, 2=holding
    var photoCaptured by remember { mutableStateOf(booleanArrayOf(false, false, false)) }

    val photos = listOf(
        PhotoStep(
            title = "Take a Selfie",
            instruction = "Look directly at the camera. You'll be asked to blink and turn your head.",
            icon = Icons.Filled.Face,
            hint = "Liveness challenge will run automatically",
        ),
        PhotoStep(
            title = "Photograph Your ID",
            instruction = "Hold your ID steady and photograph the front. Make sure all text is readable.",
            icon = Icons.Filled.CreditCard,
            hint = "ID text must be clearly legible",
        ),
        PhotoStep(
            title = "Hold ID Next to Your Face",
            instruction = "Hold your ID next to your face so both are visible in the same photo. " +
                         "This proves you, your device, and your ID are all in the same place.",
            icon = Icons.Filled.VerifiedUser,
            hint = "Both your face and your ID must be clearly visible",
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StepIndicator(step = 4, total = 5, label = "Registration")

        Spacer(Modifier.height(16.dp))

        // Photo progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            photos.forEachIndexed { i, photo ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    photoCaptured[i] -> ForestGreen
                                    i == currentPhoto -> CivicBlue
                                    else -> LightGray
                                }
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (photoCaptured[i]) {
                            Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(photo.icon, null, tint = if (i == currentPhoto) Color.White else MedGray, modifier = Modifier.size(24.dp))
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${i + 1}/3",
                        fontSize = 10.sp,
                        color = if (i == currentPhoto) CivicBlue else MedGray,
                        fontWeight = if (i == currentPhoto) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        val step = photos[currentPhoto]

        // Camera preview area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1a1a2e))
                .border(2.dp, CivicBlue, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            // In production: CameraX preview composable here
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(step.icon, null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(8.dp))
                Text("Camera Preview", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
                Text("(Simulated)", color = Color.White.copy(alpha = 0.3f), fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(step.title, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        Spacer(Modifier.height(4.dp))
        Text(step.instruction, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, lineHeight = 20.sp)

        Spacer(Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = CivicLight),
            shape = RoundedCornerShape(10.dp),
        ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("💡", fontSize = 14.sp)
                Spacer(Modifier.width(8.dp))
                Text(step.hint, fontSize = 12.sp, color = Navy)
            }
        }

        Spacer(Modifier.weight(1f))

        // Capture button
        Button(
            onClick = {
                val updated = photoCaptured.copyOf()
                updated[currentPhoto] = true
                photoCaptured = updated

                if (currentPhoto < 2) {
                    currentPhoto++
                } else {
                    // All three photos captured
                    onPhotosComplete()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                if (currentPhoto == 2 && !photoCaptured[2]) "Capture & Complete Registration"
                else "Capture Photo ${currentPhoto + 1}",
                modifier = Modifier.padding(vertical = 4.dp),
                fontWeight = FontWeight.Bold,
            )
        }

        if (currentPhoto > 0) {
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = {
                currentPhoto--
                val updated = photoCaptured.copyOf()
                updated[currentPhoto] = false
                photoCaptured = updated
            }) {
                Text("← Retake previous photo", color = MedGray, fontSize = 13.sp)
            }
        }
    }
}

private data class PhotoStep(
    val title: String,
    val instruction: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val hint: String,
)

// ================================================================
// SHARED COMPONENTS
// ================================================================

@Composable
fun PinDots(pinLength: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        (0 until total).forEach { i ->
            Box(
                modifier = Modifier
                    .size(44.dp, 52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        2.dp,
                        if (pinLength > i) CivicBlue else Color(0xFFDDDDDD),
                        RoundedCornerShape(12.dp)
                    )
                    .background(if (pinLength > i) CivicLight else Color.White),
                contentAlignment = Alignment.Center,
            ) {
                if (pinLength > i) {
                    Box(
                        Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(CivicBlue)
                    )
                }
            }
        }
    }
}

@Composable
fun NumericKeypad(onKey: (String) -> Unit) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "del"),
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        keys.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { key ->
                    Box(
                        modifier = Modifier
                            .size(72.dp, 56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .then(
                                if (key.isNotEmpty()) Modifier
                                    .background(LightGray)
                                    .clickable { onKey(key) }
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (key == "del") {
                            Icon(Icons.Filled.Backspace, null, tint = Navy, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                key,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                color = Navy,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
