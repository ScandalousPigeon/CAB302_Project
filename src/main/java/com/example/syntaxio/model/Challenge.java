package com.example.syntaxio.model;

import java.util.List;

/**
 * Represents a programming challenge that can be displayed and attempted in Syntaxio.
 *
 * <p>A challenge contains the prompt shown to the learner, starter code for the
 * editor, an ordered set of {@link TestCase} instances used to check submissions,
 * and an optional model solution that can be used after the learner has completed
 * the exercise.</p>
 *
 * <p>Difficulty is currently stored as a string value. Expected values are
 * {@code EASY}, {@code MEDIUM}, and {@code HARD}.</p>
 */
public class Challenge {
    private String id;
    private String title;
    private String description;
    private String starterCode;
    private String difficulty;  // "EASY", "MEDIUM", "HARD"
    private List<TestCase> testCases;
    private String modelSolution;  // For comparison after completion

    /**
     * Creates a challenge with all fields populated.
     *
     * @param id unique identifier for the challenge
     * @param title short display name shown in challenge lists
     * @param description learner-facing prompt that explains the task
     * @param starterCode initial source code loaded into the editor
     * @param difficulty difficulty label, usually {@code EASY}, {@code MEDIUM}, or {@code HARD}
     * @param testCases test cases used to validate a submitted solution
     * @param modelSolution reference solution available after completion
     */
    public Challenge(String id, String title, String description, 
                     String starterCode, String difficulty, 
                     List<TestCase> testCases, String modelSolution) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.starterCode = starterCode;
        this.difficulty = difficulty;
        this.testCases = testCases;
        this.modelSolution = modelSolution;
    }

    /**
     * Creates an empty challenge for frameworks that require a no-argument constructor.
     */
    public Challenge() {}

    /**
     * Returns the unique challenge identifier.
     *
     * @return the challenge id
     */
    public String getId() { return id; }

    /**
     * Updates the unique challenge identifier.
     *
     * @param id the new challenge id
     */
    public void setId(String id) { this.id = id; }

    /**
     * Returns the short title displayed to users.
     *
     * @return the challenge title
     */
    public String getTitle() { return title; }

    /**
     * Updates the short title displayed to users.
     *
     * @param title the new challenge title
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Returns the prompt that explains the programming task.
     *
     * @return the challenge description
     */
    public String getDescription() { return description; }

    /**
     * Updates the prompt that explains the programming task.
     *
     * @param description the new challenge description
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Returns the initial code loaded into the editor.
     *
     * @return the starter code for this challenge
     */
    public String getStarterCode() { return starterCode; }

    /**
     * Updates the initial code loaded into the editor.
     *
     * @param starterCode the new starter code
     */
    public void setStarterCode(String starterCode) { this.starterCode = starterCode; }

    /**
     * Returns the difficulty label for this challenge.
     *
     * @return the difficulty label, usually {@code EASY}, {@code MEDIUM}, or {@code HARD}
     */
    public String getDifficulty() { return difficulty; }

    /**
     * Updates the difficulty label for this challenge.
     *
     * @param difficulty the new difficulty label
     */
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    /**
     * Returns the test cases used to check learner submissions.
     *
     * @return the challenge test cases
     */
    public List<TestCase> getTestCases() { return testCases; }

    /**
     * Updates the test cases used to check learner submissions.
     *
     * @param testCases the new challenge test cases
     */
    public void setTestCases(List<TestCase> testCases) { this.testCases = testCases; }

    /**
     * Returns the reference implementation for this challenge.
     *
     * @return the model solution
     */
    public String getModelSolution() { return modelSolution; }

    /**
     * Updates the reference implementation for this challenge.
     *
     * @param modelSolution the new model solution
     */
    public void setModelSolution(String modelSolution) { this.modelSolution = modelSolution; }

    /**
     * Returns a hexadecimal color value that represents the challenge difficulty in the UI.
     *
     * @return a difficulty color, or a neutral fallback color for unknown difficulty values
     */
    public String getDifficultyColor() {
        switch (difficulty) {
            case "EASY": return "#4ecdc4";  // Teal
            case "MEDIUM": return "#f9ca24"; // Yellow
            case "HARD": return "#ff6b6b";   // Red
            default: return "#cccccc";
        }
    }    
}
