package com.securevote.remote.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.securevote.remote.crypto.SVRCrypto
import com.securevote.remote.ui.theme.*
import kotlinx.coroutines.delay

// ================================================================
// SPLASH SCREEN
// ================================================================

@Composable
fun SplashScreen(onBeginVoting: () -> Unit, onVerify: () -> Unit = {}) {
    val alpha by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Navy, Color(0xFF0F1A33)))),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(CivicBlue, ForestGreen))),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
            }

            Spacer(Modifier.height(24.dp))
            Text("SecureVote", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Text("REMOTE", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp, letterSpacing = 3.sp)

            Spacer(Modifier.height(48.dp))

            Button(
                onClick = onBeginVoting,
                modifier = Modifier.width(280.dp).height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CivicBlue),
            ) {
                Text("Begin Voting", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onVerify,
                modifier = Modifier.width(280.dp).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
            ) {
                Icon(Icons.Filled.Verified, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Verify My Vote", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }

            Spacer(Modifier.height(16.dp))
            Text("General Election 2026", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)

            Spacer(Modifier.height(80.dp))
            Text(
                "End-to-end encrypted · Post-quantum secure · Open source",
                color = Color.White.copy(alpha = 0.2f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

// ================================================================
// NFC SCAN SCREEN
// ================================================================

@Composable
fun NFCScanScreen(onScanComplete: () -> Unit, onNoNFC: () -> Unit) {
    var phase by remember { mutableIntStateOf(0) } // 0=prompt, 1=scanning, 2=done

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        StepIndicator(step = 1, total = 4, label = "Verify Identity")

        Spacer(Modifier.height(40.dp))

        when (phase) {
            0 -> {
                Icon(Icons.Filled.Nfc, contentDescription = null, tint = CivicBlue, modifier = Modifier.size(80.dp))
                Spacer(Modifier.height(24.dp))
                Text("Tap Your ID", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Text("Hold the back of your phone against your government-issued photo ID to read the NFC chip.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                Spacer(Modifier.height(32.dp))
                Button(onClick = { phase = 1 }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Text("Simulate NFC Tap", modifier = Modifier.padding(vertical = 4.dp))
                }
                // Simulate NFC read delay
                LaunchedEffect(phase) {
                    if (phase == 1) {
                        delay(2500)
                        phase = 2
                        delay(1000)
                        onScanComplete()
                    }
                }
            }
            1 -> {
                CircularProgressIndicator(color = CivicBlue, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(24.dp))
                Text("Reading NFC Chip...", style = MaterialTheme.typography.headlineSmall)
                Text("Verifying ICAO 9303 signature", style = MaterialTheme.typography.bodyMedium)
                LaunchedEffect(Unit) { delay(2500); phase = 2; delay(1000); onScanComplete() }
            }
            2 -> {
                Box(Modifier.size(80.dp).clip(CircleShape).background(GreenLight), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(48.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text("ID Verified", style = MaterialTheme.typography.headlineMedium.copy(color = ForestGreen))
                Text("NFC chip signature valid", style = MaterialTheme.typography.bodyMedium)

                LaunchedEffect(Unit) {
                    delay(1200)
                    onScanComplete()
                }
            }
        }
    }
}

// ================================================================
// BIOMETRIC SCREEN
// ================================================================

@Composable
fun BiometricScreen(onVerified: () -> Unit, onUncertain: () -> Unit, onRejected: () -> Unit) {
    var phase by remember { mutableIntStateOf(0) }
    var challenge by remember { mutableStateOf("Position your face in the oval") }

    LaunchedEffect(Unit) {
        delay(1500); phase = 1; challenge = "Please blink"
        delay(2000); challenge = "Turn head slightly left"
        delay(2000); challenge = "Smile"
        delay(1500); phase = 2
        delay(1200); onVerified()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        StepIndicator(step = 2, total = 4, label = "Verify Identity")
        Spacer(Modifier.height(32.dp))

        when (phase) {
            0, 1 -> {
                Box(
                    modifier = Modifier.size(220.dp, 280.dp).clip(RoundedCornerShape(50))
                        .border(3.dp, CivicBlue, RoundedCornerShape(50))
                        .background(CivicLight),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Face, contentDescription = null, tint = CivicBlue.copy(alpha = 0.3f), modifier = Modifier.size(100.dp))
                }
                Spacer(Modifier.height(24.dp))
                Card(colors = CardDefaults.cardColors(containerColor = CivicLight), shape = RoundedCornerShape(20.dp)) {
                    Text(challenge, modifier = Modifier.padding(12.dp, 8.dp), fontWeight = FontWeight.SemiBold, color = CivicBlue)
                }
                Spacer(Modifier.height(8.dp))
                Text("Dual-model matching active", style = MaterialTheme.typography.bodySmall)

                // Show IPVC fallback button after a delay
                Spacer(Modifier.height(24.dp))
                TextButton(onClick = onUncertain) {
                    Text("Having trouble? Get in-person verification →", fontSize = 12.sp, color = MedGray)
                }
            }
            2 -> {
                Box(Modifier.size(80.dp).clip(CircleShape).background(GreenLight), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(48.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text("Identity Verified", style = MaterialTheme.typography.headlineMedium.copy(color = ForestGreen))
                Text("Model A ✓ · Model B ✓ · Liveness confirmed", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// ================================================================
// PIN ENTRY SCREEN
// ================================================================

@Composable
fun PINEntryScreen(onPinVerified: (isDuress: Boolean) -> Unit) {
    var pin by remember { mutableStateOf("") }
    var attempts by remember { mutableIntStateOf(3) }

    // Duress PIN detection would happen here — compare hash against stored duress hash
    val isDuress = false  // In production: check against encrypted duress PIN hash

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StepIndicator(step = 3, total = 4, label = "Verify Identity")
        Spacer(Modifier.height(40.dp))

        Text("Enter Election PIN", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text("The PIN mailed to your registered address", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(32.dp))

        // PIN dots
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            (0 until 6).forEach { i ->
                Box(
                    modifier = Modifier.size(44.dp, 52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, if (pin.length > i) CivicBlue else Color(0xFFDDD), RoundedCornerShape(12.dp))
                        .background(if (pin.length > i) CivicLight else Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    if (pin.length > i) {
                        Box(Modifier.size(14.dp).clip(CircleShape).background(CivicBlue))
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Numeric keypad
        val keys = listOf(listOf("1","2","3"), listOf("4","5","6"), listOf("7","8","9"), listOf("","0","⌫"))
        keys.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { key ->
                    Box(
                        modifier = Modifier
                            .size(72.dp, 56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .then(if (key.isNotEmpty()) Modifier.background(LightGray).clickable {
                                if (key == "⌫") {
                                    pin = pin.dropLast(1)
                                } else if (pin.length < 6) {
                                    pin += key
                                    if (pin.length == 6) {
                                        // PIN complete — verify
                                        onPinVerified(isDuress)
                                    }
                                }
                            } else Modifier),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(key, fontSize = if (key == "⌫") 20.sp else 24.sp, fontWeight = FontWeight.Medium, color = Navy)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(16.dp))
        Text("$attempts attempts remaining", style = MaterialTheme.typography.bodySmall)
    }
}

// ================================================================
// TOKEN ISSUANCE SCREEN
// ================================================================

@Composable
fun TokenIssuanceScreen(onTokenIssued: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(600); step = 1
        delay(1000); step = 2
        delay(1000); step = 3
        delay(1000); step = 4
        delay(800); onTokenIssued()
    }

    val steps = listOf(
        "Generating random token..." to "🎲",
        "Blinding token (RSA)..." to "🔒",
        "Server signing blind token..." to "✍️",
        "Unblinding — token ready" to "✅",
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Filled.Token, contentDescription = null, tint = CivicBlue, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(16.dp))
        Text("Issuing Anonymous Token", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))

        steps.forEachIndexed { i, (label, icon) ->
            Card(
                modifier = Modifier.fillMaxWidth().alpha(if (step >= i) 1f else 0.3f),
                colors = CardDefaults.cardColors(containerColor = if (step >= i) CivicLight else LightGray),
                shape = RoundedCornerShape(10.dp),
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(if (step >= i) icon else "⏳", fontSize = 20.sp)
                    Spacer(Modifier.width(12.dp))
                    Text(label, fontWeight = if (step == i) FontWeight.SemiBold else FontWeight.Normal, color = if (step >= i) Navy else MedGray)
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(24.dp))
        Card(colors = CardDefaults.cardColors(containerColor = LightGray), shape = RoundedCornerShape(10.dp)) {
            Text(
                "Your identity is now decoupled from your ballot. The authentication server signed your token without seeing it.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}

// ================================================================
// BALLOT RACE SCREEN
// ================================================================

@Composable
fun BallotRaceScreen(
    raceIndex: Int,
    totalRaces: Int,
    currentSelection: String?,
    onSelect: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    // Mock race data — in production, loaded from BDF
    val races = listOf(
        Triple("President of the United States", "Vote for ONE", listOf("Jane Mitchell|Democratic Party|a7f2", "Robert Caldwell|Republican Party|cc04", "Maria Santos|Independent|d41f")),
        Triple("US Senator — Florida", "Vote for ONE", listOf("David Park|Republican Party|b82e", "Angela Torres|Democratic Party|9a3c")),
        Triple("Governor of Florida", "Vote for ONE", listOf("Thomas Reed|Republican Party|e6b1", "Patricia Okafor|Democratic Party|f73d", "James Liu|Libertarian Party|c49e")),
        Triple("Proposition 99: Infrastructure Bond", "Vote YES or NO", listOf("Yes||aa11", "No||cc33")),
    )
    val (title, instruction, candidates) = races.getOrElse(raceIndex) { races[0] }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        StepIndicator(step = 2, total = 4, label = "Cast Your Vote")
        Spacer(Modifier.height(8.dp))
        Text("Race ${raceIndex + 1} of $totalRaces", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Text(instruction, color = CivicBlue, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)

        Spacer(Modifier.height(16.dp))

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            candidates.forEach { raw ->
                val parts = raw.split("|")
                val name = parts[0]; val party = parts[1]; val hash = parts[2]
                val selected = currentSelection == hash

                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        .clickable { onSelect(if (selected) "" else hash) },
                    border = BorderStroke(2.dp, if (selected) CivicBlue else Color(0xFFE5E7EB)),
                    colors = CardDefaults.cardColors(containerColor = if (selected) CivicLight else Color.White),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(24.dp).clip(CircleShape)
                                .border(2.dp, if (selected) CivicBlue else Color(0xFFCCC), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (selected) Box(Modifier.size(12.dp).clip(CircleShape).background(CivicBlue))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Navy)
                            if (party.isNotEmpty()) Text(party, fontSize = 13.sp, color = MedGray)
                            Text("Hash: ${hash}...", fontSize = 10.sp, color = CivicBlue, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (raceIndex > 0) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Text("← Back")
                }
            }
            Button(
                onClick = onNext,
                modifier = Modifier.weight(2f),
                shape = RoundedCornerShape(12.dp),
                enabled = currentSelection != null,
            ) {
                Text(if (raceIndex == totalRaces - 1) "Review Ballot →" else "Next →", modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

// ================================================================
// BALLOT REVIEW SCREEN
// ================================================================

@Composable
fun BallotReviewScreen(
    selections: Map<String, String?>,
    onConfirm: () -> Unit,
    onChangeRace: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        StepIndicator(step = 3, total = 4, label = "Review Your Ballot")
        Spacer(Modifier.height(16.dp))
        Text("Review All Selections", style = MaterialTheme.typography.headlineMedium)
        Text("Verify each selection. Tap Change to modify.", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(16.dp))

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            val raceNames = listOf("President", "US Senator", "Governor", "Prop 99")
            raceNames.forEachIndexed { i, name ->
                val sel = selections[i.toString()]
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    border = BorderStroke(1.dp, if (sel == null) Amber else Color(0xFFE5E7EB)),
                    colors = CardDefaults.cardColors(containerColor = if (sel == null) AmberLight else Color.White),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, fontSize = 13.sp, color = MedGray)
                            if (sel != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("✓ ", color = ForestGreen, fontWeight = FontWeight.Bold)
                                    Text("Selected (${sel.take(4)}...)", fontWeight = FontWeight.SemiBold, color = Navy)
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⚠ ", color = Amber, fontWeight = FontWeight.Bold)
                                    Text("No selection (skipped)", fontWeight = FontWeight.SemiBold, color = Amber)
                                }
                            }
                        }
                        TextButton(onClick = { onChangeRace(i) }) { Text("Change", color = CivicBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                    }
                }
            }
        }

        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
        ) {
            Text("🔒  Encrypt & Submit Ballot", modifier = Modifier.padding(vertical = 6.dp), fontWeight = FontWeight.Bold)
        }
    }
}

// ================================================================
// ENCRYPTION ANIMATION SCREEN
// ================================================================

@Composable
fun EncryptionScreen(onComplete: (voteRecordId: String, confirmationHash: String) -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    var hexDisplay by remember { mutableStateOf("") }

    // Hex rain animation
    LaunchedEffect(Unit) {
        while (true) {
            hexDisplay = SVRCrypto.generateNonce()
            delay(80)
        }
    }

    LaunchedEffect(Unit) {
        delay(600); step = 1
        delay(1200); step = 2
        delay(1400); step = 3
        delay(1200); step = 4
        delay(1000); step = 5
        delay(1000)
        onComplete(SVRCrypto.generateVoteRecordId(), SVRCrypto.generateNonce().take(16))
    }

    val steps = listOf(
        "Serializing selections..." to "CandidateHash array",
        "AES-256-GCM encryption..." to "Classical inner layer",
        "ECIES P-384 key wrap..." to "Elliptic curve encapsulation",
        "ML-KEM-1024 encryption..." to "Post-quantum outer layer (FIPS 203)",
        "Computing confirmation hash..." to "SHA-256 commitment",
        "Signing with device key..." to "Secure Enclave signature",
    )

    Column(
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.White, CivicLight))).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).background(CivicBlue), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text("Encrypting Your Ballot", style = MaterialTheme.typography.headlineSmall)
        Text("Hybrid post-quantum encryption", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(24.dp))

        steps.forEachIndexed { i, (label, sub) ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp).alpha(if (step >= i) 1f else 0.2f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(22.dp).clip(CircleShape)
                        .background(if (step > i) ForestGreen else if (step == i) CivicBlue else Color(0xFFDDD)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (step > i) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    else Box(Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(label, fontSize = 13.sp, fontWeight = if (step == i) FontWeight.SemiBold else FontWeight.Normal, color = if (step >= i) Navy else MedGray)
                    Text(sub, fontSize = 10.sp, color = MedGray)
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(hexDisplay, fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = CivicBlue.copy(alpha = 0.4f), maxLines = 2, textAlign = TextAlign.Center)
    }
}

// ================================================================
// SUCCESS SCREEN
// ================================================================

@Composable
fun SuccessScreen(voteRecordId: String, confirmationHash: String, onDone: () -> Unit) {
    val formatted = confirmationHash.uppercase().chunked(4).joinToString("-")

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(Modifier.size(80.dp).clip(CircleShape).background(GreenLight), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(48.dp))
        }

        Spacer(Modifier.height(20.dp))
        Text("Vote Recorded", style = MaterialTheme.typography.headlineLarge.copy(color = ForestGreen))
        Spacer(Modifier.height(8.dp))
        Text("Your vote is encrypted and stored. It can only be decrypted at the air-gapped tabulation center.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)

        Spacer(Modifier.height(24.dp))

        Card(colors = CardDefaults.cardColors(containerColor = LightGray), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("VOTE RECORD ID", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(4.dp))
                Text(voteRecordId, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Navy)

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFE5E7EB))
                Spacer(Modifier.height(16.dp))

                Text("CONFIRMATION CODE", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(4.dp))
                Text(formatted, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = CivicBlue, letterSpacing = 2.sp)
                Spacer(Modifier.height(8.dp))
                Text("Save this code. Verify your ballot at verify.securevote.gov after the election.", style = MaterialTheme.typography.bodySmall, lineHeight = 16.sp)
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOf("ML-KEM-1024" to "Encryption", "REMOTE" to "Channel", "#1 of 5" to "Submission").forEach { (value, label) ->
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = CivicLight), shape = RoundedCornerShape(10.dp)) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(label, fontSize = 10.sp, color = MedGray)
                        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CivicBlue)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Text("Done", modifier = Modifier.padding(vertical = 4.dp), fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Text("You may revote up to 4 more times before the window closes.", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
    }
}

// ================================================================
// THANK YOU SCREEN
// ================================================================

@Composable
fun ThankYouScreen(onReturn: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Navy, Color(0xFF0F1A33)))),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(40.dp)) {
            Text("Thank You for Voting", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Text("Your voice matters. Your vote is cryptographically secured and will be counted.", color = Color.White.copy(alpha = 0.6f), fontSize = 16.sp, textAlign = TextAlign.Center, lineHeight = 24.sp)
            Spacer(Modifier.height(48.dp))
            OutlinedButton(onClick = onReturn, modifier = Modifier.width(280.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))) {
                Text("Return to Start", color = Color.White)
            }
        }
    }
}

// ================================================================
// SHARED COMPONENTS
// ================================================================

@Composable
fun StepIndicator(step: Int, total: Int, label: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text("Step $step of $total", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { step.toFloat() / total },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = CivicBlue,
            trackColor = LightGray,
        )
    }
}
