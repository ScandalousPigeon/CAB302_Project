package com.example.syntaxio.database;

import com.example.syntaxio.model.UserPreferences;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteUserPreferencesDAO {
    private final Connection connection;

    public SqliteUserPreferencesDAO() {
        connection = SqliteConnection.getInstance();
        createTable();
    }

    private void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS user_preferences (
                    userId INTEGER PRIMARY KEY,
                    defaultDifficulty TEXT NOT NULL DEFAULT 'EASY',
                    assistantDetailLevel TEXT NOT NULL DEFAULT 'Balanced',
                    showSolvedChallenges INTEGER NOT NULL DEFAULT 1,
                    updatedAt TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating user preferences table: " + e.getMessage());
        }
    }

    public UserPreferences getOrCreatePreferences(int userId) {
        UserPreferences preferences = findPreferences(userId);
        return preferences == null ? createDefaultPreferences(userId) : preferences;
    }

    public UserPreferences findPreferences(int userId) {
        String sql = "SELECT * FROM user_preferences WHERE userId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapPreferences(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user preferences: " + e.getMessage());
        }
        return null;
    }

    public boolean savePreferences(UserPreferences preferences) {
        String sql = """
                INSERT INTO user_preferences (
                    userId, defaultDifficulty, assistantDetailLevel, showSolvedChallenges, updatedAt
                ) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
                ON CONFLICT(userId) DO UPDATE SET
                    defaultDifficulty = excluded.defaultDifficulty,
                    assistantDetailLevel = excluded.assistantDetailLevel,
                    showSolvedChallenges = excluded.showSolvedChallenges,
                    updatedAt = CURRENT_TIMESTAMP
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, preferences.getUserId());
            pstmt.setString(2, preferences.getDefaultDifficulty());
            pstmt.setString(3, preferences.getAssistantDetailLevel());
            pstmt.setInt(4, preferences.isShowSolvedChallenges() ? 1 : 0);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving user preferences: " + e.getMessage());
        }
        return false;
    }

    private UserPreferences createDefaultPreferences(int userId) {
        UserPreferences preferences = new UserPreferences(userId);
        savePreferences(preferences);
        return preferences;
    }

    private UserPreferences mapPreferences(ResultSet rs) throws SQLException {
        return new UserPreferences(
                rs.getInt("userId"),
                rs.getString("defaultDifficulty"),
                rs.getString("assistantDetailLevel"),
                rs.getInt("showSolvedChallenges") == 1
        );
    }
}
