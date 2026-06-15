package com.example.syntaxio.achievements;

import com.example.syntaxio.model.Achievement;
import com.example.syntaxio.model.Challenge;
import com.example.syntaxio.model.Solution;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class AchievementServiceTest {

    private final AchievementService service = new AchievementService();

    @Test
    void evaluateLocksAchievementsWhenUserHasNoProgress() {
        Map<String, Achievement> achievements = byId(service.evaluate(challenges(), List.of(), 0));

        assertAll(
                () -> assertFalse(achievements.get("first-steps").unlocked()),
                () -> assertFalse(achievements.get("practice-sprint").unlocked()),
                () -> assertFalse(achievements.get("independent-solver").unlocked()),
                () -> assertFalse(achievements.get("easy-track").unlocked()),
                () -> assertFalse(achievements.get("medium-track").unlocked()),
                () -> assertFalse(achievements.get("hard-won").unlocked()),
                () -> assertFalse(achievements.get("hard-track").unlocked()),
                () -> assertFalse(achievements.get("completionist").unlocked())
        );
    }

    @Test
    void evaluateUnlocksProgressAchievementsFromSolutions() {
        List<Solution> solutions = List.of(
                passed("easy-1"),
                failed("easy-2"),
                failed("hard-1"),
                failed("easy-1"),
                failed("easy-2")
        );

        Map<String, Achievement> achievements = byId(service.evaluate(challenges(), solutions, 0));

        assertAll(
                () -> assertTrue(achievements.get("first-steps").unlocked()),
                () -> assertTrue(achievements.get("practice-sprint").unlocked()),
                () -> assertTrue(achievements.get("independent-solver").unlocked()),
                () -> assertFalse(achievements.get("easy-track").unlocked()),
                () -> assertFalse(achievements.get("medium-track").unlocked()),
                () -> assertFalse(achievements.get("hard-won").unlocked()),
                () -> assertFalse(achievements.get("hard-track").unlocked()),
                () -> assertFalse(achievements.get("completionist").unlocked())
        );
    }

    @Test
    void evaluateUnlocksDifficultyAndCompletionAchievements() {
        List<Solution> solutions = List.of(
                passed("easy-1"),
                passed("easy-2"),
                passed("medium-1"),
                passed("hard-1"),
                passed("hard-2")
        );

        Map<String, Achievement> achievements = byId(service.evaluate(challenges(), solutions, 2));

        assertAll(
                () -> assertTrue(achievements.get("easy-track").unlocked()),
                () -> assertTrue(achievements.get("medium-track").unlocked()),
                () -> assertTrue(achievements.get("hard-won").unlocked()),
                () -> assertTrue(achievements.get("hard-track").unlocked()),
                () -> assertTrue(achievements.get("completionist").unlocked()),
                () -> assertFalse(achievements.get("independent-solver").unlocked())
        );
    }

    @Test
    void evaluateKeepsDifficultyTrackLockedUntilEveryChallengeInThatDifficultyIsComplete() {
        List<Solution> solutions = List.of(
                passed("medium-1"),
                passed("hard-1")
        );

        Map<String, Achievement> achievements = byId(service.evaluate(challenges(), solutions, 1));

        assertAll(
                () -> assertTrue(achievements.get("medium-track").unlocked()),
                () -> assertTrue(achievements.get("hard-won").unlocked()),
                () -> assertFalse(achievements.get("hard-track").unlocked()),
                () -> assertFalse(achievements.get("completionist").unlocked())
        );
    }

    private Map<String, Achievement> byId(List<Achievement> achievements) {
        return achievements.stream().collect(Collectors.toMap(Achievement::id, Function.identity()));
    }

    private List<Challenge> challenges() {
        return List.of(
                challenge("easy-1", "EASY"),
                challenge("easy-2", "EASY"),
                challenge("medium-1", "MEDIUM"),
                challenge("hard-1", "HARD"),
                challenge("hard-2", "HARD")
        );
    }

    private Challenge challenge(String id, String difficulty) {
        return new Challenge(id, id, "Description", "return 0;", difficulty, List.of(), "return 1;");
    }

    private Solution passed(String challengeId) {
        return new Solution(challengeId, "code", true, 0);
    }

    private Solution failed(String challengeId) {
        return new Solution(challengeId, "code", false, 0);
    }
}
