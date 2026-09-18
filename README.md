# ResearchGraph

ResearchGraph is an Android scientific research analysis and knowledge synthesis application built with Jetpack Compose and Material Design 3.

## Features

- **Topic Discovery & Knowledge Graph**: Interactively explore papers, concepts, authors, and methodologies.
- **Academic Synthesis**: Synthesizes foundational literature, active paradigms, and open research questions using the Gemini API.
- **Reading & Annotation Environment**: Annotate research papers, save passages, highlight methodologies, and generate citations (BibTeX, APA, IEEE, Harvard).
- **Source & Project Workspaces**: Organize research projects, literature collections, and custom notes.

## Getting Started

### Prerequisites

- Android Studio Ladybug (or newer)
- Android SDK 36 (Minimum SDK 24)
- JDK 11 or newer

### Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/researchgraph.git
   cd researchgraph
   ```

2. **Configure Environment Variables**:
   Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```
   Open `.env` and configure your API key:
   ```properties
   GEMINI_API_KEY=your_gemini_api_key_here
   ```
   *(Note: If you run the app without an API key, ResearchGraph operates smoothly using its built-in offline research database and fallback heuristics.)*

3. **Build & Run**:
   Open the project in Android Studio or compile using Gradle:
   ```bash
   ./gradlew assembleDebug
   ```

## Security & Secrets

- Real API credentials must never be committed to source control.
- `.env` and Android keystores are configured in `.gitignore`.
- Only sanitized placeholders are included in `.env.example`.
