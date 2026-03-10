# Job Coach

AI-powered job coaching for adults with intellectual and developmental disabilities (IDD), delivered through Meta Ray-Ban smart glasses.

## Problem

7.4 million working-age adults in the US have an intellectual or developmental disability. Most can work — and want to — but the support systems are fragile. Job coaches are expensive, inconsistent, and unavailable at the moments that matter most: the first confusing interaction with a customer, the unexpected change in routine, the task that was explained once three weeks ago.

Job Coach puts a real-time AI assistant in the worker's ear. No screen to look at. No app to fumble with. Just glasses that look normal and a voice that knows your job.

## What It Does

- **Voice loop** — talk through the glasses, get real-time guidance via speech
- **Task coaching** — step-by-step job prompts learned with a DSP, replayed on shift
- **Situation steering** — real-time alerts for unsafe or unfamiliar situations
- **Social navigation** — guided prompts for restaurants, checkout, small talk
- **Zero-screen UX** — the person wearing the glasses never touches a phone

## Architecture

```
Meta Ray-Ban Smart Glasses
  │  5-mic array + camera
  │  Bluetooth (DAT SDK)
  ▼
Android Companion App ◄── this repo
  │  SpeechRecognizer (on-device STT)
  │  Intent classification
  │  CloudEvents (application/cloudevents+json)
  ▼
Event Platform (OpenShift)
  │  Ingest → Process → Route
  │  SSE stream to dashboards
  ▼
Monitoring Dashboard
  │  Real-time transcription feed
  │  Coaching session state
  └  Alerts for support staff
```

## Project Structure

```
job-coach/
├── app/              # Android app — Compose UI, Hilt DI, main activity
├── core/             # Shared types — CloudEvent model, network client
├── wearable/         # Meta DAT SDK integration, glasses lifecycle
├── stt/              # Speech-to-text engine (Google STT, extensible)
├── gradle/
│   └── libs.versions.toml   # Version catalog (single source of truth)
└── docs/             # Architecture decisions, API contracts
```

### Module Responsibilities

| Module | Depends On | Purpose |
|--------|-----------|---------|
| `app` | core, wearable, stt | UI, navigation, dependency injection, lifecycle |
| `core` | — | CloudEvent data model, HTTP client, shared contracts |
| `wearable` | core | Meta DAT SDK wrapper, connection state, audio routing |
| `stt` | core | Speech-to-text abstraction + Google STT implementation |

Dependency flows one direction: `app → {core, wearable, stt}`, `wearable → core`, `stt → core`. No circular dependencies. No God module.

## Tech Stack

| Layer | Choice | Why |
|-------|--------|-----|
| Language | Kotlin 2.1 | Android-first, coroutines, type safety |
| UI | Jetpack Compose + Material 3 | Declarative, modern, accessible |
| DI | Hilt (Dagger) | Compile-time injection, Android-native |
| Network | OkHttp + Moshi | Lightweight, no reflection (KSP codegen) |
| Glasses | Meta DAT SDK 0.4.0 | Official SDK for Ray-Ban smart glasses |
| STT | Android SpeechRecognizer | On-device, zero-latency, no API key |
| Events | CloudEvents spec | Vendor-neutral event envelope |
| Processing | KSP | Annotation processing without kapt overhead |
| Build | Gradle version catalog | Centralized dependency management |

## Prerequisites

- JDK 17+
- Android SDK (API 35 / compileSdk 35, minSdk 29)
- GitHub personal access token with `read:packages` scope (for Meta DAT SDK)

## Build

```bash
# Clone
git clone https://github.com/jodonnel/job-coach.git
cd job-coach

# Configure (one-time)
cat >> local.properties <<EOF
sdk.dir=/path/to/Android/Sdk
dat.github.token=ghp_your_github_pat_here
EOF

# Build
./gradlew assembleDebug

# Install to connected device or emulator
./gradlew installDebug

# Run tests
./gradlew test
```

### GitHub Token for Meta DAT SDK

The Meta Wearables DAT SDK is distributed via GitHub Packages. You need a classic GitHub PAT with `read:packages` permission:

1. Go to GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token with `read:packages` scope
3. Add to `local.properties` as `dat.github.token=ghp_...`

Alternatively, set the `GITHUB_TOKEN` environment variable.

## Design Principles

**Local-first.** Profiles live on the device. No account creation. No cloud login. No central registry. The app knows who's wearing the glasses by local device configuration — not by authenticating against a server.

**Zero-screen.** The person wearing the glasses should never need to look at or touch the phone. All interaction happens through voice. The phone is a relay, not an interface.

**Event-driven.** Every meaningful interaction produces a CloudEvent. Events flow to whatever backend is listening — could be OpenShift, could be a Raspberry Pi, could be nothing. The app doesn't care.

**Modular.** Each capability (glasses connection, speech recognition, event routing) lives in its own Gradle module with a clean interface. Swap Google STT for Whisper? Change the `SttEngine` binding. No other module notices.

**Privacy by architecture.** Audio is processed on-device. Transcriptions are ephemeral unless explicitly sent as events. No recordings are stored. No data leaves the device without an explicit action.

## Profiles

Profiles are local JSON documents on the device. A profile contains:

- Display name
- Coaching mode (task list, free conversation, alert-only)
- Job-specific prompts and sequences
- Communication preferences (verbosity, language, pace)

There is no user ID. There is no login. The device is the identity boundary.

## Event Format

All events follow the [CloudEvents specification](https://cloudevents.io/):

```json
{
  "specversion": "1.0",
  "type": "ohc.demo.job-coach.voice",
  "source": "meta-glasses://local",
  "id": "uuid",
  "time": "2026-03-10T08:30:00Z",
  "eventclass": "job-coach",
  "data": {
    "transcription": "where do the cups go",
    "profile": "Default"
  }
}
```

## Roadmap

- [x] Multi-module Gradle scaffold
- [x] Meta DAT SDK integration (skeleton)
- [x] CloudEvents to OpenShift
- [x] Android SpeechRecognizer wiring
- [ ] Runtime permission handling ([#7](https://github.com/jodonnel/job-coach/issues/7))
- [ ] Bluetooth audio routing (glasses mic → STT)
- [ ] On-device intent classification
- [ ] Profile management UI
- [ ] Whisper on-device STT option
- [ ] Coaching prompt sequences
- [ ] Foreground service for background operation

## Contributing

Issues and pull requests welcome. If you work in disability services, vocational rehabilitation, or assistive technology — your input is especially valuable. Open an issue and tell us what you need.

## License

Apache 2.0

## Part of the OHC Platform

Job Coach is a use case running on the [Operational Hub for the Connected Enterprise (OHC)](https://github.com/jodonnel/ohc-sap-demo) — an event-driven integration platform built on OpenShift.

OHC connects edge devices to enterprise systems using CloudEvents and server-sent events. The same platform that routes rail safety alerts, substation telemetry, and shopfloor quality events also carries voice interactions from a pair of smart glasses. That's the point: one event mesh, many use cases.

```
Job Coach App ──► CloudEvent ──► OHC /ingest ──► SSE ──► Dashboard
                                    ▲
Rail sensors ───► CloudEvent ───────┘
Substation SCADA ► CloudEvent ──────┘
Shopfloor MES ──► CloudEvent ───────┘
```

The stage demo at SAP Insider 2026: Jim wears the glasses, talks through the full stack, and the audience watches the event arrive on screen in real time — alongside every other industry vertical running on the same platform.
