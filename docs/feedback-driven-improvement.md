# Feedback-Driven Improvement Evidence

This document records the main feedback signals we used during the final sprint, the decisions made from that feedback, and the code, test, or UI changes that resulted.

Our team had limited formal meeting notes, so most feedback evidence is asynchronous: GitHub project items, branch history, local acceptance checks, screenshots, issue/task status, and test results. We used those signals to identify gaps, improve user flows, and add tests around the changed behaviour.

## Evidence Sources

- GitHub project board and issues for feature tracking.
- Feature branches and merge commits on `main`.
- Local acceptance testing with screenshots while using the JavaFX app.
- Unit tests run through Maven and GitHub Actions.
- Requirements and README documentation describing the learning-focused product goal.
- Generated Javadocs included under `docs/javadocs/apidocs`.

## Feedback Log

| Feedback / Finding | Decision | Implementation Evidence | Test / Quality Evidence |
| --- | --- | --- | --- |
| The AI assistant could give direct answers when the user asked for them. This conflicted with the product goal of guided learning rather than answer delivery. | Add a guardrail that refuses direct-answer and academic shortcut requests, then redirects the user toward step-by-step learning support. | `src/main/java/com/example/syntaxio/ai/safety/AiSafetyGuardrail.java`; merge commit `d20d8a0 Merge dew-ai-safety-guardrail`; README and requirements both describe guided support instead of full solutions. | `src/test/java/com/example/syntaxio/ai/safety/AiSafetyGuardrailTest.java`; tests check direct-answer blocking, unsafe content blocking, and varied learning-support responses. |
| The challenge page AI needed to be useful inside the current puzzle context, not just a generic chat window. | Add an AI Assistant tab that includes puzzle title, difficulty, description, current code, and latest test output in the assistant prompt. Save AI responses as hints so progress can be reviewed later. | `src/main/java/com/example/syntaxio/ai/chat/PuzzlePageAssistant.java`; `src/main/java/com/example/syntaxio/ui/controller/CodingChallengeController.java`; merge commit `a2d5810 Merge dew-coding-challenge-ai-assistant`. | `src/test/java/com/example/syntaxio/ai/chat/PuzzlePageAssistantTest.java`; `src/test/java/com/example/syntaxio/ui/controller/CodingChallengeTest.java`. |
| The home page In Progress section was empty even after opening a challenge and leaving before completion. | Persist unfinished challenge attempts and render the most recent unfinished challenge on the main menu with a Continue action. | `src/main/java/com/example/syntaxio/database/SqliteInProgressChallengeDAO.java`; `src/main/java/com/example/syntaxio/model/InProgressChallenge.java`; merge commit `0ef8e32 Merge pull request #75 from ScandalousPigeon/dew-inprogress-card`. | `src/test/java/com/example/syntaxio/database/SqliteInProgressChallengeDAOTest.java`; main menu controller tests cover the in-progress card wiring. |
| Dashboard progress was not detailed enough for users to review what they attempted, submitted, and requested hints for. | Add a progress review dialog with Attempt Review, Submission History, and Hint Log tabs. | `src/main/java/com/example/syntaxio/progress/UserProgressService.java`; `src/main/java/com/example/syntaxio/progress/UserProgressReport.java`; `src/main/java/com/example/syntaxio/ui/controller/DashboardController.java`; merge commit `7842837 Merge dew-user-progress`. | `src/test/java/com/example/syntaxio/progress/UserProgressServiceTest.java`; `src/test/java/com/example/syntaxio/ui/controller/DashboardControllerTest.java`. |
| Users needed a way to take progress data outside the app for reporting or review. | Add export progress support for CSV and PDF-style reports. | `src/main/java/com/example/syntaxio/export/ProgressReportExporter.java`; merge commit `1898ff6 Merge pull request #76 from ScandalousPigeon/dew-export-progress`. | `src/test/java/com/example/syntaxio/export/ProgressReportExporterTest.java`. |
| Dashboard needed clearer motivational feedback beyond raw progress numbers. | Add achievement evaluation and dashboard achievement display. | `src/main/java/com/example/syntaxio/achievements/AchievementService.java`; `src/main/java/com/example/syntaxio/model/Achievement.java`; commit `38fce3a Add dashboard achievements`. | `src/test/java/com/example/syntaxio/achievements/AchievementServiceTest.java`; dashboard tests check achievement labels and badge styles. |
| Profile/user settings should not show unfinished or confusing controls. The theme option was removed during acceptance because it was not implemented consistently. | Keep functional preferences only: default difficulty, AI assistant detail level, and show solved challenges. | `src/main/java/com/example/syntaxio/database/SqliteUserPreferencesDAO.java`; `src/main/java/com/example/syntaxio/model/UserPreferences.java`; `src/main/resources/com/example/syntaxio/profile-page.fxml`; merge commit `8394f54 Merge dew-user-preferences`. | `src/test/java/com/example/syntaxio/database/SqliteUserPreferencesDAOTest.java`; `src/test/java/com/example/syntaxio/ui/controller/ProfileControllerTest.java`. |
| User profile data needed to persist across sessions so the app could support progress, last puzzle, and profile state. | Extend user data storage with additional persisted profile/progress fields. | `src/main/java/com/example/syntaxio/database/SqliteUserDAO.java`; `src/main/java/com/example/syntaxio/model/User.java`; merge commit `c46f55b Merge pull request #77 from ScandalousPigeon/dew-user-data-storage`. | Existing database and controller tests run through Maven; final local run passed 96 tests. |

## Continuous Improvement Evidence

The final integration branch shows multiple focused feature branches merged into `main`, including:

- `dew-inprogress-card`
- `dew-export-progress`
- `dew-user-data-storage`
- `dew-user-preferences`
- `dew-user-progress`
- `dew-main-menu-chat`
- `dew-coding-challenge-ai-assistant`
- `dew-ai-safety-guardrail`

This branch history demonstrates iterative implementation rather than a single final commit. Several changes were added after local acceptance checks, especially the AI guardrail, profile preferences cleanup, and progress review features.

## Quality Checks

- GitHub Actions workflow: `.github/workflows/maven.yml`
- Build command used by CI: `./mvnw -B -ntp clean verify`
- Local verification after final integration: `./mvnw test`
- Latest local result: 96 tests run, 0 failures, 0 errors.
- Generated Javadocs: `docs/javadocs/apidocs/index.html`

## Walkthrough Notes

For the video, the strongest feedback-driven improvement example is the AI assistant guardrail:

1. Show the README/requirements goal: Syntaxio should guide learners rather than give full answers.
2. Explain the acceptance finding: the AI could respond too directly when asked for the answer.
3. Show `AiSafetyGuardrail.java` and its refusal messages.
4. Show `AiSafetyGuardrailTest.java` proving direct-answer requests are blocked.
5. Show the challenge page AI Assistant tab working as a guided helper.

The second strongest example is progress visibility:

1. Show the In Progress card and progress review/export work.
2. Explain that acceptance checks showed users needed clearer continuation and review paths.
3. Show the DAO/service classes and tests.
4. Show the dashboard UI with Review Progress and Export Progress.

## Reflection

The main weakness in our process was that we did not keep many formal meeting records. For future sprints, we would create short weekly feedback notes or issue comments that explicitly record:

- what feedback was received,
- who raised it,
- what decision was made,
- which branch or commit implemented the response,
- which test or screenshot verifies the change.

This would make our feedback-driven process easier to audit and present.
