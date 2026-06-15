package com.example.syntaxio.model;

import java.time.LocalDateTime;

/**
 * Represents a saved draft for a challenge a user has started but not completed.
 *
 * <p>The record keeps the draft code with the user and challenge identifiers so
 * work can be restored when the learner returns to the challenge.</p>
 */
public class InProgressChallenge {
    private final int userId;
    private final String challengeId;
    private final String draftCode;
    private final LocalDateTime startedAt;
    private final LocalDateTime updatedAt;

    /**
     * Creates an in-progress challenge draft snapshot.
     *
     * @param userId identifier of the user who owns the draft
     * @param challengeId identifier of the challenge being attempted
     * @param draftCode latest saved source code for the challenge
     * @param startedAt time the user first started the challenge
     * @param updatedAt time the draft was last saved
     */
    public InProgressChallenge(int userId, String challengeId, String draftCode,
                               LocalDateTime startedAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.challengeId = challengeId;
        this.draftCode = draftCode;
        this.startedAt = startedAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Returns the identifier of the user who owns the draft.
     *
     * @return the user id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Returns the identifier of the challenge being attempted.
     *
     * @return the challenge id
     */
    public String getChallengeId() {
        return challengeId;
    }

    /**
     * Returns the latest saved source code for the challenge.
     *
     * @return the draft code
     */
    public String getDraftCode() {
        return draftCode;
    }

    /**
     * Returns when the user first started the challenge.
     *
     * @return the start timestamp
     */
    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    /**
     * Returns when the draft was last saved.
     *
     * @return the last updated timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
