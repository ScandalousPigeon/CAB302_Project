package com.example.syntaxio.export;

import com.example.syntaxio.model.Achievement;
import com.example.syntaxio.model.Challenge;
import com.example.syntaxio.model.Solution;
import com.example.syntaxio.model.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ProgressReportExporter {

    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

    public Path exportCsv(
            User user,
            List<Challenge> challenges,
            List<Solution> solutions,
            List<Achievement> achievements,
            Path directory
    ) throws IOException {
        Path file = directory.resolve(fileName(user, "csv"));
        Files.createDirectories(directory);
        Files.writeString(file, buildCsv(user, challenges, solutions, achievements), StandardCharsets.UTF_8);
        return file;
    }

    public Path exportPdf(
            User user,
            List<Challenge> challenges,
            List<Solution> solutions,
            List<Achievement> achievements,
            Path directory
    ) throws IOException {
        Path file = directory.resolve(fileName(user, "pdf"));
        Files.createDirectories(directory);
        Files.writeString(file, buildPdf(reportLines(user, challenges, solutions, achievements)), StandardCharsets.ISO_8859_1);
        return file;
    }

    public Path downloadsDirectory() {
        return Path.of(System.getProperty("user.home"), "Downloads");
    }

    private String buildCsv(
            User user,
            List<Challenge> challenges,
            List<Solution> solutions,
            List<Achievement> achievements
    ) {
        List<String> lines = new ArrayList<>();
        ReportMetrics metrics = metrics(challenges, solutions);
        List<Achievement> unlockedAchievements = unlockedAchievements(achievements);

        lines.add("Syntaxio Progress Report");
        lines.add(csv("Username", user.getUsername()));
        lines.add(csv("Join Date", DISPLAY_DATE_FORMAT.format(user.getCreatedAt().toLocalDate())));
        lines.add(csv("Challenges Completed", metrics.completedChallenges() + " / " + metrics.totalChallenges()));
        lines.add(csv("Completion Rate", String.format(Locale.ENGLISH, "%.1f%%", metrics.completionRate())));
        lines.add(csv("Success Rate", String.format(Locale.ENGLISH, "%.1f%%", metrics.successRate())));
        lines.add("");
        lines.add("Hints Used Per Challenge");
        lines.add(csv("Challenge", "Hints Used", "Passed"));

        Map<String, String> challengeTitles = challenges.stream()
                .collect(Collectors.toMap(Challenge::getId, Challenge::getTitle, (first, second) -> first));
        Map<String, Integer> hintsByChallenge = hintsByChallenge(solutions);
        Set<String> completedChallengeIds = completedChallengeIds(solutions);

        challenges.forEach(challenge -> lines.add(csv(
                challenge.getTitle(),
                String.valueOf(hintsByChallenge.getOrDefault(challenge.getId(), 0)),
                completedChallengeIds.contains(challenge.getId()) ? "Yes" : "No"
        )));

        solutions.stream()
                .map(Solution::getChallengeId)
                .filter(challengeId -> !challengeTitles.containsKey(challengeId))
                .distinct()
                .forEach(challengeId -> lines.add(csv(
                        challengeId,
                        String.valueOf(hintsByChallenge.getOrDefault(challengeId, 0)),
                        completedChallengeIds.contains(challengeId) ? "Yes" : "No"
                )));

        lines.add("");
        lines.add("Achievement Badges Earned");
        if (unlockedAchievements.isEmpty()) {
            lines.add(csv("None yet"));
        } else {
            unlockedAchievements.forEach(achievement -> lines.add(csv(achievement.title(), achievement.description())));
        }

        return String.join(System.lineSeparator(), lines) + System.lineSeparator();
    }

    private List<String> reportLines(
            User user,
            List<Challenge> challenges,
            List<Solution> solutions,
            List<Achievement> achievements
    ) {
        ReportMetrics metrics = metrics(challenges, solutions);
        List<String> lines = new ArrayList<>();
        lines.add("Syntaxio Progress Report");
        lines.add("Username: " + user.getUsername());
        lines.add("Join Date: " + DISPLAY_DATE_FORMAT.format(user.getCreatedAt().toLocalDate()));
        lines.add("Challenges Completed: " + metrics.completedChallenges() + " / " + metrics.totalChallenges());
        lines.add(String.format(Locale.ENGLISH, "Completion Rate: %.1f%%", metrics.completionRate()));
        lines.add(String.format(Locale.ENGLISH, "Success Rate: %.1f%%", metrics.successRate()));
        lines.add("");
        lines.add("Hints Used Per Challenge");

        Map<String, Integer> hintsByChallenge = hintsByChallenge(solutions);
        Set<String> completedChallengeIds = completedChallengeIds(solutions);
        for (Challenge challenge : challenges) {
            lines.add("- " + challenge.getTitle() + ": "
                    + hintsByChallenge.getOrDefault(challenge.getId(), 0)
                    + " hints, "
                    + (completedChallengeIds.contains(challenge.getId()) ? "passed" : "not passed"));
        }

        lines.add("");
        lines.add("Achievement Badges Earned");
        List<Achievement> unlockedAchievements = unlockedAchievements(achievements);
        if (unlockedAchievements.isEmpty()) {
            lines.add("- None yet");
        } else {
            unlockedAchievements.forEach(achievement -> lines.add("- " + achievement.title()));
        }

        return lines;
    }

    private String buildPdf(List<String> lines) {
        StringBuilder text = new StringBuilder();
        text.append("BT\n/F1 12 Tf\n14 TL\n50 780 Td\n");
        for (String line : lines) {
            text.append("(").append(escapePdf(line)).append(") Tj\nT*\n");
        }
        text.append("ET");

        String stream = text.toString();
        List<String> objects = List.of(
                "<< /Type /Catalog /Pages 2 0 R >>",
                "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>",
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>",
                "<< /Length " + stream.getBytes(StandardCharsets.ISO_8859_1).length + " >>\nstream\n" + stream + "\nendstream"
        );

        StringBuilder pdf = new StringBuilder("%PDF-1.4\n");
        List<Integer> offsets = new ArrayList<>();
        for (int index = 0; index < objects.size(); index++) {
            offsets.add(pdf.length());
            pdf.append(index + 1).append(" 0 obj\n").append(objects.get(index)).append("\nendobj\n");
        }

        int xrefOffset = pdf.length();
        pdf.append("xref\n0 ").append(objects.size() + 1).append("\n");
        pdf.append("0000000000 65535 f \n");
        for (int offset : offsets) {
            pdf.append(String.format(Locale.ENGLISH, "%010d 00000 n \n", offset));
        }
        pdf.append("trailer\n<< /Size ").append(objects.size() + 1).append(" /Root 1 0 R >>\n");
        pdf.append("startxref\n").append(xrefOffset).append("\n%%EOF\n");
        return pdf.toString();
    }

    private String fileName(User user, String extension) {
        return "syntaxio-progress-"
                + sanitize(user.getUsername())
                + "-"
                + LocalDate.now().format(FILE_DATE_FORMAT)
                + "."
                + extension;
    }

    private ReportMetrics metrics(List<Challenge> challenges, List<Solution> solutions) {
        int totalChallenges = challenges.size();
        int completedChallenges = completedChallengeIds(solutions).size();
        long passedSubmissions = solutions.stream().filter(Solution::isPassed).count();
        double completionRate = totalChallenges == 0 ? 0 : completedChallenges * 100.0 / totalChallenges;
        double successRate = solutions.isEmpty() ? 0 : passedSubmissions * 100.0 / solutions.size();
        return new ReportMetrics(totalChallenges, completedChallenges, completionRate, successRate);
    }

    private Set<String> completedChallengeIds(List<Solution> solutions) {
        return solutions.stream()
                .filter(Solution::isPassed)
                .map(Solution::getChallengeId)
                .collect(Collectors.toSet());
    }

    private Map<String, Integer> hintsByChallenge(List<Solution> solutions) {
        return solutions.stream()
                .collect(Collectors.groupingBy(
                        Solution::getChallengeId,
                        Collectors.summingInt(Solution::getHintsUsedForThisSolution)
                ));
    }

    private List<Achievement> unlockedAchievements(List<Achievement> achievements) {
        return achievements.stream().filter(Achievement::unlocked).toList();
    }

    private String csv(String... columns) {
        return java.util.Arrays.stream(columns)
                .map(this::escapeCsv)
                .collect(Collectors.joining(","));
    }

    private String escapeCsv(String value) {
        String safe = value == null ? "" : value;
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }

    private String escapePdf(String value) {
        return value.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private String sanitize(String value) {
        return value.toLowerCase(Locale.ENGLISH).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }

    private record ReportMetrics(int totalChallenges, int completedChallenges, double completionRate, double successRate) {
    }
}
