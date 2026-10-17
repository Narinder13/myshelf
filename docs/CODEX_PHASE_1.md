# Codex task 01: MyShelf premium Android bookshelf

**Task type:** Implement code, not just propose a design.
**Work target:** `Narinder13/myshelf`
**Instructions:** Read root `AGENTS.md` and `docs/MYSHELF_PRODUCT_BRIEF.md` before touching implementation.

## Mission

Transform the Android library entry experience into **MyShelf**, a beautiful, approachable personal PDF and EPUB library. Emphasize useful functionality and reliability over decorative UI. The original Episteme reading engine should remain intact.

## Start with an audit

1. Inspect the real routes, state, data persistence, and code ownership for Home, Library, Shelves/Collections, Search, Settings, and Book Opening.
2. Note that `HomeScreen.kt` contains legacy shared UI; the current home experience belongs to the unified library flow.
3. Trace `UnifiedLibraryScreen.kt`, `UnifiedLibrarySections.kt`, `UnifiedLibraryShelfCrud.kt`, `AppNavigation.kt`, `ThemedBookCover.kt`, `ContentThumbnailGenerator.kt`, and the shared mobile UI/width-class helpers.
4. Run the existing Android OSS debug build where possible, before modifying the code. Record errors rather than hiding them.

## Implement one reviewable first slice

- Give the unified Home surface an attractive, original reading-library layout with a clearly prioritized **Continue Reading** item, recently added books, and collections.
- Improve the actual book cards: correctly cached cover thumbnails, sensible aspect ratio, consistent titles, unobtrusive real reading progress, and graceful missing-cover placeholders.
- Improve spacing, section labels, visual hierarchy, surfaces, theme tokens, and empty/loading/error states. Aim for modern editorial calm rather than a crowded dashboard.
- Improve browsing/discoverability through the **existing** import, search, sorting, grid/list, shelf and filter mechanisms. Do not add mock controls or duplicate existing sections.
- Respect measured available width using shared width helpers; support compact folded phone, medium unfolded Fold, split-screen and tablet without enormous cards or hardcoded device dimensions.
- Preserve system back, Android file picker permission handling, offline use, reader launch/resume, existing progress and annotations.
- Keep settings and every important existing feature reachable. If something isn't fully implementable in one reviewable slice, retain the existing functioning behavior and document the next PR.
- For light and dark themes, use consistent reusable theme tokens, restrained surfaces, accessible typography and contrast. Don't add unrelated packages or online asset dependencies.
- If adding tests, place them alongside existing test conventions. Screenshots or previews must reflect implemented UI, not speculative designs.

## Explicit non-goals for this PR

- Replacing the PDF renderer or EPUB reader
- Full database migrations or destructive schema changes
- Renaming Java/Kotlin packages or changing signing/release configuration
- Cloud sync, accounts, AI tools, analytics, ads, or paid features
- Biometric locks, extensive backup/import migration, or new permission requests
- Rebuilding iOS/desktop; shared-code changes must preserve compatibility
- Copying Apple Books iconography or interface pixel-for-pixel

## Quality bar

- A real local PDF appears in the library with an accurate title and usable cover fallback.
- Existing covers and reading progress reflect current data, never guessed progress.
- Continue Reading correctly resumes a book.
- Library collections, search, sorting, PDF and EPUB opening, settings, and back navigation remain functional.
- Folding/unfolding, rotating, and changing window widths preserve scroll/selection state and don't cause crashes.
- Long titles, missing authors, a fully empty library, and corrupt PDFs are graceful.
- No excessive main-thread PDF decoding; no recomposition storm from thumbnail work.
- Build with `./gradlew :app:assembleOssDebug` if toolchain is available and run appropriate tests; include any failures or environment blockers in report.

## Delivery

Work on a feature branch (prefer `codex/myshelf-phase1` once created), and open a pull request to `main`. In the PR include:
- what changed, with code paths
- compact vs unfolded screenshots if emulator/device screenshots are available
- exact build/test commands and results
- manual verification checklist
- remaining limitations and a narrowly scoped Phase 2 suggestion

Do not merge to `main` as part of this task. Do not report the app as tested unless the tests ran.
