# job-coach

> AI-powered job coach for adults with intellectual and developmental disabilities (IDD), running on Meta Ray-Ban smart glasses.

## What It Does

- **Voice loop** — talk to Chloe/Zoe through the glasses, get real-time guidance
- **Task coaching** — step-by-step job prompts learned with a DSP, replayed on shift
- **Situation steering** — real-time alerts for unsafe or unfamiliar situations
- **Social navigation** — guided prompts for restaurants, checkout, small talk

## Architecture

```
Meta Ray-Ban Glasses (mic + camera)
  ↓ DAT SDK (Bluetooth)
Android App (this repo)
  ↓ STT (Google / Whisper)
Intent Processing (Claude API)
  ↓ CloudEvents
Red Hat OpenShift (ROSA)
  ↓
Dashboard (real-time monitoring)
```

## Modules

| Module | Purpose |
|--------|---------|
| `app` | Compose UI, Hilt DI, main activity |
| `core` | CloudEvent model, OpenShift client, shared types |
| `wearable` | Meta DAT SDK integration, audio capture |
| `stt` | Speech-to-text (Google STT, Whisper on-device) |

## Build

```bash
# Set your GitHub token for Meta DAT SDK access
export GITHUB_TOKEN=ghp_your_token_here

# Build
./gradlew build

# Install debug APK
./gradlew installDebug
```

## Profiles

Profiles are local to the device. No central registry, no login, no account. The app knows which preferences and coaching mode to use for the person wearing the glasses.

## Status

🟡 V1 — Stage demo (SAP Insider 2026)

## Related

- [OHC Demo System](https://github.com/jodonnel/ohc-sap-demo)
