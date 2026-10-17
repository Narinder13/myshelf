# MyShelf Phase 1 implementation and verification

Date: 2026-10-17. Branch: `codex/myshelf-phase1`. Android-only bookshelf slice; device acceptance remains pending.

## Latest verification after toolchain installation

The requested toolchains are now installed and verified. Microsoft OpenJDK 21.0.12.1 is at `C:\Users\narin\AppData\Local\Programs\MyShelfToolchain\jdk-21.0.12.1+1`; Android NDK r28c (28.2.13676358) is at `C:\Users\narin\AppData\Local\Android\Sdk\ndk\28.2.13676358`. Both archives matched their official vendor checksums. Java/javac 21 and the NDK compiler were executed successfully. Machine and user environment settings were not changed.

For these successful runs, PowerShell set process-local `JAVA_HOME` to the JDK path above, prepended its `bin` to `PATH`, set `ANDROID_HOME`/`ANDROID_SDK_ROOT` to the existing SDK, and set `GRADLE_USER_HOME=$env:USERPROFILE\.gradle`.

| Command/check | Actual result |
| --- | --- |
| `.\gradlew.bat :app:assembleOssDebug --no-configuration-cache --console=plain --max-workers=2` | **Passed**, 9m 14s, 76 actionable tasks. Kotlin, Java, resources and native libraries compiled; universal OSS debug APK packaged. Missing dependencies were fetched online. No source/build requirement changes or toolchain overrides were needed. |
| `.\gradlew.bat :app:testOssDebugUnitTest --tests com.aryan.reader.UnifiedLibraryScreenTest --tests com.aryan.reader.MyShelfHomeTest --tests com.aryan.reader.MyShelfBookPresentationTest --no-configuration-cache --console=plain --max-workers=2` | **Passed**, 1m 1s. **19 tests**, zero failures/errors/skips: 14 unified-library, 2 Home import-order, 3 presentation tests. |
| SDK build-tools 36.0.0 `apksigner verify --verbose --print-certs` | **Passed**, v2 signature with the Android debug certificate. |
| SHA256 source/output copy comparison | **Matched**: `3773E2572E9EFE76D2C5515DD47D2C2B7195B8186B76BB783A20F464FF5F90BC`. |

Installer in this workspace: `output/MyShelf-debug.apk` (97,219,500 bytes). Original Gradle output: `app/build/outputs/apk/oss/debug/app-oss-debug.apk`; existing finalizer copy: `app/build/outputs/renamed-apk/ossDebug/Episteme-oss-v1.0.58-oss-debug.apk`. APKs remain ignored build artifacts. The APK was built from application source commit `64c09c4`.

Package `com.aryan.reader.oss`, version `1.0.58-oss` (64), Android 8+ (API 26), target API 36; includes arm64-v8a, armeabi-v7a, x86 and x86_64. The launcher label is still **Episteme**; the new bookshelf header is MyShelf. No phone installation or device runtime verification is claimed. Fold/rotation/accessibility/reader regression checks and screenshots below remain pending.

The initial failed checks below are retained as historical evidence; the NDK/JDK/dependency blockers have been resolved.

## Implemented behavior

- Home now has MyShelf branding, compact search/import actions, a scrolling Continue Reading card, a recently added cover carousel, and existing manual collections. Recently added uses import time rather than last-open time. Empty states invoke the real file picker or shelf-creation dialog.
- Continue Reading requires persisted reading position or finite positive progress, excludes deleted/completed books and audiobooks, and opens through the existing reader callback. Unknown progress stays absent. The existing reader still owns page/CFI/locator restoration.
- Library has adaptive cover-first grid and wrapping list rows, search, sorting, reading-status filters, advanced filters, known progress and pinned/status badges. Long press retains the existing contextual actions, metadata, annotations export, sharing, saving, shelf organization and delete confirmation.
- Collections show actual names, counts and cover previews. Selected collections retain breadcrumbs, child folders, add/rename/delete and remove-book actions. The visible shelf now uses the existing persisted `viewingShelfId`, which is also used by remove-from-shelf actions; there is no new shelf store.
- Primary Home/Library/Collections navigation becomes a rail at 600dp of available width and a bottom bar below that. The existing drawer keeps secondary destinations and settings. Grid cells adapt to measured content width and covers are capped at 180dp. Horizontal safe-drawing insets protect landscape cutouts.
- Saveable section scopes retain Home, Library and per-shelf scroll state; grid and list have independent saved positions. Search/filter state survives configuration changes. No size-class key recreates the reader or library state.
- The scoped Material 3 library palette provides warm paper and charcoal surfaces while preserving the parent theme's accent and typography. Missing/corrupt cover-image loads retain generated fallback art. Coil handles file access/decoding; existing background PDF first-page generation and cover caching are unchanged.

## Code paths

| Files | Purpose |
| --- | --- |
| `UnifiedLibraryScreen.kt` | Existing entry point, primary navigation, state retention, shelf persistence, real action wiring, Continue selector |
| `MyShelfHome.kt` | Home feed, root collections, recent-import projection and implemented Compose previews |
| `MyShelfLibrary.kt` | Android library browsing controls, adaptive grid/list, top bar and primary navigation |
| `MyShelfBookCard.kt`, `MyShelfBookPresentation.kt` | Shared Android book-card/list presentation and finite-progress/author normalization |
| `HomeScreen.kt`, `LibraryScreen.kt` | Existing live card wrappers only; the retired Home screen was not rebuilt |
| `UnifiedLibrarySections.kt` | Continue card and selected-collection content; unused Android adapters removed |
| `ThemedBookCover.kt` | Asynchronous loading/error fallback; stable Coil cache requests |
| `ui/theme/MyShelfLibraryTheme.kt` | Scoped surface colors and reusable layout tokens |
| `res/values/myshelf_{home,library,cards}.xml` | Localizable default UI text |
| `MyShelfHomeTest.kt`, `MyShelfBookPresentationTest.kt`, `UnifiedLibraryScreenTest.kt` | Import order, progress/author normalization and Continue selector regressions |

No reader engine, shared Kotlin source, database schema, import/sync service, navigation route, dependency or license file changed. PDF original-page rendering, EPUB, annotations, bookmarks, folder permission handling, metadata, export and settings continue to use the original implementation. Runtime regression verification is still required.

## Initial verification before toolchain installation

Process-local environment for Gradle: `GRADLE_USER_HOME=$env:USERPROFILE\.gradle`, `ANDROID_HOME=$env:LOCALAPPDATA\Android\Sdk`. No machine settings or project build requirements were changed.

| Command/check | Actual result |
| --- | --- |
| Baseline `.\gradlew.bat :app:assembleOssDebug --offline --console=plain` | Failed during Android configuration: `NDK not configured. Download it with SDK manager. Preferred NDK version is '28.2.13676358'.` |
| Initial post-change `.\gradlew.bat :app:assembleOssDebug --offline --no-configuration-cache --console=plain` | Same missing-NDK failure; no Android compilation/APK |
| `.\gradlew.bat :app:testOssDebugUnitTest --tests com.aryan.reader.UnifiedLibraryScreenTest --tests com.aryan.reader.MyShelfHomeTest --tests com.aryan.reader.MyShelfBookPresentationTest --offline --no-configuration-cache --console=plain` | Same missing-NDK failure; **Android Gradle tests did not run** |
| Standalone cached Kotlin 2.4.20 / JUnit 4.13.2, JVM target 17: compile actual `MyShelfBookPresentation.kt` and `MyShelfBookPresentationTest.kt`, then `org.junit.runner.JUnitCore com.aryan.reader.MyShelfBookPresentationTest` | **3 tests passed**. Only pure numeric/author normalization is verified; this is not an Android build. Local replay: `powershell -NoProfile -ExecutionPolicy Bypass -File .\build\codex-card-tests\run-card-tests.ps1` |
| `.\gradlew.bat -PdesktopOnly=true --offline --no-configuration-cache :shared:verifyPortableSharedSources` | Passed shared-source portability gate |
| `.\gradlew.bat -PdesktopOnly=true --offline --no-configuration-cache verifyCodebaseArchitecture` | Failed on 40 existing over-budget files, including MainViewModel, PdfViewerScreen and shared reader/UI files. None of this slice's changed Android library files exceeds its budget. Reader/module cleanup is outside this slice. |
| Kotlin PSI parsing, XML/resource/icon checks, `git diff --check` | Syntax/static checks only; no replacement for Compose type checking or device tests |

The first sandboxed build also encountered an unwritable default `C:\.gradle` cache; using the existing per-user cache resolved that initial access issue. Before installation, available tools included JDK 17 and NDK 26.3.11579264; the project requires JDK 21 and NDK 28.2.13676358. A temporary diagnostic override (outside tracked configuration) confirmed the separate JDK 21 blocker. A further diagnostic run reached dependency resolution but the offline cache lacks required versions including Kotlin stdlib 2.3.10 and Compose BOM 2026.06.01. No override is part of the implementation.

Build/test logs and local diagnostic scripts are under ignored `build/codex-ui-validation/` and `build/codex-card-tests/` in this workspace. Compose previews in `MyShelfHome.kt` cover compact, unfolded, dark and enlarged-text variants of the implemented Home. They were not rendered; no runtime screenshots or device-pass claims are supplied.

## Acceptance checklist for a configured Android device/emulator

- [ ] Import local PDF and EPUB books offline without an account; verify real cover/title and duplicate-import behavior.
- [ ] Verify an untouched library hides Continue Reading. Read both formats, close/reopen, and confirm the existing page/CFI/locator restoration and accurate progress.
- [ ] Verify recent-import order is unchanged after opening an older book; verify View all opens Library.
- [ ] Exercise search/clear, every sort/filter, pinned books, grid/list, contextual actions, metadata, annotation export, share/save and Settings.
- [ ] Create/rename/delete a manual collection, add/remove books, navigate child folders/breadcrumbs, and verify the correct collection is mutated.
- [ ] At 320/360dp compact, 600/840dp unfolded, landscape and split-screen widths, verify navigation adaptation, 48dp actions, capped covers, cutouts and system bars.
- [ ] Scroll Home/Library/a collection; change destination, grid/list, rotate, fold/unfold, then verify retained scroll/query/filter/selection. Resume a reader across the same changes.
- [ ] Check light/dark themes, custom fonts/accent, TalkBack, keyboard focus and 1.5x/2x text size.
- [ ] Check empty/loading states, long filenames, absent authors, missing/corrupt cover caches, corrupt PDFs, moved files and revoked folder URIs.
- [ ] Confirm original-layout PDF rendering, EPUB handling, bookmarks/highlights and import/folder sync behave as before; verify existing audiobook mini-player/secondary destinations.
- [x] Run the OSS debug build and focused Android Gradle tests with the required JDK/NDK/dependencies.
- [ ] Capture compact and unfolded screenshots after the device regression checks.

Phase 2 should begin with that device regression pass, followed by a focused review of the existing PDF viewport/resume behavior across fold/rotation. This PR does not add dual-page or renderer capabilities.
