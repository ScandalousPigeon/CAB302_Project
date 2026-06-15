package com.example.syntaxio.model;

import java.time.LocalDateTime;

/**
 * Represents an assistance request response for a specific coding challenge.
 *
 * <p>A hint stores the text shown to the learner, the type of support requested,
 * an estimated confidence score, whether the learner marked it as helpful, and
 * the time it was requested.</p>
 */
public class Hint {
    private String id;
    private String challengeId;
    private String hintText;
    private String hintType;  // "GENERAL", "PSEUDOCODE", "DOCUMENTATION"
    private int confidence;   // 0-100
    private boolean wasHelpful;
    private LocalDateTime requestedAt;

    /**
     * Creates a hint for a challenge and initializes tracking metadata.
     *
     * <p>The hint id is generated automatically, {@code wasHelpful} defaults to
     * {@code false}, and {@code requestedAt} is set to the current time.</p>
     *
     * @param challengeId identifier of the challenge this hint belongs to
     * @param hintText learner-facing hint content
     * @param hintType hint category, usually {@code GENERAL}, {@code PSEUDOCODE}, or {@code DOCUMENTATION}
     * @param confidence confidence score from 0 to 100
     */
    public Hint(String challengeId, String hintText, String hintType, int confidence) {
        this.id = java.util.UUID.randomUUID().toString();
        this.challengeId = challengeId;
        this.hintText = hintText;
        this.hintType = hintType;
        this.confidence = confidence;
        this.wasHelpful = false;
        this.requestedAt = LocalDateTime.now();
    }

    /**
     * Returns the unique hint identifier.
     *
     * @return the hint id
     */
    public String getId() { return id; }

    /**
     * Updates the unique hint identifier.
     *
     * @param id the new hint id
     */
    public void setId(String id) { this.id = id; }

    /**
     * Returns the challenge identifier associated with this hint.
     *
     * @return the challenge id
     */
    public String getChallengeId() { return challengeId; }

    /**
     * Updates the challenge identifier associated with this hint.
     *
     * @param challengeId the new challenge id
     */
    public void setChallengeId(String challengeId) { this.challengeId = challengeId; }

    /**
     * Returns the learner-facing hint content.
     *
     * @return the hint text
     */
    public String getHintText() { return hintText; }

    /**
     * Updates the learner-facing hint content.
     *
     * @param hintText the new hint text
     */
    public void setHintText(String hintText) { this.hintText = hintText; }

    /**
     * Returns the hint category.
     *
     * @return the hint type
     */
    public String getHintType() { return hintType; }

    /**
     * Updates the hint category.
     *
     * @param hintType the new hint type
     */
    public void setHintType(String hintType) { this.hintType = hintType; }

    /**
     * Returns the confidence score for the hint.
     *
     * @return confidence score from 0 to 100
     */
    public int getConfidence() { return confidence; }

    /**
     * Updates the confidence score for the hint.
     *
     * @param confidence the new confidence score
     */
    public void setConfidence(int confidence) { this.confidence = confidence; }

    /**
     * Returns whether the learner marked this hint as helpful.
     *
     * @return {@code true} if the hint was marked helpful
     */
    public boolean isWasHelpful() { return wasHelpful; }

    /**
     * Updates whether the learner marked this hint as helpful.
     *
     * @param wasHelpful {@code true} when the hint was helpful
     */
    public void setWasHelpful(boolean wasHelpful) { this.wasHelpful = wasHelpful; }

    /**
     * Returns when the hint was requested.
     *
     * @return the request timestamp
     */
    public LocalDateTime getRequestedAt() { return requestedAt; }

    /**
     * Updates when the hint was requested.
     *
     * @param requestedAt the new request timestamp
     */
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    /**
     * Returns a hexadecimal color value that represents the hint confidence in the UI.
     *
     * @return green for high confidence, orange for medium confidence, or red for low confidence
     */
    public String getConfidenceColor() {
        if (confidence >= 70) return "#2ecc71";
        if (confidence >= 40) return "#f39c12";
        return "#e74c3c";
    }

    /**
     * Returns a human-readable confidence label for display.
     *
     * @return a confidence label based on the current confidence score
     */
    public String getConfidenceText() {
        if (confidence >= 70) return "High confidence";
        if (confidence >= 40) return "Medium confidence";
        return "Low confidence - verify carefully";
    }    
}
