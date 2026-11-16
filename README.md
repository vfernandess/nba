# NBA

A small NBA browser for Android. Two tabs, Home (teams) and Players
(search), share one bottom sheet that lists a team's games.

- **Home**: a table of the 30 current teams (name, city, conference). The "Name" pill in the top
  bar opens a sort dialog (Name, City, Conference). Name sorts by the team name ("76ers",
  "Bucks", "Bulls"…): every full name starts with the city, so sorting by full name would just
  repeat the City order. Tapping a team opens its games.
- **Players**: search by first or last name, with paged results. Tapping a player opens their
  team's games.
- **Team games sheet**: header with a close (✕) button and the team name, a table of Home Name,
  Home Score, Visitor Name and Visitor Score, newest games first, with endless scroll. The mockup
  shows a "< Home" back link; a close button was chosen instead because the screen is a sheet laid
  over the list, not a pushed page. Long team names wrap to two lines as in the mockup.

Stack: Kotlin, Jetpack Compose + Material 3, Hilt, Navigation Compose (type-safe routes), Ktor,
kotlinx.serialization, Coroutines/Flow, NDK (API key), JUnit4 + Turbine.

## Setup

1. Get a free API key at [app.balldontlie.io](https://app.balldontlie.io).
2. Add it to `local.properties` in the project root (the file is git-ignored):

   ```properties
   BALLDONTLIE_API_KEY=your-key-here
   ```

   Or export it as an environment variable of the same name, which is useful on CI:

   ```sh
   export BALLDONTLIE_API_KEY=your-key-here
   ```

   `BALLDONTLIE_BASE_URL` can be set the same way. It defaults to `https://api.balldontlie.io/v1/`.

   `local.properties` is parsed by `java.util.Properties`, so a backslash in a value is an escape
   character and must be doubled.

The key is read at **build time** only. Without it the build still succeeds and logs a warning,
and the app shows an "API key missing" error.

## Run and test

```sh
./gradlew installDebug                 # build and install on a connected device or emulator
./gradlew testDebugUnitTest            # JVM unit tests
./gradlew connectedDebugAndroidTest    # instrumented test (native key provider), needs a device
./gradlew assembleDebug                # build the APK only
```

Requirements: Android SDK with the NDK and CMake installed, minSdk 29.

### UI tests (Maestro)

End-to-end flows live in `.maestro/` and run against the real balldontlie API:

| Flow | Covers |
|---|---|
| `home_sort` | Sort pill and dialog (Name / City / Conference), table order, scroll back to top on a new sort |
| `home_game_sheet` | Team games sheet: header, games table, load-more on scroll, close via the Close button and system back |
| `players_search` | Empty-query prompt, search for "james", player games sheet closed with Close |
| `tab_state` | Switching tabs keeps the Home scroll position and the Players query and results |
| `offline_retry` | Airplane mode shows the network error, Retry recovers once back online |

Prerequisites: the [Maestro CLI](https://maestro.dev), one running emulator or connected device, and an
API key set up as described in [Setup](#setup) (it is baked into the build).

```sh
./gradlew installDebug && maestro test .maestro/   # whole suite, in the order set in .maestro/config.yaml
maestro test .maestro/players_search.yaml           # a single flow
maestro test --include-tags smoke .maestro/         # home_sort, home_game_sheet, players_search
```

The free API tier allows 5 requests per minute and each flow spends 1–5, so every flow starts with a
60 s cooldown (the full suite takes about 12 minutes). Pass `-e COOLDOWN_SECONDS=0` to skip it when
the quota is known to be fresh. If a request still gets a 429, the flow waits for the quota window and
taps Retry once. `offline_retry` switches airplane mode on and always switches it off again at the end.

## Architecture

### FUSE (Flow Unified State Effect)

Every screen uses FUSE, a small unidirectional architecture (`core/fuse`):

```
View --Command--> ViewModel --> Interactor --> use cases --> Flow<Effect>
  ^                                  |                            |
  |                                  +--SideEffect (navigation)   v
  +------------------- State <------------------------------ Reducer
```

- The **View** sends a `Command` (for example `Search(query)` or `LoadMore`).
- The **Interactor** routes each command to feature use cases. Each use case returns a
  `Flow<Effect>`: `Loading`, then a success effect or `ShowError(AppError)`.
- The **Reducer** is a pure function `(State, Effect) -> State`. It is the only place list
  state changes, paging included.
- One-off events, such as opening a team's games, are `SideEffect`s sent over an unlimited
  channel and collected with `OnEffect`. Navigation can't be dropped, as it could be on the
  rendezvous effect channel.
- The **ViewModel** (`EffectViewModel` / `SideEffectViewModel`) wires these together on an
  injected `CoroutineContext`, so tests can run on a test dispatcher.

Two additions to the base: `dispatch(command, cancelKey)` cancels the previous in-flight job
with the same key, and `LazyListState.OnReachedEnd(buffer)` triggers endless scroll.

Every page renders a shared `ContentState` (Loading, Empty, Error, Success) through
`StateContent`. A failure while loading more rows shows an inline "Retry" footer and keeps the
rows already loaded.

### Packages

A single `:app` module, split by feature into packages. Package names are singular.

```
com.voidx.nba
├── app/            NbaApplication, MainActivity, NbaApp (Scaffold + bottom bar),
│                   RootNavigation (NavHost), TopLevelDestination; the only package that knows every feature
├── core/
│   ├── fuse/       FUSE base + compose/ext (OnEffect, OnReachedEnd)
│   ├── network/    KeyProvider, NativeKeyProvider (JNI)
│   ├── di/         KeyModule, NetworkModule, DispatcherModule, DataModule
│   ├── common/     AppError
│   └── ui/         theme, TableHeader/TableRow, ContentState + StateContent
├── data/
│   ├── remote/     BallDontLieApi (Ktor), HttpClientFactory, DTOs
│   ├── mapper/     DTO -> domain
│   └── repository/ TeamRepositoryImpl, PlayerRepositoryImpl, GameRepositoryImpl
├── domain/
│   ├── model/      Team, Player, Game, Page<T>, TeamSort
│   └── repository/ repository interfaces
└── feature/
    ├── home/       HomeNavigation, HomeModule, business/, presentation/, view/
    ├── player/     same shape
    └── game/       same shape; the team games sheet
```

Dependencies point one way: `feature -> domain <- data`. Every package can use `core`. Features
never import each other. Each feature exposes a `@Serializable` route,
`NavGraphBuilder.xxxPage(...)` and `NavController.navigateToXxx(...)`, and
`app/RootNavigation` connects them. The games sheet is a dialog destination that hosts a
`ModalBottomSheet`. Its ViewModel reads `teamId` and `teamName` from the
`SavedStateHandle`. Tab switches use `popUpTo(start) { saveState = true }`, `launchSingleTop` and
`restoreState`, so each tab keeps its list and scroll position.

Inside a feature: `business/` holds the use cases and the ViewModel, `presentation/` holds the
Command, Effect, SideEffect, State, Interactor and Reducer, and `view/` holds a stateful `XxxPage`
that delegates to a stateless, previewable `XxxPageContent`.
