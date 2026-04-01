# SecureVote Remote — Android App

## Overview

SecureVote Remote (SVR) is the smartphone-based absentee voting client for the SecureVote election system. It enables voters to cast cryptographically secured, post-quantum encrypted ballots from their Android device.

**This is a companion app to the SecureVote specification.** It interfaces with the `sv-remote-gateway` API (Zone 6) as defined in `ABSENTEE.md`.

## Architecture

```
┌─────────────────────────────────────────────┐
│           SecureVote Remote App              │
│                                             │
│  ┌─────────────┐  ┌─────────────────────┐  │
│  │ Biometric    │  │ Crypto Module       │  │
│  │ Engine       │  │ (Secure Enclave)    │  │
│  │ ML Kit Face  │  │ ML-KEM-1024        │  │
│  │ Liveness     │  │ ECIES P-384        │  │
│  │ Dual-model   │  │ AES-256-GCM        │  │
│  └──────┬───────┘  │ RSA Blind Sig      │  │
│         │          │ Argon2id           │  │
│  ┌──────┴───────┐  │ HMAC-SHA256        │  │
│  │ IPVC QR      │  └──────┬──────────────┘  │
│  │ Generator    │         │                  │
│  │ 10s rotation │  ┌──────┴──────────────┐  │
│  │ HMAC-bound   │  │ Repository          │  │
│  └──────────────┘  │ (session state)     │  │
│                    └──────┬──────────────┘  │
│                           │ TLS 1.3 + Pinning│
└───────────────────────────┼──────────────────┘
                            │
                   sv-remote-gateway (Zone 6)
```

## Security Features

### Screenshot Prevention (FLAG_SECURE)
`FLAG_SECURE` is set on the Activity window **unconditionally** and **cannot be disabled**. This prevents:
- Screenshots (returns black)
- Screen recording (captures black frames)
- Screen casting (Chromecast shows black)
- Recent apps thumbnail (black in switcher)

### IPVC QR Code (10-Second Rotation)
When biometric verification fails, the app displays a rotating QR code for in-person verification at an IPVC (bank, post office, embassy, etc.). The QR:
- Regenerates every **10 seconds** with a fresh nonce
- Contains an HMAC proving device possession
- Cannot be relayed (expired by the time a screenshot reaches a remote admin)
- The admin must be **physically present** to scan it before expiry

### Hybrid Post-Quantum Encryption
Ballot selections are encrypted with:
1. **Inner layer:** AES-256-GCM, key wrapped with ECIES P-384 (classical)
2. **Outer layer:** AES-256-GCM, key encapsulated with ML-KEM-1024 (FIPS 203, post-quantum)

Both layers must be broken to reveal selections. The decryption keys exist only in the HSM at the air-gapped tabulation center.

### Blind Token Protocol
The RSA blind signature scheme decouples voter identity from ballot content. The authentication server signs the voter's token without seeing it. The ballot server receives the token without knowing who it belongs to.

## API Integration

The app communicates with `sv-remote-gateway` via REST over TLS 1.3 with certificate pinning.

**Same databases as in-person:** The `vote_channel` column (`IN_PERSON` vs `REMOTE`) on `vote_casts` distinguishes the channels. Remote votes are interleaved in the Merkle tree for privacy.

Key endpoints:
- `POST /register/device` — Device registration
- `POST /auth/session` — Voting session authentication
- `POST /auth/token` — Blind token issuance
- `GET /ballot/bdf` — Fetch Ballot Definition File
- `POST /ballot/submit` — Submit encrypted vote

## Build Instructions

### Prerequisites
- Android Studio Ladybug (2024.2+) or newer
- JDK 17+
- Android SDK 35
- Kotlin 2.1.0+

### Steps

1. **Clone the project:**
   ```bash
   git clone <repo-url>
   cd SecureVoteRemote
   ```

2. **Open in Android Studio:**
   File → Open → select the `SecureVoteRemote` directory

3. **Configure keys:**
   - Replace certificate pins in `network_security_config.xml` and `NetworkModule.kt`
   - Set your Play Integrity API key in `strings.xml`
   - Set the gateway URL in `app/build.gradle.kts` (`SVR_GATEWAY_URL`)

4. **Build:**
   ```bash
   ./gradlew assembleDebug
   ```

5. **Install:**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### For release builds:
```bash
./gradlew assembleRelease
```
You must configure a signing key in `app/build.gradle.kts` (replace the debug signing config with the election authority's signing certificate).

## Project Structure

```
SecureVoteRemote/
├── app/src/main/java/com/securevote/remote/
│   ├── SVRApplication.kt          # Hilt application entry
│   ├── MainActivity.kt            # FLAG_SECURE enforced here
│   ├── biometric/
│   │   └── BiometricEngine.kt     # Face detection, liveness, dual-model
│   ├── crypto/
│   │   ├── SVRCrypto.kt           # All crypto ops (PQ encrypt, blind sig, hash)
│   │   └── IPVCQRGenerator.kt     # 10-second rotating QR with HMAC
│   ├── data/
│   │   ├── api/
│   │   │   ├── SVRGatewayApi.kt   # Retrofit API interface
│   │   │   └── NetworkModule.kt   # Hilt DI, cert pinning, TLS config
│   │   ├── models/
│   │   │   └── Models.kt          # All domain models (matches ABSENTEE.md)
│   │   └── repository/
│   │       └── SVRRepository.kt   # Business logic coordinator
│   ├── navigation/
│   │   └── SVRRoute.kt            # All navigation routes
│   └── ui/
│       ├── SVRApp.kt              # Main nav graph
│       ├── theme/
│       │   └── Theme.kt           # SecureVote color palette + typography
│       └── screens/
│           ├── Screens.kt         # All voting flow screens
│           └── IPVCQRCodeScreen.kt # IPVC QR with rotation + countdown
└── app/src/main/res/
    ├── values/                    # strings, themes
    └── xml/                       # NFC filter, network security, backup rules
```

## Testing

```bash
# Unit tests
./gradlew test

# Instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest
```

## Minimum Device Requirements

| Requirement | Minimum |
|---|---|
| Android version | 9.0+ (API 28) |
| Hardware keystore | Required (StrongBox preferred) |
| NFC | Required for chip-based ID verification |
| Camera | Required for biometric capture |
| Play Services | Required for Play Integrity API |

## License

Open source — same license as the SecureVote specification.
