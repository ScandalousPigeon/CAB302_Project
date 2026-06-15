package com.example.syntaxio.database;

import com.example.syntaxio.model.UserPreferences;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqliteUserPreferencesDAOTest {
    private static final int USER_ID = 1;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        SqliteConnection.closeConnection();
        System.setProperty(
                SqliteConnection.DATABASE_PATH_PROPERTY,
                tempDir.resolve("syntaxio-test.db").toString()
        );
    }

    @AfterEach
    void tearDown() {
        SqliteConnection.closeConnection();
        System.clearProperty(SqliteConnection.DATABASE_PATH_PROPERTY);
    }

    @Test
    void getOrCreatePreferencesReturnsDefaults() {
        SqliteUserPreferencesDAO dao = new SqliteUserPreferencesDAO();

        UserPreferences preferences = dao.getOrCreatePreferences(USER_ID);

        assertAll(
                () -> assertEquals(USER_ID, preferences.getUserId()),
                () -> assertEquals("EASY", preferences.getDefaultDifficulty()),
                () -> assertEquals("Balanced", preferences.getAssistantDetailLevel()),
                () -> assertTrue(preferences.isShowSolvedChallenges())
        );
    }

    @Test
    void savePreferencesUpdatesExistingRow() {
        SqliteUserPreferencesDAO dao = new SqliteUserPreferencesDAO();

        assertTrue(dao.savePreferences(new UserPreferences(USER_ID, "HARD", "Detailed", false)));
        UserPreferences saved = dao.getOrCreatePreferences(USER_ID);

        assertAll(
                () -> assertEquals("HARD", saved.getDefaultDifficulty()),
                () -> assertEquals("Detailed", saved.getAssistantDetailLevel()),
                () -> assertFalse(saved.isShowSolvedChallenges())
        );
    }
}
