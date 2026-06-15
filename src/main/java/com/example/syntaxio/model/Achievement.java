package com.example.syntaxio.model;

/**
 * Represents a user-facing achievement displayed in progress and profile views.
 *
 * @param id stable identifier for the achievement
 * @param title short display name for the achievement
 * @param description explanation of how the achievement is earned
 * @param unlocked whether the current user has earned the achievement
 */
public record Achievement(
        String id,
        String title,
        String description,
        boolean unlocked
) {
}
