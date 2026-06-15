package com.example.syntaxio.model;

public class UserPreferences {
    public static final String DEFAULT_DIFFICULTY = "EASY";
    public static final String DEFAULT_ASSISTANT_DETAIL_LEVEL = "Balanced";

    private final int userId;
    private String defaultDifficulty;
    private String assistantDetailLevel;
    private boolean showSolvedChallenges;

    public UserPreferences(int userId) {
        this(userId, DEFAULT_DIFFICULTY, DEFAULT_ASSISTANT_DETAIL_LEVEL, true);
    }

    public UserPreferences(
            int userId,
            String defaultDifficulty,
            String assistantDetailLevel,
            boolean showSolvedChallenges
    ) {
        this.userId = userId;
        this.defaultDifficulty = normalize(defaultDifficulty, DEFAULT_DIFFICULTY);
        this.assistantDetailLevel = normalize(assistantDetailLevel, DEFAULT_ASSISTANT_DETAIL_LEVEL);
        this.showSolvedChallenges = showSolvedChallenges;
    }

    public int getUserId() {
        return userId;
    }

    public String getDefaultDifficulty() {
        return defaultDifficulty;
    }

    public void setDefaultDifficulty(String defaultDifficulty) {
        this.defaultDifficulty = normalize(defaultDifficulty, DEFAULT_DIFFICULTY);
    }

    public String getAssistantDetailLevel() {
        return assistantDetailLevel;
    }

    public void setAssistantDetailLevel(String assistantDetailLevel) {
        this.assistantDetailLevel = normalize(assistantDetailLevel, DEFAULT_ASSISTANT_DETAIL_LEVEL);
    }

    public boolean isShowSolvedChallenges() {
        return showSolvedChallenges;
    }

    public void setShowSolvedChallenges(boolean showSolvedChallenges) {
        this.showSolvedChallenges = showSolvedChallenges;
    }

    private static String normalize(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
