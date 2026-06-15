package com.example.syntaxio.model;

import java.time.LocalDateTime;

/**
 * Represents a user's submitted solution for a coding challenge.
 *
 * <p>A solution stores the submitted code, whether it passed validation, how many
 * hints were used for the attempt, and when the submission occurred.</p>
 */
public class Solution {
    private String challengeId;
    private String code;
    private boolean passed;
    private int hintsUsed;
    private LocalDateTime submittedAt;

    /**
     * Creates a solution submission and timestamps it with the current time.
     *
     * @param challengeId identifier of the challenge this solution was submitted for
     * @param code submitted source code
     * @param passed whether the submitted code passed validation
     * @param hintsUsed number of hints used before this submission
     */
    public Solution(String challengeId, String code, boolean passed, int hintsUsed) {
        this.challengeId = challengeId;
        this.code = code;
        this.passed = passed;
        this.hintsUsed = hintsUsed;
        this.submittedAt = LocalDateTime.now();
    }

    /**
     * Returns the identifier of the challenge this solution was submitted for.
     *
     * @return the challenge id
     */
    public String getChallengeId() { return challengeId; }

    /**
     * Returns the submitted source code.
     *
     * @return the solution code
     */
    public String getCode() { return code; }

    /**
     * Returns whether the submitted code passed validation.
     *
     * @return {@code true} if the solution passed
     */
    public boolean isPassed() { return passed; }

    /**
     * Returns the number of hints used before this submission.
     *
     * @return hints used for this solution
     */
    public int getHintsUsedForThisSolution() { return hintsUsed; }

    /**
     * Returns when this solution was submitted.
     *
     * @return the submission timestamp
     */
    public LocalDateTime getSubmittedAt() { return submittedAt; }

    /**
     * Updates when this solution was submitted.
     *
     * @param submittedAt the new submission timestamp
     */
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
