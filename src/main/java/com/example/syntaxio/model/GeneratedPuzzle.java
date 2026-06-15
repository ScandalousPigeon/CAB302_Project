package com.example.syntaxio.model;

import java.util.List;

/**
 * Holds a programming puzzle produced by the puzzle generation service.
 *
 * <p>This model is used before a generated puzzle is persisted as a full
 * {@link Challenge}. It contains the learner-facing prompt, starter code, test
 * cases, difficulty label, and reference solution returned by generation.</p>
 */
public class GeneratedPuzzle {

    private final String title;
    private final String description;
    private final String difficulty;
    private final String starterCode;
    private final List<TestCase> testCases;
    private final String modelSolution;

    /**
     * Creates a generated puzzle with all generated fields populated.
     *
     * @param title short display name for the puzzle
     * @param description learner-facing prompt that explains the task
     * @param difficulty difficulty label, usually {@code EASY}, {@code MEDIUM}, or {@code HARD}
     * @param starterCode initial source code loaded into the editor
     * @param testCases test cases used to validate submitted code
     * @param modelSolution reference implementation for the puzzle
     */
    public GeneratedPuzzle(
            String title,
            String description,
            String difficulty,
            String starterCode,
            List<TestCase> testCases,
            String modelSolution
    ) {
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.starterCode = starterCode;
        this.testCases = testCases;
        this.modelSolution = modelSolution;
    }

    /**
     * Returns the short display name for the puzzle.
     *
     * @return the generated puzzle title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the prompt that explains the programming task.
     *
     * @return the generated puzzle description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the generated difficulty label.
     *
     * @return the difficulty label, usually {@code EASY}, {@code MEDIUM}, or {@code HARD}
     */
    public String getDifficulty() {
        return difficulty;
    }

    /**
     * Returns the initial code loaded into the editor.
     *
     * @return the generated starter code
     */
    public String getStarterCode() {
        return starterCode;
    }

    /**
     * Returns the test cases used to check submitted code.
     *
     * @return the generated test cases
     */
    public List<TestCase> getTestCases() {
        return testCases;
    }

    /**
     * Returns the reference implementation for this puzzle.
     *
     * @return the generated model solution
     */
    public String getModelSolution() {
        return modelSolution;
    }
}
