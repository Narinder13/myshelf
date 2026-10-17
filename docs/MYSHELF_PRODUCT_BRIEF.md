# MyShelf: Product brief and delivery roadmap

**Status:** Planning specification for the Episteme-based fork; not a claim that changes have been implemented.
**Repository:** `Narinder13/myshelf`
**Platform priority:** Android, with particular attention to the Galaxy Z Fold8 Ultra.
**Positioning:** Your personal reading room, not a generic file manager. Offline-first, visually quiet, fast, reliable, and yours.

## Product promise

A user can add all their personal PDF and EPUB books, browse attractive covers, organize collections, return to the exact reading position, and enjoy a high-quality reader without logging in. The original document layout must stay accurate.

## Core user journeys

1. **First launch:** Warm, minimal empty state with prominent **Add books** action; use Android's Storage Access Framework, not blanket file-system permissions.
2. **Import:** Pick one or multiple PDFs or EPUBs, or connect a permitted folder where existing infrastructure supports it. Show import progress and errors without freezing UI.
3. **Browse:** Home gives one-tap access to Continue Reading, Recently Added, and compact collections; Library provides search, sorting, filters, cover grid/list toggle.
4. **Organize:** Place books into existing shelves/collections, edit metadata or cover when infrastructure allows, mark favorites, and keep deleted/missing documents clearly distinguished.
5. **Read:** Tap a cover to open the existing reader; exit, rotate, fold/unfold, or re-enter without losing progress, bookmarks, annotation state, or page positioning.
6. **Maintain:** Find duplicates, handle moved files and revoked URIs, back up and restore reading metadata (later phase).

## Information architecture

- **Home:** Continue Reading hero, Recently Added, Your Collections, quick access to All Books. Restrained editorial typography and generous whitespace.
- **Library:** Cover-first grid as default with dense list alternative. Search, sort (recent/title/author/progress), filters (all/reading/unread/completed/favorites) when backed by actual data. Avoid fake counters.
- **Collections:** Custom user-named shelves; use existing shelf persistence and keep multi-shelf semantics if supported. Include empty state.
- **Settings:** App appearance, import/folder options, reading preferences, privacy, licensing, and attribution, reusing existing settings.
- **Book details:** Cover, title, author if available, format, progress, actions to read/organize. Prefer existing metadata path; do not invent data.

Navigation may reuse the present library's destinations and drawer. A bottom bar on compact Android should not accidentally duplicate existing navigation; on unfolded devices prefer an appropriate rail/pane where it helps.

## Design direction

- **Style:** Premium digital library, not a high-density dashboard. Original styling inspired by physical books and modern editorial reading.
- **Light:** Soft paper/ivory surfaces, dark ink text, delicate dividers; **dark:** charcoal/graphite surfaces and comfortable off-white text. Design via theme tokens, not scattered hard-coded colors.
- **Type:** Distinct but readable section headlines, clean book title labels, sensible max lines. Respect system font scaling.
- **Covers:** Real EPUB cover or existing first-page PDF thumbnail, using existing caching/generator. Gentle shadow and subtle corner rounding. No placeholder copyrighted cover art and no invented author metadata.
- **Progress:** Small unobtrusive indicator on books with known progress; prominent continue card shows accurate progress.
- **Motion:** Restrained transitions; respect reduced motion where available.
- **Accessibility:** Sufficient contrast, >=48dp interactive targets, meaningful semantic labels, support large fonts and TalkBack.
- **Performance:** Use lazy layouts and stable keys, cache thumbnails, avoid decoding PDF pages on the UI thread.

## Adaptive and foldable requirements

Use measured available width and the existing shared width-class helpers. Do not hardcode model-specific pixel sizes.

| Layout | Intent |
| --- | --- |
| Compact (folded phone) | One-column home feed; comfortably spaced 2-3-column cover grid depending on usable width |
| Medium (unfolded / multi-window) | More books visible while preserving readable cards, optional persistent section navigation |
| Expanded | Constrained content widths for text, more columns for shelves, thoughtful rail or side panel |

On fold/unfold or split-screen changes: maintain library filter, search, scroll position, selected shelf, current reading position, and PDF viewport as the existing engine allows. Do not reinitialize the book or reset progress due to recomposition.

## Milestones

### Phase 0: Foundation (this branch)
- [x] Document source architecture and guardrails for Codex.
- [x] Define product direction, phased acceptance criteria, and a first implementation task.
- [ ] Confirm baseline Android build in a configured build environment.
- [ ] Obtain at least one before screenshot on compact and unfolded screen/emulator.

### Phase 1: Premium bookshelf UI (first Codex implementation)
- [ ] Polish Android Home/Library/Collections experience without changing the underlying data model.
- [ ] Better cover-card composition; accurate progress, title, and cover fallbacks.
- [ ] Clear responsive spacing and layout for compact/medium/expanded widths.
- [ ] Friendly empty/loading/error states and discoverable import/search/sort controls.
- [ ] Preserve current navigation and settings access, reader opening, collection CRUD and existing folder import.
- [ ] Tests for any newly introduced pure state/layout logic; screenshots and QA notes.

### Phase 2: Reading comfort
- [ ] Review PDF page-mode/dual-page behavior, gestures, fit/zoom, toolbar and resume on foldable displays.
- [ ] Optimize large PDF page rendering and annotation interactions without replacing the proven engine.
- [ ] Reader-specific device and rotation regression suite.

### Phase 3: Personal library power features
- [ ] Book details, editable display metadata and personal covers with persistent storage.
- [ ] Strong duplicate detection, batch import/organize, safe missing-file recovery.
- [ ] Reading insights (time/progress) if measurement can be done privately and accurately.
- [ ] Optional local data export/import, tested for restore and accidental overwrite safety.
- [ ] Optional biometric lock with correct Android Keystore/biometrics behavior.

### Phase 4: Packaging and polish
- [ ] Replace upstream product-facing name/logo with original MyShelf identity, while retaining upstream license notices.
- [ ] Audit application ID, versioning, signed release/APK and update path deliberately.
- [ ] Full regression checks, accessibility, RTL/language behavior, offline mode, large libraries, launch speed and battery.
- [ ] Document installation and backup.

## Phase 1 acceptance checklist

1. A first-time user can import a PDF and see its cover/title without an account.
2. Continue Reading shows the last opened book and resumes the correct position after reopening.
3. Library grid/list, collection management, search, import and settings still function.
4. Navigating between sections, folding/unfolding and rotating does not lose reading metadata or crash.
5. PDFs open with their original formatting; annotations and EPUB handling are not regressed.
6. Empty library, corrupt files, long filenames and missing thumbnails display usable fallback UI.
7. Accessibility and responsiveness are verified on compact and unfolded widths.
8. Build/tests are run when tools allow, with exact results recorded. No fabricated pass status.

## Constraints and decisions

- **No forced cloud sync/login, analytics or network calls** in the personal library experience.
- Scope Android before modifying shared multi-platform UI; investigate call sites before shared changes.
- Existing Episteme platform features are more extensive than the initial MyShelf experience. Simplify the default UI without deleting core reader functionality.
- This is an AGPL-3.0 project. Keep all license/copyright notices and satisfy source sharing obligations if distributed.
- Brand name and color choices are working design decisions, not irreversible architecture.
