# job-coach

> AI-powered job coach for adults with intellectual and developmental disabilities (IDD), running on Meta smart glasses at the edge.

## What It Does

- **Task coaching** — step-by-step job prompts learned with a DSP, replayed on shift
- **Situation steering** — real-time alerts for unsafe or unfamiliar situations  
- **Social navigation** — guided prompts for restaurants, checkout, small talk

## Stack (proposed)

- Meta smart glasses (Ray-Ban Meta) — capture + audio I/O
- Red Hat OpenShift AI — model serving at the edge
- Red Hat Device Edge / MicroShift — on-device inference
- SAP Business Technology Platform — workflow + caregiver dashboard
- Whisper (STT) + LLaMA 3 (reasoning) + TTS — local inference stack

## Status

🟡 Concept / early prototype

## Related

- [OHC Demo System](https://github.com/jodonnel/ohc-sap-demo)
- [Live Demo](https://north-qr-demo-qa.apps.cluster-nlthm.nlthm.sandbox3528.opentlc.com/present-job-coach)
