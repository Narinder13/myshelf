# MyShelf contributor and Codex instructions

This repository is a fork of [Episteme Reader](https://github.com/Aryan-Raj3112/episteme). We are developing **MyShelf**, a beautiful, private, offline-first personal book library for Android, especially the Samsung Galaxy Z Fold family.

## Product priorities
1. Bookshelf experience first: readable cover grid, Continue Reading, Recently Added, collections/shelves, search, and progress.
2. Preserve Episteme's working PDF/EPUB readers, original PDF page rendering, annotations, import/export, local folder sync, metadata, reading state, and accessibility.
3. Make the interface clean and premium but **original**; avoid copying Apple Books assets or its exact visual design.
4. Work offline by default; never require a login or server to read local books. New analytics, ads, trackers, and cloud dependencies are out of scope.
5. Use adaptive Compose layouts based on available width and window insets. Folded, unfolded, split-screen, and orientation changes must not lose selection or reading position.

## Ground-truth code map
- Android: `app/src/main/java/com/aryan/reader/`
- Current Android library UI: `UnifiedLibraryScreen.kt`, `UnifiedLibrarySections.kt`, `UnifiedLibraryShelfCrud.kt`
- Android navigation: `AppNavigation.kt`; activity: `MainActivity.kt`
- Covers: `ContentThumbnailGenerator.kt`, `ThemedBookCover.kt`
- Shared mobile UI and layout: `shared/src/commonMain/kotlin/com/aryan/reader/shared/ui/`
- Adaptive grid logic: `SharedMobileGridWidth.kt`
- Shared library contracts/state: `shared/src/commonMain/kotlin/com/aryan/reader/shared/`
- PDF UI: `app/src/main/java/com/aryan/reader/pdf/`
- Gradle variants: `app/build.gradle.kts`
- `HomeScreen.kt` is **not** the active home screen despite its name; its header explicitly says the former HomeScreen was retired. Do not rebuild UI there by assumption.

Trace call sites and existing persistence before editing. Shared Kotlin Multiplatform changes can affect Android, iOS, and desktop; do not casually break the other hosts.

## Scope and process
- Read `docs/MYSHELF_PRODUCT_BRIEF.md` and `docs/CODEX_PHASE_1.md` before starting Phase 1.
- Make a small incremental implementation against an isolated feature branch and open a PR to `main`; do not rewrite the reader engine, database, or module structure just to change appearance.
- Reuse existing shelf/book data and reader navigation. Do not add duplicate sources of truth or migrate user data unless truly necessary; if necessary, provide migration and regression tests.
- If the current PDF renderer does not support a requested UX detail, document the limitation and defer it rather than claiming it works.
- Generate thumbnails off the main thread and cache them. Avoid rendering many PDF pages during scrolling or recomposition.
- Use Material 3 and existing app theme infrastructure; keep light/dark schemes, accessible contrast, scalable text, focus states, content descriptions, and touch targets.
- Handle empty library, missing permissions, revoked folder URIs, corrupt PDFs, duplicate imports, long filenames, and device rotations gracefully.
- Keep the AGPL-3.0 license, upstream copyright notices, and required attribution intact. Review any replacement branding/assets for license compatibility.

## Verification
1. Inspect baseline and record whether a build is possible before changing code.
2. Run `./gradlew :app:assembleOssDebug` where an Android SDK and JDK are configured; check available tasks before assuming any other variant name.
3. Run relevant tests / compilation checks that the environment supports. If an environment, SDK, or dependency blocks a build, report the exact failure; **never mark an unrun test as passed**.
4. Report changed files, functional behavior, screenshots when available, and any unresolved regressions.
5. Keep every phase reviewable, with explicit acceptance criteria.

## Current scope
Phase 1 is Android bookshelf and visual foundation. Do not add biometric lock, cloud sync, AI features, app monetization, or major reader changes as part of Phase 1.
