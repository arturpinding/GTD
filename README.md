# GTD

An offline Android app for capturing, clarifying, organizing, and reviewing work
with the Getting Things Done method.

## Core workflow

- Quick-capture from Home or Inbox.
- Clarify each Inbox item into Next Actions, Today, Projects, Waiting For,
  Calendar, Someday/Maybe, or Reference.
- Edit, move, complete, focus, and delete items from a visible action menu.
- Link Next Actions to Projects and see which projects still need a next action.
- Plan Today from focused actions and a time-sorted calendar schedule.
- Browse a six-week month calendar, add/edit events, and jump back to Today.
- Follow a resumable Weekly Review with live Inbox and stalled-project counts.

All data is stored locally. Existing Inbox data from earlier versions is migrated
into the unified GTD repository on first launch.

## Build

Use a complete JDK and an Android SDK containing API 36.1:

    ./gradlew testDebugUnitTest assembleDebug lintDebug
