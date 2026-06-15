package com.example.syntaxio.ui.controller;

import com.example.syntaxio.ai.chat.PuzzlePageAssistant;
import com.example.syntaxio.ai.client.OllamaClient;
import com.example.syntaxio.database.SessionManager;
import com.example.syntaxio.database.SqliteChallengeDAO;
import com.example.syntaxio.database.SqliteHintDAO;
import com.example.syntaxio.database.SqliteInProgressChallengeDAO;
import com.example.syntaxio.database.SqliteSolutionDAO;
import com.example.syntaxio.model.Challenge;
import com.example.syntaxio.model.Hint;
import com.example.syntaxio.model.InProgressChallenge;
import com.example.syntaxio.model.Solution;
import com.example.syntaxio.model.TestCase;
import com.example.syntaxio.model.User;
import com.example.syntaxio.runner.CodeExecutor;
import com.example.syntaxio.ui.util.ScreenManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class CodingChallengeController {
    private static final double MIN_ASSISTANT_MESSAGE_WIDTH = 220.0;
    private static final double MAX_ASSISTANT_MESSAGE_WIDTH = 470.0;
    private static final double ASSISTANT_MESSAGE_WIDTH_RATIO = 0.84;

    static final String MAIN_MENU_FXML = "/com/example/syntaxio/main-menu.fxml";
    static final double MAIN_MENU_WIDTH = 1200;
    static final double MAIN_MENU_HEIGHT = 1150;
    static final String DASHBOARD_FXML = "/com/example/syntaxio/dashboard.fxml";
    static final double DASHBOARD_WIDTH = 1200;
    static final double DASHBOARD_HEIGHT = 800;

    private static String currentChallengeId = "ch-001";

    public static void setCurrentChallengeId(String id) {
        currentChallengeId = id;
    }

    @FunctionalInterface
    interface ScreenSwitcher {
        void switchScreen(ActionEvent event, String fxmlPath, double width, double height) throws IOException;
    }

    @FXML private Label titleLabel;
    @FXML private Label difficultyLabel;
    @FXML private TextArea descriptionArea;
    @FXML private TextArea codeEditor;
    @FXML private TextArea outputArea;
    @FXML private Button runButton;
    @FXML private Button submitButton;
    @FXML private Button backButton;
    @FXML private VBox testResultsContainer;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label timeIndicator;
    @FXML private ToggleButton descriptionTab;
    @FXML private ToggleButton aiAssistantTab;
    @FXML private VBox aiAssistantView;
    @FXML private ScrollPane assistantScrollPane;
    @FXML private VBox assistantMessages;
    @FXML private TextField assistantInput;
    @FXML private Button assistantSendButton;

    private SqliteChallengeDAO challengeDAO;
    private SqliteSolutionDAO solutionDAO;
    private SqliteInProgressChallengeDAO inProgressChallengeDAO;
    private SqliteHintDAO hintDAO;
    private SessionManager sessionManager;
    private final PuzzlePageAssistant puzzlePageAssistant = new PuzzlePageAssistant(new OllamaClient());
    private Challenge currentChallenge;
    private ScreenSwitcher screenSwitcher = ScreenManager::switchScreen;
    private Timeline stopwatch;
    private int elapsedSeconds;

    void setScreenSwitcher(ScreenSwitcher screenSwitcher) {
        this.screenSwitcher = screenSwitcher;
    }

    @FXML
    public void initialize() {
        challengeDAO = new SqliteChallengeDAO();
        solutionDAO = new SqliteSolutionDAO();
        inProgressChallengeDAO = new SqliteInProgressChallengeDAO();
        hintDAO = new SqliteHintDAO();
        sessionManager = SessionManager.getInstance();

        loadingIndicator.setVisible(false);
        showDescriptionTab();
        addAssistantMessage(
                "Ask me for hints about this puzzle. I can explain the prompt, review your approach, or help debug a failing test.",
                false
        );

        loadChallenge(currentChallengeId);
    }

    public void loadChallenge(String challengeId) {
        currentChallenge = challengeDAO.getChallengeById(challengeId);
        if (currentChallenge != null) {
            displayChallenge();
            startStopwatch();
        } else {
            showError("Challenge not found!");
        }
    }

    @FXML
    private void showDescriptionTab() {
        setVisibleManaged(descriptionArea, true);
        setVisibleManaged(aiAssistantView, false);
        if (descriptionTab != null) {
            descriptionTab.setSelected(true);
        }
        if (aiAssistantTab != null) {
            aiAssistantTab.setSelected(false);
        }
    }

    @FXML
    private void showAiAssistantTab() {
        setVisibleManaged(descriptionArea, false);
        setVisibleManaged(aiAssistantView, true);
        if (descriptionTab != null) {
            descriptionTab.setSelected(false);
        }
        if (aiAssistantTab != null) {
            aiAssistantTab.setSelected(true);
        }
        scrollAssistantToLatest();
    }

    @FXML
    private void handleAssistantMessage() {
        if (assistantInput == null) {
            return;
        }

        String userMessage = assistantInput.getText().trim();
        if (userMessage.isEmpty()) {
            return;
        }

        assistantInput.clear();
        addAssistantMessage(userMessage, true);
        Label pendingBubble = addAssistantMessage("Thinking...", false);
        setAssistantControlsDisabled(true);

        String currentCode = codeEditor == null ? "" : codeEditor.getText();
        String latestTestOutput = outputArea == null ? "" : outputArea.getText();

        Task<String> replyTask = new Task<>() {
            @Override
            protected String call() {
                return puzzlePageAssistant.reply(userMessage, currentChallenge, currentCode, latestTestOutput);
            }
        };

        replyTask.setOnSucceeded(event -> {
            String reply = replyTask.getValue();
            String safeReply = reply == null || reply.isBlank()
                    ? "I couldn't generate a helpful hint. Try asking the question another way."
                    : reply.trim();
            pendingBubble.setText(safeReply);
            saveAssistantHint(safeReply);
            setAssistantControlsDisabled(false);
            scrollAssistantToLatest();
        });

        replyTask.setOnFailed(event -> {
            pendingBubble.setText("I couldn't reach the AI assistant yet. It may still be starting up. Please try again in a moment.");
            setAssistantControlsDisabled(false);
            scrollAssistantToLatest();
        });

        Thread replyThread = new Thread(replyTask, "coding-challenge-assistant-reply");
        replyThread.setDaemon(true);
        replyThread.start();
    }

    private void displayChallenge() {
        titleLabel.setText(currentChallenge.getTitle());
        difficultyLabel.setText(currentChallenge.getDifficulty());
        difficultyLabel.setStyle("-fx-text-fill: " + currentChallenge.getDifficultyColor() + ";");
        descriptionArea.setText(currentChallenge.getDescription());
        codeEditor.setText(loadDraftCode().orElse(currentChallenge.getStarterCode()));

        testResultsContainer.getChildren().clear();
        outputArea.clear();
        saveCurrentProgress();
    }

    private void startStopwatch() {
        stopStopwatch();
        elapsedSeconds = 0;
        updateTimerLabel();

        stopwatch = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            elapsedSeconds++;
            updateTimerLabel();
        }));
        stopwatch.setCycleCount(Animation.INDEFINITE);
        stopwatch.play();
    }

    private void stopStopwatch() {
        if (stopwatch != null) {
            stopwatch.stop();
            stopwatch = null;
        }
    }

    private void updateTimerLabel() {
        timeIndicator.setText(formatElapsedTime(elapsedSeconds));
    }

    static String formatElapsedTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @FXML
    private void onRun() {
        String userCode = codeEditor.getText();

        if (userCode.trim().isEmpty()) {
            outputArea.setText("Please write some code first!");
            return;
        }

        loadingIndicator.setVisible(true);
        runButton.setDisable(true);

        new Thread(() -> {
            List<TestCase> results = CodeExecutor.executeTests(userCode, currentChallenge.getTestCases());

            Platform.runLater(() -> {
                displayTestResults(results);
                loadingIndicator.setVisible(false);
                runButton.setDisable(false);
            });
        }).start();
    }

    private void displayTestResults(List<TestCase> results) {
        testResultsContainer.getChildren().clear();

        int passedCount = 0;
        StringBuilder output = new StringBuilder();

        for (TestCase test : results) {
            VBox resultCard = new VBox(5);
            resultCard.setStyle("-fx-background-color: " + (test.isPassed() ? "#2ecc7133" : "#e74c3c33") +
                               "; -fx-padding: 10; -fx-background-radius: 5;");

            Label statusLabel = new Label(test.isPassed() ? "✓ PASSED" : "✗ FAILED");
            statusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " +
                                (test.isPassed() ? "#2ecc71" : "#e74c3c") + ";");

            Label descriptionLabel = new Label("Test: " + test.getDescription());
            Label expectedLabel = new Label("Expected: " + test.getExpectedOutput());
            Label actualLabel = new Label("Actual: " + test.getActualOutput());

            resultCard.getChildren().addAll(statusLabel, descriptionLabel, expectedLabel, actualLabel);
            testResultsContainer.getChildren().add(resultCard);

            if (test.isPassed()) passedCount++;

            output.append(test.isPassed() ? "✓ " : "✗ ").append(test.getDescription())
                  .append(" | Expected: ").append(test.getExpectedOutput())
                  .append(" | Got: ").append(test.getActualOutput()).append("\n");
        }

        outputArea.setText("Results: " + passedCount + "/" + results.size() + " tests passed\n\n" + output);
        submitButton.setDisable(passedCount != results.size());
    }

    @FXML
    private void onSubmit(ActionEvent event) throws IOException {
        if (currentChallenge == null) return;

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Submit Solution");
        confirmDialog.setHeaderText("Submit your solution?");
        confirmDialog.setContentText("Once submitted, you can't edit this solution. You can still try the challenge again later.");

        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Solution solution = new Solution(
                currentChallenge.getId(),
                codeEditor.getText(),
                true,
                0
            );

            int userId = sessionManager.getCurrentUser().getId();
            boolean saved = solutionDAO.addSolution(userId, solution);

            if (saved) {
                removeCurrentProgress(userId);
                sessionManager.getCurrentUser().incrementChallengesCompleted();
                sessionManager.getUserDAO().updateUser(sessionManager.getCurrentUser());

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success!");
                successAlert.setHeaderText("Solution Submitted!");
                successAlert.setContentText("Great work! Your solution has been saved.");
                successAlert.showAndWait();

                stopStopwatch();
                screenSwitcher.switchScreen(event, DASHBOARD_FXML, DASHBOARD_WIDTH, DASHBOARD_HEIGHT);
            } else {
                showError("Failed to save solution. Please try again.");
            }
        }
    }

    @FXML
    private void onBack(ActionEvent event) throws IOException {
        saveCurrentProgress();
        stopStopwatch();
        screenSwitcher.switchScreen(event, MAIN_MENU_FXML, MAIN_MENU_WIDTH, MAIN_MENU_HEIGHT);
    }

    private Optional<String> loadDraftCode() {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null || currentChallenge == null) {
            return Optional.empty();
        }

        return inProgressChallengeDAO
                .getInProgressForUserAndChallenge(currentUser.getId(), currentChallenge.getId())
                .map(InProgressChallenge::getDraftCode)
                .filter(draftCode -> !draftCode.isBlank());
    }

    private void saveCurrentProgress() {
        User currentUser = sessionManager == null ? null : sessionManager.getCurrentUser();
        if (currentUser == null || currentChallenge == null || codeEditor == null || inProgressChallengeDAO == null) {
            return;
        }

        inProgressChallengeDAO.saveOrUpdateInProgress(
                currentUser.getId(),
                currentChallenge.getId(),
                codeEditor.getText()
        );
    }

    private void removeCurrentProgress(int userId) {
        if (currentChallenge != null && inProgressChallengeDAO != null) {
            inProgressChallengeDAO.removeInProgress(userId, currentChallenge.getId());
        }
    }

    private Label addAssistantMessage(String message, boolean fromUser) {
        Label bubble = new Label(message);
        bubble.setWrapText(true);
        bubble.setMinHeight(Region.USE_PREF_SIZE);
        bubble.maxWidthProperty().bind(Bindings.createDoubleBinding(
                () -> {
                    double availableWidth = assistantMessages != null && assistantMessages.getWidth() > 0
                            ? assistantMessages.getWidth()
                            : MAX_ASSISTANT_MESSAGE_WIDTH;
                    return Math.max(
                            MIN_ASSISTANT_MESSAGE_WIDTH,
                            Math.min(MAX_ASSISTANT_MESSAGE_WIDTH, availableWidth * ASSISTANT_MESSAGE_WIDTH_RATIO)
                    );
                },
                assistantMessages.widthProperty()
        ));
        bubble.getStyleClass().add(fromUser ? "assistant-user-bubble" : "assistant-bot-bubble");

        HBox row = new HBox(bubble);
        row.setAlignment(fromUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(bubble, Priority.NEVER);
        row.getStyleClass().add(fromUser ? "assistant-user-row" : "assistant-bot-row");

        assistantMessages.getChildren().add(row);
        scrollAssistantToLatest();
        return bubble;
    }

    private void saveAssistantHint(String hintText) {
        User currentUser = sessionManager == null ? null : sessionManager.getCurrentUser();
        if (currentUser == null || currentChallenge == null || hintDAO == null) {
            return;
        }

        hintDAO.saveHint(currentUser.getId(), new Hint(currentChallenge.getId(), hintText, "AI_ASSISTANT", 80));
        currentUser.incrementHintsUsed();
        sessionManager.getUserDAO().updateUser(currentUser);
    }

    private void setAssistantControlsDisabled(boolean disabled) {
        if (assistantInput != null) {
            assistantInput.setDisable(disabled);
        }
        if (assistantSendButton != null) {
            assistantSendButton.setDisable(disabled);
        }
    }

    private void scrollAssistantToLatest() {
        if (assistantScrollPane != null) {
            Platform.runLater(() -> assistantScrollPane.setVvalue(1.0));
        }
    }

    private void setVisibleManaged(Region region, boolean visible) {
        if (region != null) {
            region.setVisible(visible);
            region.setManaged(visible);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
