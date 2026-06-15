package com.example.syntaxio.model;

import java.time.LocalDateTime;

/**
 * Represents an application user and their account-level progress summary.
 *
 * <p>The user model stores identity and authentication data alongside summary
 * counters used by profile and dashboard views, such as total hints used,
 * completed challenges, login count, and current activity streak.</p>
 */
public class User {
    private int id;                   
    private String username;
    private String displayName;
    private String passwordHash;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private LocalDateTime updatedAt;
    private int loginCount;
    private int totalHintsUsed;
    private int totalChallengesCompleted;
    private int currentActivityStreak;
    private int experiencePoints;
    private String lastPuzzleId;

    /**
     * Creates a new user before it has been assigned a database id.
     *
     * <p>Account timestamps are initialized to the current time and progress
     * counters are initialized to zero.</p>
     *
     * @param username unique username for sign-in
     * @param passwordHash hashed password value
     */
    public User(String username, String passwordHash) {
        LocalDateTime now = LocalDateTime.now();
        this.username = username;
        this.displayName = username;
        this.passwordHash = passwordHash;
        this.createdAt = now;
        this.lastLoginAt = now;
        this.updatedAt = now;
        this.loginCount = 0;
        this.totalHintsUsed = 0;
        this.totalChallengesCompleted = 0;
        this.currentActivityStreak = 0;
        this.experiencePoints = 0;
    }

    /**
     * Creates a user loaded from storage with core progress counters.
     *
     * <p>This overload uses {@code lastLoginAt} as the update timestamp, sets
     * login count to zero, and initializes the current activity streak to zero.</p>
     *
     * @param id database identifier for the user
     * @param username unique username for sign-in
     * @param passwordHash hashed password value
     * @param createdAt time the account was created
     * @param lastLoginAt time the user last logged in
     * @param totalHintsUsed total number of hints used by the user
     * @param totalChallengesCompleted total number of challenges completed by the user
     */
    public User(int id, String username, String passwordHash, 
                LocalDateTime createdAt, LocalDateTime lastLoginAt,
                int totalHintsUsed, int totalChallengesCompleted) {
        this(id, username, passwordHash, createdAt, lastLoginAt, lastLoginAt, 0,
                totalHintsUsed, totalChallengesCompleted, 0, totalChallengesCompleted * 100, null);
    }

    /**
     * Creates a user loaded from storage with account audit fields.
     *
     * <p>This overload initializes the current activity streak to zero.</p>
     *
     * @param id database identifier for the user
     * @param username unique username for sign-in
     * @param passwordHash hashed password value
     * @param createdAt time the account was created
     * @param lastLoginAt time the user last logged in
     * @param updatedAt time the account record was last updated
     * @param loginCount number of successful logins recorded for the user
     * @param totalHintsUsed total number of hints used by the user
     * @param totalChallengesCompleted total number of challenges completed by the user
     */
    public User(int id, String username, String passwordHash,
                LocalDateTime createdAt, LocalDateTime lastLoginAt,
                LocalDateTime updatedAt, int loginCount,
                int totalHintsUsed, int totalChallengesCompleted) {
        this(id, username, passwordHash, createdAt, lastLoginAt, updatedAt, loginCount,
                totalHintsUsed, totalChallengesCompleted, 0, totalChallengesCompleted * 100, null);
    }

    /**
     * Creates a fully populated user loaded from storage.
     *
     * @param id database identifier for the user
     * @param username unique username for sign-in
     * @param passwordHash hashed password value
     * @param createdAt time the account was created
     * @param lastLoginAt time the user last logged in
     * @param updatedAt time the account record was last updated
     * @param loginCount number of successful logins recorded for the user
     * @param totalHintsUsed total number of hints used by the user
     * @param totalChallengesCompleted total number of challenges completed by the user
     * @param currentActivityStreak current consecutive activity streak count
     */
    public User(int id, String username, String passwordHash,
                LocalDateTime createdAt, LocalDateTime lastLoginAt,
                LocalDateTime updatedAt, int loginCount,
                int totalHintsUsed, int totalChallengesCompleted,
                int currentActivityStreak) {
        this(id, username, passwordHash, createdAt, lastLoginAt, updatedAt, loginCount,
                totalHintsUsed, totalChallengesCompleted, currentActivityStreak,
                totalChallengesCompleted * 100, null);
    }

    // Constructor for LOADING from database (with user data storage fields)
    public User(int id, String username, String passwordHash,
                LocalDateTime createdAt, LocalDateTime lastLoginAt,
                LocalDateTime updatedAt, int loginCount,
                int totalHintsUsed, int totalChallengesCompleted,
                int currentActivityStreak, int experiencePoints,
                String lastPuzzleId) {
        this.id = id;
        this.username = username;
        this.displayName = username;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
        this.updatedAt = updatedAt;
        this.loginCount = loginCount;
        this.totalHintsUsed = totalHintsUsed;
        this.totalChallengesCompleted = totalChallengesCompleted;
        this.currentActivityStreak = currentActivityStreak;
        this.experiencePoints = experiencePoints;
        this.lastPuzzleId = lastPuzzleId;
    }

    public User(int id, String username, String displayName, String passwordHash,
                LocalDateTime createdAt, LocalDateTime lastLoginAt,
                LocalDateTime updatedAt, int loginCount,
                int totalHintsUsed, int totalChallengesCompleted,
                int currentActivityStreak, int experiencePoints,
                String lastPuzzleId) {
        this(id, username, passwordHash, createdAt, lastLoginAt, updatedAt, loginCount,
                totalHintsUsed, totalChallengesCompleted, currentActivityStreak,
                experiencePoints, lastPuzzleId);
        this.displayName = normalizeDisplayName(displayName, username);
    }

    /**
     * Returns the database identifier for the user.
     *
     * @return the user id
     */
    public int getId() { return id; }

    /**
     * Updates the database identifier for the user.
     *
     * @param id the new user id
     */
    public void setId(int id) { this.id = id; }

    /**
     * Returns the username used for sign-in.
     *
     * @return the username
     */
    public String getUsername() { return username; }

    /**
     * Updates the username used for sign-in.
     *
     * @param username the new username
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Returns the display name shown in profile and dashboard UI.
     *
     * @return the display name
     */
    public String getDisplayName() { return displayName; }

    /**
     * Updates the display name shown in profile and dashboard UI.
     *
     * @param displayName the new display name
     */
    public void setDisplayName(String displayName) {
        this.displayName = normalizeDisplayName(displayName, username);
    }

    /**
     * Returns the stored password hash.
     *
     * @return the password hash
     */
    public String getPasswordHash() { return passwordHash; }

    /**
     * Updates the stored password hash.
     *
     * @param passwordHash the new password hash
     */
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    /**
     * Returns when the account was created.
     *
     * @return the account creation timestamp
     */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * Updates when the account was created.
     *
     * @param createdAt the new account creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * Returns when the user last logged in.
     *
     * @return the last login timestamp
     */
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }

    /**
     * Updates when the user last logged in.
     *
     * @param lastLoginAt the new last login timestamp
     */
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    /**
     * Returns when the account record was last updated.
     *
     * @return the last updated timestamp
     */
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    /**
     * Updates when the account record was last updated.
     *
     * @param updatedAt the new last updated timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Returns the number of successful logins recorded for the user.
     *
     * @return the login count
     */
    public int getLoginCount() { return loginCount; }

    /**
     * Updates the number of successful logins recorded for the user.
     *
     * @param loginCount the new login count
     */
    public void setLoginCount(int loginCount) { this.loginCount = loginCount; }

    /**
     * Returns the total number of hints used by the user.
     *
     * @return the total hints used
     */
    public int getTotalHintsUsed() { return totalHintsUsed; }

    /**
     * Updates the total number of hints used by the user.
     *
     * @param totalHintsUsed the new total hints used
     */
    public void setTotalHintsUsed(int totalHintsUsed) { this.totalHintsUsed = totalHintsUsed; }

    /**
     * Returns the total number of challenges completed by the user.
     *
     * @return the total completed challenges
     */
    public int getTotalChallengesCompleted() { return totalChallengesCompleted; }

    /**
     * Updates the total number of challenges completed by the user.
     *
     * @param totalChallengesCompleted the new total completed challenges
     */
    public void setTotalChallengesCompleted(int totalChallengesCompleted) { 
        this.totalChallengesCompleted = totalChallengesCompleted; 
    }

    /**
     * Returns the user's current consecutive activity streak count.
     *
     * @return the current activity streak
     */
    public int getCurrentActivityStreak() { return currentActivityStreak; }

    /**
     * Updates the user's current consecutive activity streak count.
     *
     * @param currentActivityStreak the new activity streak count
     */
    public void setCurrentActivityStreak(int currentActivityStreak) {
        this.currentActivityStreak = currentActivityStreak;
    }

    /**
     * Returns the user's stored experience points.
     *
     * @return the experience points
     */
    public int getExperiencePoints() { return experiencePoints; }

    /**
     * Updates the user's stored experience points.
     *
     * @param experiencePoints the new experience points value
     */
    public void setExperiencePoints(int experiencePoints) {
        this.experiencePoints = Math.max(0, experiencePoints);
    }

    /**
     * Returns the id of the most recently opened puzzle.
     *
     * @return the last puzzle id, or {@code null} if none has been recorded
     */
    public String getLastPuzzleId() { return lastPuzzleId; }

    /**
     * Updates the id of the most recently opened puzzle.
     *
     * @param lastPuzzleId the new last puzzle id
     */
    public void setLastPuzzleId(String lastPuzzleId) { this.lastPuzzleId = lastPuzzleId; }

    /**
     * Increments the total hints used counter by one.
     */
    public void incrementHintsUsed() {
        this.totalHintsUsed++;
    }

    /**
     * Increments the total completed challenges counter by one.
     */
    public void incrementChallengesCompleted() {
        this.totalChallengesCompleted++;
        this.experiencePoints += 100;
    }

    private static String normalizeDisplayName(String displayName, String username) {
        return displayName == null || displayName.isBlank() ? username : displayName.trim();
    }
}
