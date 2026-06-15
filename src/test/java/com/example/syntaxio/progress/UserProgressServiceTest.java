package com.example.syntaxio.progress;

import com.example.syntaxio.model.Challenge;
import com.example.syntaxio.model.Hint;
import com.example.syntaxio.model.Solution;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserProgressServiceTest {

    private final UserProgressService service = new UserProgressService();

    @Test
    void buildReportSummarizesAttemptsSubmissionsAndHints() {
        UserProgressReport report = service.buildReport(
                challenges(),
                List.of(
                        solution("ch-001", true, 1, LocalDateTime.of(2026, 6, 12, 10, 0)),
                        solution("ch-001", false, 2, LocalDateTime.of(2026, 6, 11, 10, 0))
                ),
                List.of(hint("ch-001", "Try a loop", LocalDateTime.of(2026, 6, 12, 9, 30)))
        );

        assertAll(
                () -> assertEquals(2, report.attemptSummaryLines().size()),
                () -> assertTrue(report.attemptSummaryLines().get(0).contains("Attempts: 2")),
                () -> assertTrue(report.attemptSummaryLines().get(0).contains("Passed: 1")),
                () -> assertTrue(report.attemptSummaryLines().get(0).contains("Hints: 3")),
                () -> assertTrue(report.submissionHistoryLines().get(0).contains("Passed Sum of Array")),
                () -> assertTrue(report.hintLogLines().get(0).contains("Try a loop"))
        );
    }

    @Test
    void buildReportProvidesEmptyStates() {
        UserProgressReport report = service.buildReport(List.of(), List.of(), List.of());

        assertAll(
                () -> assertEquals(List.of("No challenges are available yet."), report.attemptSummaryLines()),
                () -> assertEquals(List.of("No submissions yet."), report.submissionHistoryLines()),
                () -> assertEquals(List.of("No hints requested yet."), report.hintLogLines())
        );
    }

    private List<Challenge> challenges() {
        return List.of(
                new Challenge("ch-001", "Sum of Array", "Description", "return 0;", "EASY", List.of(), "return 1;"),
                new Challenge("ch-002", "Reverse String", "Description", "return \"\";", "EASY", List.of(), "return \"a\";")
        );
    }

    private Solution solution(String challengeId, boolean passed, int hintsUsed, LocalDateTime submittedAt) {
        Solution solution = new Solution(challengeId, "code", passed, hintsUsed);
        solution.setSubmittedAt(submittedAt);
        return solution;
    }

    private Hint hint(String challengeId, String hintText, LocalDateTime requestedAt) {
        Hint hint = new Hint(challengeId, hintText, "GENERAL", 80);
        hint.setRequestedAt(requestedAt);
        return hint;
    }
}
