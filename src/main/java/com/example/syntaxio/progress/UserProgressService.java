package com.example.syntaxio.progress;

import com.example.syntaxio.model.Challenge;
import com.example.syntaxio.model.Hint;
import com.example.syntaxio.model.Solution;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class UserProgressService {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("MMM d, h:mm a", Locale.ENGLISH);

    public UserProgressReport buildReport(List<Challenge> challenges, List<Solution> solutions, List<Hint> hints) {
        Map<String, String> challengeTitles = challenges.stream()
                .collect(Collectors.toMap(Challenge::getId, Challenge::getTitle, (first, second) -> first));

        return new UserProgressReport(
                buildAttemptSummary(challenges, solutions),
                buildSubmissionHistory(solutions, challengeTitles),
                buildHintLog(hints, challengeTitles)
        );
    }

    private List<String> buildAttemptSummary(List<Challenge> challenges, List<Solution> solutions) {
        if (challenges.isEmpty()) {
            return List.of("No challenges are available yet.");
        }

        Map<String, List<Solution>> solutionsByChallenge = solutions.stream()
                .collect(Collectors.groupingBy(Solution::getChallengeId));

        return challenges.stream()
                .map(challenge -> {
                    List<Solution> challengeSolutions = solutionsByChallenge.getOrDefault(challenge.getId(), List.of());
                    long passedAttempts = challengeSolutions.stream().filter(Solution::isPassed).count();
                    int hintsUsed = challengeSolutions.stream().mapToInt(Solution::getHintsUsedForThisSolution).sum();
                    String status = passedAttempts > 0 ? "Completed" : "In progress";

                    return String.format(
                            Locale.ENGLISH,
                            "%s - %s | Attempts: %d | Passed: %d | Hints: %d",
                            challenge.getTitle(),
                            status,
                            challengeSolutions.size(),
                            passedAttempts,
                            hintsUsed
                    );
                })
                .toList();
    }

    private List<String> buildSubmissionHistory(List<Solution> solutions, Map<String, String> challengeTitles) {
        if (solutions.isEmpty()) {
            return List.of("No submissions yet.");
        }

        return solutions.stream()
                .sorted(Comparator.comparing(Solution::getSubmittedAt).reversed())
                .map(solution -> String.format(
                        Locale.ENGLISH,
                        "%s - %s %s | Hints: %d",
                        solution.getSubmittedAt().format(TIMESTAMP_FORMATTER),
                        solution.isPassed() ? "Passed" : "Attempted",
                        challengeTitles.getOrDefault(solution.getChallengeId(), solution.getChallengeId()),
                        solution.getHintsUsedForThisSolution()
                ))
                .toList();
    }

    private List<String> buildHintLog(List<Hint> hints, Map<String, String> challengeTitles) {
        if (hints.isEmpty()) {
            return List.of("No hints requested yet.");
        }

        return hints.stream()
                .sorted(Comparator.comparing(Hint::getRequestedAt).reversed())
                .map(hint -> String.format(
                        Locale.ENGLISH,
                        "%s - %s hint for %s | %s | Helpful: %s",
                        hint.getRequestedAt().format(TIMESTAMP_FORMATTER),
                        hint.getHintType(),
                        challengeTitles.getOrDefault(hint.getChallengeId(), hint.getChallengeId()),
                        hint.getHintText(),
                        hint.isWasHelpful() ? "Yes" : "No"
                ))
                .toList();
    }
}
