# MyShelf: simple browsing and reading styles

Date: 2026-10-23. Branch: `codex/myshelf-simple-reading`. Target: `main`.

## Product choices

This increment makes everyday actions easier to discover while keeping the existing offline library and readers. A backend is unnecessary for importing, organizing, and reading personal books. No database migration, account requirement, new permission, service, or dependency is introduced.

- The Android launcher and About app name are **MyShelf**, including the OSS flavor override. Generated APK filenames also use MyShelf. The application ID and signing configuration remain compatible with the existing installation.
- Home adds Reading, Unread, and Finished shortcuts with actual counts, icons, and light/dark theme container colors. Selecting a shortcut opens the existing Library, clearing search and advanced filters so its results match the count.
- Existing pinned books appear in a Home row when present. Long-press actions, selection, cached covers, and reader navigation reuse the existing implementation.
- Library sort/status controls have icons, and Add books has a clearer accent. Collections retain the existing creation dialog and gain a clearer empty state.
- PDF and EPUB menus expose **Reading style**: Scroll up and down, Swipe left and right, and Book page turn. These choices compose the existing mode and page-turn preferences. Right-to-left direction remains available separately, as does EPUB beta scrolling. Existing text-to-speech mode-switch guards are retained.

## Reader limits

The page curl is an existing visual effect, not a new rendering engine. PDFs use it at the original zoom level; zoomed pages retain their current panning behavior. The animation preference is already shared between PDF and EPUB, so changing it affects both formats. Current mode-switch locator/page restoration paths are reused.

Status shortcuts deliberately use the same predicates as Library. Unknown progress is treated as unread by the existing filter, even when a saved position is available for Continue Reading. This increment does not reinterpret or migrate reading metadata.

## Code paths

- Branding/package output: `app/src/main/res/values/strings.xml`, `app/src/oss/res/values/strings.xml`, `app/build.gradle.kts`.
- Home, pins, counts: `MyShelfHome.kt`, `MyShelfHomeQuickAccess.kt`, `values/myshelf_home_strings.xml`.
- Library controls and shortcut navigation: `MyShelfLibrary.kt`, `UnifiedLibraryScreen.kt`.
- Reading choices: `pdf/PdfToolbars.kt`, `epubreader/EpubReaderControls.kt`, `values/myshelf_reader_strings.xml`.
- Regression coverage: `MyShelfHomeTest.kt`, `AndroidStringFormatResourcesTest.kt`, and existing reader preference/position tests.

## Verification

The unmodified baseline OSS debug build passed in 45 seconds (76 tasks, 9 executed). The configured toolchain uses the existing Microsoft JDK 21, Android SDK, and per-user Gradle cache. An initial isolated-cache attempt could not resolve Android Gradle plugin 9.1.0 offline; the first attempt to access the populated user cache was sandbox-denied. The configured build succeeded with the permitted cache access and no build-setting changes.

| Check | Actual result |
| --- | --- |
| `:app:assembleOssDebug :app:testOssDebugUnitTest` with the nine class filters below | Passed in 2m 54s; 83 tasks, 30 executed. 102 tests, zero failures/errors/skips. |
| SDK build-tools 36.0.0 `aapt dump badging` | Packaged application label is MyShelf in every included locale; package remains `com.aryan.reader.oss`, version `1.0.58-oss` (64). |
| SDK build-tools 36.0.0 `apksigner verify --verbose` | Passed; APK Signature Scheme v2, one signer. |
| `git diff --check` | Passed. |
| Android device/emulator | No connected device and no configured AVD; runtime checks not run. |

Reproduction after setting the process-local JDK/SDK paths and `GRADLE_USER_HOME` to the existing user cache:

```powershell
.\gradlew.bat :app:assembleOssDebug :app:testOssDebugUnitTest `
  --tests com.aryan.reader.MyShelfHomeTest `
  --tests com.aryan.reader.MyShelfBookPresentationTest `
  --tests com.aryan.reader.UnifiedLibraryScreenTest `
  --tests com.aryan.reader.AndroidStringFormatResourcesTest `
  --tests com.aryan.reader.epubreader.EpubReaderBridgeAndControlsTest `
  --tests com.aryan.reader.epubreader.EpubReaderPreferencesAndAnnotationsTest `
  --tests com.aryan.reader.epubreader.EpubReaderPositionDurabilityTest `
  --tests com.aryan.reader.pdf.PdfReaderSettingsAndSharedModelsTest `
  --tests com.aryan.reader.pdf.PdfReaderPreferencesTest `
  --offline --no-configuration-cache --console=plain --max-workers=2
```

Logs: ignored `build/myshelf-baseline.log` and `build/myshelf-update-validation.log`. Build warnings are existing deprecations, Kotlin annotation warnings, and an OpenJDK class-sharing warning; no compilation failure occurred.

Installer: `output/MyShelf-debug.apk` (102,378,095 bytes), SHA256 `F846AA3900CF37349FF6756038FA4B06785034E1D0C48E21A57575BEEFD1131A`. It matches `app/build/outputs/apk/oss/debug/app-oss-debug.apk`. The MyShelf-named Gradle copy is `app/build/outputs/renamed-apk/ossDebug/MyShelf-oss-v1.0.58-oss-debug.apk`. These remain ignored build artifacts.

No Android device is connected and no emulator AVD is configured. Screenshots and device acceptance are therefore pending; source review and JVM tests do not establish visual quality or gesture behavior.

## Device acceptance checklist

- [ ] Update an existing installation and confirm the launcher says MyShelf and books/progress remain present.
- [ ] Import a PDF and EPUB offline; check empty state, cover fallback, long filenames, and missing-file errors.
- [ ] Open each Home shortcut after setting a search and advanced filters; confirm its count and resulting books agree.
- [ ] Pin/unpin books through existing long-press actions and confirm the Home row updates without stale/deleted entries.
- [ ] Switch among all three reading styles midway through PDF and EPUB books, exit/reopen, and verify position, bookmarks, and annotations.
- [ ] Check RTL direction, disabled mode changes during text-to-speech, PDF original-zoom curl, and zoomed panning.
- [ ] Verify compact/unfolded/split-screen widths, rotation, light/dark themes, TalkBack, and large text.

The next increment should prioritize this device regression pass, then make missing-file recovery and duplicate-import feedback clearer using existing import/storage infrastructure.
