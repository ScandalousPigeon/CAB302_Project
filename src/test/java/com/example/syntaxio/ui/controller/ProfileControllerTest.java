package com.example.syntaxio.ui.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfileControllerTest {

    @Test
    void profileFXMLKeepsPreferenceControlsWiredToController() throws IOException, NoSuchFieldException {
        String fxml = readResource("/com/example/syntaxio/profile-page.fxml");

        assertAll(
                () -> assertTrue(fxml.contains("fx:id=\"usernameLabel\"")),
                () -> assertTrue(fxml.contains("fx:id=\"difficultyComboBox\"")),
                () -> assertTrue(fxml.contains("fx:id=\"assistantDetailComboBox\"")),
                () -> assertTrue(fxml.contains("fx:id=\"showSolvedCheckBox\"")),
                () -> assertTrue(fxml.contains("onAction=\"#handleSavePreferences\"")),
                () -> assertTrue(fxml.contains("onAction=\"#handleBack\"")),
                () -> assertEquals(Label.class,
                        ProfileController.class.getDeclaredField("usernameLabel").getType()),
                () -> assertEquals(ComboBox.class,
                        ProfileController.class.getDeclaredField("difficultyComboBox").getType()),
                () -> assertEquals(ComboBox.class,
                        ProfileController.class.getDeclaredField("assistantDetailComboBox").getType()),
                () -> assertEquals(CheckBox.class,
                        ProfileController.class.getDeclaredField("showSolvedCheckBox").getType())
        );
    }

    @Test
    void profileControllerKeepsNavigationHandlersAvailable() throws NoSuchMethodException {
        assertAll(
                () -> assertHandlerExists("handleBack", ActionEvent.class),
                () -> assertHandlerExists("handleSavePreferences")
        );
    }

    private String readResource(String path) throws IOException {
        try (InputStream stream = ProfileControllerTest.class.getResourceAsStream(path)) {
            assertNotNull(stream, "Missing test resource: " + path);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static void assertHandlerExists(String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = ProfileController.class.getDeclaredMethod(methodName, parameterTypes);
        assertTrue(method.getReturnType().equals(void.class));
    }
}
