package com.example.syntaxio.ui.controller;

import com.example.syntaxio.database.SessionManager;
import com.example.syntaxio.database.SqliteUserPreferencesDAO;
import com.example.syntaxio.model.User;
import com.example.syntaxio.model.UserPreferences;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import java.io.IOException;

import static com.example.syntaxio.ui.util.ScreenManager.switchScreen;

public class ProfileController {
    @FXML
    private Label usernameLabel;

    @FXML
    private ComboBox<String> difficultyComboBox;

    @FXML
    private ComboBox<String> assistantDetailComboBox;

    @FXML
    private CheckBox showSolvedCheckBox;

    @FXML
    private Label statusLabel;

    private SessionManager sessionManager;
    private SqliteUserPreferencesDAO preferencesDAO;

    @FXML
    private void initialize() {
        sessionManager = SessionManager.getInstance();
        preferencesDAO = new SqliteUserPreferencesDAO();

        difficultyComboBox.setItems(FXCollections.observableArrayList("EASY", "MEDIUM", "HARD"));
        assistantDetailComboBox.setItems(FXCollections.observableArrayList("Concise", "Balanced", "Detailed"));

        loadPreferences();
    }

    @FXML
    private void handleSavePreferences() {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            setStatus("Sign in to save preferences.");
            return;
        }

        UserPreferences preferences = new UserPreferences(
                currentUser.getId(),
                difficultyComboBox.getValue(),
                assistantDetailComboBox.getValue(),
                showSolvedCheckBox.isSelected()
        );

        setStatus(preferencesDAO.savePreferences(preferences)
                ? "Preferences saved."
                : "Could not save preferences.");
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        switchScreen(event, "/com/example/syntaxio/main-menu.fxml", 1200, 1000);
    }

    private void loadPreferences() {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            usernameLabel.setText("Guest");
            applyPreferences(new UserPreferences(0));
            setStatus("Sign in to save preferences.");
            return;
        }

        usernameLabel.setText(currentUser.getUsername());
        applyPreferences(preferencesDAO.getOrCreatePreferences(currentUser.getId()));
        setStatus("");
    }

    private void applyPreferences(UserPreferences preferences) {
        difficultyComboBox.setValue(preferences.getDefaultDifficulty());
        assistantDetailComboBox.setValue(preferences.getAssistantDetailLevel());
        showSolvedCheckBox.setSelected(preferences.isShowSolvedChallenges());
    }

    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
}
