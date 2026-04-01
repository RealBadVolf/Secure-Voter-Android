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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.securevote.remote.ui.theme.*

/**
 * Post-Election Verification Screen.
 *
 * After the election is certified, the voter can:
 *   1. Enter their VoteRecordID and Election PIN
 *   2. Retrieve the confirmation hash from the Merkle tree
 *   3. Compare it against the code they saved at submission time
 *
 * Match = their exact encrypted ballot is in the official record.
 * Mismatch = the ballot was altered after submission (raise a challenge).
 *
 * This is the remote equivalent of the VVPAT paper review for in-person voters.
 */
@Composable
fun VerificationScreen(
    onVerify: (voteRecordId: String, pin: String) -> Unit,
    onBack: () -> Unit,
) {
    var voteRecordId by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var phase by remember { mutableIntStateOf(0) }  // 0=input, 1=verifying, 2=result
    var verificationResult by remember { mutableStateOf<VerificationDisplayResult?>(null) }

    // Saved confirmation hash (from when the voter submitted)
    var savedHash by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (phase) {
            0 -> {
                Icon(
                    Icons.Filled.Verified,
                    contentDescription = null,
                    tint = CivicBlue,
                    modifier = Modifier.size(48.dp),
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "Verify Your Vote",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    "Confirm your encrypted ballot is in the certified Merkle tree",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(24.dp))

                // VoteRecordID input
                OutlinedTextField(
                    value = voteRecordId,
                    onValueChange = { voteRecordId = it.uppercase() },
                    label = { Text("Vote Record ID") },
                    placeholder = { Text("SV-2026-XXXX-XXXX") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )

                Spacer(Modifier.height(12.dp))

                // Election PIN input
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 6) pin = it },
                    label = { Text("Election PIN") },
                    placeholder = { Text("6-digit PIN") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword,
                    ),
                )

                Spacer(Modifier.height(12.dp))

                // Saved confirmation hash input
                OutlinedTextField(
                    value = savedHash,
                    onValueChange = { savedHash = it.uppercase().filter { c -> c.isLetterOrDigit() || c == '-' } },
                    label = { Text("Your Saved Confirmation Code (optional)") },
                    placeholder = { Text("A7F2-38B1-CC04-9E67") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "The confirmation code you wrote down or saved when you submitted your ballot. " +
                    "If provided, we'll compare it against the code in the Merkle tree.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 16.sp,
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        phase = 1
                        onVerify(voteRecordId, pin)
                        // Simulate verification delay
                        // In production: API call to sv-verify
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = voteRecordId.isNotBlank() && pin.length == 6,
                ) {
                    Text(
                        "Verify My Vote",
                        modifier = Modifier.padding(vertical = 4.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.height(16.dp))

                TextButton(onClick = onBack) {
                    Text("← Back", color = MedGray)
                }
            }

            1 -> {
                Spacer(Modifier.height(80.dp))
                CircularProgressIndicator(color = CivicBlue, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(16.dp))
                Text("Verifying...", style = MaterialTheme.typography.headlineSmall)
                Text("Checking Merkle tree inclusion", style = MaterialTheme.typography.bodyMedium)

                // Simulate API response
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    // Simulate successful verification
                    val treeHash = "A7F238B1CC049E67"  // From Merkle tree
                    val savedClean = savedHash.replace("-", "")
                    val hashMatch = savedClean.isEmpty() || savedClean.equals(treeHash, ignoreCase = true)

                    verificationResult = VerificationDisplayResult(
                        found = true,
                        treeConfirmationHash = treeHash,
                        hashMatch = hashMatch,
                        merkleRoot = "7c3a1b9f2e8d4c6a...",
                        proofDepth = 17,
                    )
                    phase = 2
                }
            }

            2 -> {
                val result = verificationResult ?: return

                if (result.found) {
                    // Vote found in tree
                    Box(
                        Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(GreenLight),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            null,
                            tint = ForestGreen,
                            modifier = Modifier.size(48.dp),
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        "Your Vote Was Counted",
                        style = MaterialTheme.typography.headlineMedium.copy(color = ForestGreen),
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Your encrypted ballot exists in the certified Merkle tree.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(20.dp))

                    // Merkle proof details
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightGray),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("MERKLE PROOF", style = MaterialTheme.typography.labelSmall)
                            Spacer(Modifier.height(8.dp))

                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("Proof depth:", fontSize = 13.sp, color = MedGray)
                                Text(
                                    "${result.proofDepth} nodes",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Navy,
                                )
                            }

                            Spacer(Modifier.height(4.dp))

                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("Merkle root:", fontSize = 13.sp, color = MedGray)
                                Text(
                                    result.merkleRoot,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Navy,
                                    fontFamily = FontFamily.Monospace,
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Confirmation hash comparison
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (result.hashMatch) GreenLight else CrimsonLight
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("CONFIRMATION CODE", style = MaterialTheme.typography.labelSmall)
                            Spacer(Modifier.height(8.dp))

                            Text("In Merkle tree:", fontSize = 13.sp, color = MedGray)
                            Text(
                                formatHash(result.treeConfirmationHash),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = Navy,
                            )

                            if (savedHash.isNotBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text("Your saved code:", fontSize = 13.sp, color = MedGray)
                                Text(
                                    savedHash.ifBlank { "Not provided" },
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = Navy,
                                )

                                Spacer(Modifier.height(12.dp))

                                if (result.hashMatch) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Check, null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            "MATCH — your exact encrypted ballot is in the record",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ForestGreen,
                                        )
                                    }
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Warning, null, tint = Crimson, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            "MISMATCH — the stored ballot differs from your submission. " +
                                            "Contact the election authority to file a formal challenge.",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Crimson,
                                            lineHeight = 18.sp,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CivicLight),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            "This proves your vote was included in the official count. " +
                            "It does NOT reveal your selections — ballot secrecy is preserved.",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall.copy(color = Navy),
                            lineHeight = 16.sp,
                        )
                    }
                } else {
                    // Vote not found
                    Box(
                        Modifier.size(80.dp).clip(CircleShape).background(CrimsonLight),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Error, null, tint = Crimson, modifier = Modifier.size(48.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Vote Not Found",
                        style = MaterialTheme.typography.headlineMedium.copy(color = Crimson),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "No vote matching this Record ID was found. Please check your ID and try again, " +
                        "or contact the election authority.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(Modifier.height(24.dp))

                OutlinedButton(
                    onClick = { phase = 0; verificationResult = null },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Verify Another Vote")
                }
            }
        }
    }
}

private fun formatHash(hash: String): String {
    return hash.uppercase().chunked(4).joinToString("-")
}

private data class VerificationDisplayResult(
    val found: Boolean,
    val treeConfirmationHash: String,
    val hashMatch: Boolean,
    val merkleRoot: String,
    val proofDepth: Int,
)
