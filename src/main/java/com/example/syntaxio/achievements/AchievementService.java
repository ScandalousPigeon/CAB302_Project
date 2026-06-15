package com.example.syntaxio.achievements;

import com.example.syntaxio.model.Achievement;
import com.example.syntaxio.model.Challenge;
import com.example.syntaxio.model.Solution;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class AchievementService {

    public List<Achievement> evaluate(List<Challenge> challenges, List<Solution> solutions, int hintsUsed) {
        Set<String> completedChallengeIds = solutions.stream()
                .filter(Solution::isPassed)
                .map(Solution::getChallengeId)
                .collect(Collectors.toSet());

        int completedChallenges = completedChallengeIds.size();
        int totalChallenges = challenges.size();

        return List.of(
                achievement(
                        "first-steps",
                        "First Steps",
                        "Complete your first challenge.",
                        completedChallenges >= 1
                ),
                achievement(
                        "practice-sprint",
                        "Practice Sprint",
                        "Submit five solutions.",
                        solutions.size() >= 5
                ),
                achievement(
                        "independent-solver",
                        "Independent Solver",
                        "Complete a challenge without using hints.",
                        completedChallenges >= 1 && hintsUsed == 0
                ),
                achievement(
                        "easy-track",
                        "Easy Track",
                        "Complete every easy challenge.",
                        hasCompletedAllChallengesForDifficulty(challenges, completedChallengeIds, "EASY")
                ),
                achievement(
                        "medium-track",
                        "Medium Track",
                        "Complete every medium challenge.",
                        hasCompletedAllChallengesForDifficulty(challenges, completedChallengeIds, "MEDIUM")
                ),
                achievement(
                        "hard-won",
                        "Hard Won",
                        "Complete at least one hard challenge.",
                        hasCompletedAnyChallengeForDifficulty(challenges, completedChallengeIds, "HARD")
                ),
                achievement(
                        "hard-track",
                        "Hard Track",
                        "Complete every hard challenge.",
                        hasCompletedAllChallengesForDifficulty(challenges, completedChallengeIds, "HARD")
                ),
                achievement(
                        "completionist",
                        "Completionist",
                        "Complete every current challenge.",
                        totalChallenges > 0 && completedChallenges == totalChallenges
                )
        );
    }

    private Achievement achievement(String id, String title, String description, boolean unlocked) {
        return new Achievement(id, title, description, unlocked);
    }

    private boolean hasCompletedAllChallengesForDifficulty(
            List<Challenge> challenges,
            Set<String> completedChallengeIds,
            String difficulty
    ) {
        List<Challenge> matchingChallenges = challenges.stream()
                .filter(challenge -> matchesDifficulty(challenge, difficulty))
                .toList();

        return !matchingChallenges.isEmpty()
                && matchingChallenges.stream().allMatch(challenge -> completedChallengeIds.contains(challenge.getId()));
    }

    private boolean hasCompletedAnyChallengeForDifficulty(
            List<Challenge> challenges,
            Set<String> completedChallengeIds,
            String difficulty
    ) {
        return challenges.stream()
                .filter(challenge -> matchesDifficulty(challenge, difficulty))
                .anyMatch(challenge -> completedChallengeIds.contains(challenge.getId()));
    }

    private boolean matchesDifficulty(Challenge challenge, String difficulty) {
        return challenge.getDifficulty() != null
                && challenge.getDifficulty().toUpperCase(Locale.ENGLISH).equals(difficulty);
    }
}
