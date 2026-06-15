package com.example.syntaxio.model;

import java.time.LocalDateTime;

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
    
    // Constructor for NEW user (without ID - database generates it)
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
    
    // Constructor for LOADING from database (with ID)
    public User(int id, String username, String passwordHash, 
                LocalDateTime createdAt, LocalDateTime lastLoginAt,
                int totalHintsUsed, int totalChallengesCompleted) {
        this(id, username, passwordHash, createdAt, lastLoginAt, lastLoginAt, 0,
                totalHintsUsed, totalChallengesCompleted, 0, totalChallengesCompleted * 100, null);
    }

    // Constructor for LOADING from database (with account audit fields)
    public User(int id, String username, String passwordHash,
                LocalDateTime createdAt, LocalDateTime lastLoginAt,
                LocalDateTime updatedAt, int loginCount,
                int totalHintsUsed, int totalChallengesCompleted) {
        this(id, username, passwordHash, createdAt, lastLoginAt, updatedAt, loginCount,
                totalHintsUsed, totalChallengesCompleted, 0, totalChallengesCompleted * 100, null);
    }

    // Constructor for LOADING from database (with progress summary fields)
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
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) {
        this.displayName = normalizeDisplayName(displayName, username);
    }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getLoginCount() { return loginCount; }
    public void setLoginCount(int loginCount) { this.loginCount = loginCount; }
    
    public int getTotalHintsUsed() { return totalHintsUsed; }
    public void setTotalHintsUsed(int totalHintsUsed) { this.totalHintsUsed = totalHintsUsed; }
    
    public int getTotalChallengesCompleted() { return totalChallengesCompleted; }
    public void setTotalChallengesCompleted(int totalChallengesCompleted) { 
        this.totalChallengesCompleted = totalChallengesCompleted; 
    }

    public int getCurrentActivityStreak() { return currentActivityStreak; }
    public void setCurrentActivityStreak(int currentActivityStreak) {
        this.currentActivityStreak = currentActivityStreak;
    }

    public int getExperiencePoints() { return experiencePoints; }
    public void setExperiencePoints(int experiencePoints) {
        this.experiencePoints = Math.max(0, experiencePoints);
    }

    public String getLastPuzzleId() { return lastPuzzleId; }
    public void setLastPuzzleId(String lastPuzzleId) { this.lastPuzzleId = lastPuzzleId; }
    
    // Helper methods
    public void incrementHintsUsed() {
        this.totalHintsUsed++;
    }
    
    public void incrementChallengesCompleted() {
        this.totalChallengesCompleted++;
        this.experiencePoints += 100;
    }

    private static String normalizeDisplayName(String displayName, String username) {
        return displayName == null || displayName.isBlank() ? username : displayName.trim();
    }
}
