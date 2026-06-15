package com.example.syntaxio.model;

/**
 * Represents one validation case for a coding challenge.
 *
 * <p>A test case stores the input supplied to a submitted solution, the expected
 * output, the actual output captured after execution, and whether the case
 * passed.</p>
 */
public class TestCase {
    private String description;
    private String input;
    private String expectedOutput;
    private boolean passed;
    private String actualOutput;

    /**
     * Creates a test case with its expected behavior.
     *
     * <p>New test cases default to not passed and an empty actual output until a
     * submitted solution is executed.</p>
     *
     * @param description short explanation of what the test case verifies
     * @param input input supplied to the submitted code
     * @param expectedOutput expected output for the supplied input
     */
    public TestCase(String description, String input, String expectedOutput) {
        this.description = description;
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.passed = false;
        this.actualOutput = "";
    }

    /**
     * Creates an empty test case for frameworks that require a no-argument constructor.
     */
    public TestCase() {}

    /**
     * Returns the short explanation of what this test case verifies.
     *
     * @return the test case description
     */
    public String getDescription() { return description; }

    /**
     * Updates the short explanation of what this test case verifies.
     *
     * @param description the new test case description
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Returns the input supplied to the submitted code.
     *
     * @return the test input
     */
    public String getInput() { return input; }

    /**
     * Updates the input supplied to the submitted code.
     *
     * @param input the new test input
     */
    public void setInput(String input) { this.input = input; }

    /**
     * Returns the expected output for the supplied input.
     *
     * @return the expected output
     */
    public String getExpectedOutput() { return expectedOutput; }

    /**
     * Updates the expected output for the supplied input.
     *
     * @param expectedOutput the new expected output
     */
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }

    /**
     * Returns whether this test case passed during the latest execution.
     *
     * @return {@code true} if the test passed
     */
    public boolean isPassed() { return passed; }

    /**
     * Updates whether this test case passed during execution.
     *
     * @param passed {@code true} when the test passed
     */
    public void setPassed(boolean passed) { this.passed = passed; }

    /**
     * Returns the actual output captured from the submitted code.
     *
     * @return the actual output
     */
    public String getActualOutput() { return actualOutput; }

    /**
     * Updates the actual output captured from the submitted code.
     *
     * @param actualOutput the new actual output
     */
    public void setActualOutput(String actualOutput) { this.actualOutput = actualOutput; }    
}
