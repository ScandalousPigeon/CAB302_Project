package com.example.syntaxio.export;

import com.example.syntaxio.model.Achievement;
import com.example.syntaxio.model.Challenge;
import com.example.syntaxio.model.Solution;
import com.example.syntaxio.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProgressReportExporterTest {

    @TempDir
    Path tempDir;

    private final ProgressReportExporter exporter = new ProgressReportExporter();

    @Test
    void exportCsvWritesProgressReportWithRequiredFields() throws Exception {
        Path file = exporter.exportCsv(user(), challenges(), solutions(), achievements(), tempDir);

        String report = Files.readString(file, StandardCharsets.UTF_8);

        assertAll(
                () -> assertTrue(Files.exists(file)),
                () -> assertTrue(file.getFileName().toString().contains("syntaxio-progress-diu888")),
                () -> assertTrue(file.getFileName().toString().contains(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE))),
                () -> assertTrue(report.contains("\"Username\",\"Diu888\"")),
                () -> assertTrue(report.contains("\"Join Date\",\"Jun 1, 2026\"")),
                () -> assertTrue(report.contains("\"Challenges Completed\",\"1 / 2\"")),
                () -> assertTrue(report.contains("\"Completion Rate\",\"50.0%\"")),
                () -> assertTrue(report.contains("\"Success Rate\",\"50.0%\"")),
                () -> assertTrue(report.contains("Hints Used Per Challenge")),
                () -> assertTrue(report.contains("\"Sum of Array\",\"2\",\"Yes\"")),
                () -> assertTrue(report.contains("Achievement Badges Earned")),
                () -> assertTrue(report.contains("\"First Steps\",\"Complete your first challenge.\""))
        );
    }

    @Test
    void exportPdfWritesReadablePdfFile() throws Exception {
        Path file = exporter.exportPdf(user(), challenges(), solutions(), achievements(), tempDir);

        String report = Files.readString(file, StandardCharsets.ISO_8859_1);

        assertAll(
                () -> assertTrue(Files.exists(file)),
                () -> assertTrue(file.getFileName().toString().endsWith(".pdf")),
                () -> assertTrue(report.startsWith("%PDF-1.4")),
                () -> assertTrue(report.contains("Syntaxio Progress Report")),
                () -> assertTrue(report.contains("Username: Diu888")),
                () -> assertTrue(report.contains("Achievement Badges Earned"))
        );
    }

    private User user() {
        return new User(
                1,
                "Diu888",
                "hash",
                LocalDateTime.of(2026, 6, 1, 9, 0),
                LocalDateTime.of(2026, 6, 15, 9, 0),
                2,
                1
        );
    }

    private List<Challenge> challenges() {
        return List.of(
                challenge("ch-001", "Sum of Array"),
                challenge("ch-002", "Reverse String")
        );
    }

    private Challenge challenge(String id, String title) {
        return new Challenge(id, title, "Description", "return 0;", "EASY", List.of(), "return 1;");
    }

    private List<Solution> solutions() {
        return List.of(
                solution("ch-001", true, 2),
                solution("ch-002", false, 1)
        );
    }

    private Solution solution(String challengeId, boolean passed, int hintsUsed) {
        Solution solution = new Solution(challengeId, "code", passed, hintsUsed);
        solution.setSubmittedAt(LocalDateTime.of(2026, 6, 10, 12, 0));
        return solution;
    }

    private List<Achievement> achievements() {
        return List.of(
                new Achievement("first-steps", "First Steps", "Complete your first challenge.", true),
                new Achievement("completionist", "Completionist", "Complete every current challenge.", false)
        );
    }
}
