package com.example.syntaxio.ui.controller;

import javafx.event.ActionEvent;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DashboardControllerTest {

    private static final String DASHBOARD_FXML = "/com/example/syntaxio/dashboard.fxml";

    @Test
    void backToHomeButtonIsWiredToControllerHandler() throws Exception {
        Element backButton = findButtonByText("Back to Home");
        Method handler = DashboardController.class.getDeclaredMethod("onBackToHome", ActionEvent.class);

        assertNotNull(backButton, "Dashboard should have a Back to Home button");
        assertEquals("#onBackToHome", backButton.getAttribute("onAction"));
        assertEquals(void.class, handler.getReturnType());
    }

    @Test
    void dashboardKeepsAchievementLabelsAndBadgeStyles() throws Exception {
        Document document = readFxml(DASHBOARD_FXML);
        String css = readResource("/com/example/syntaxio/css/base.css");

        assertNotNull(findElementByFxId(document, "Label", "achievement1Label"));
        assertNotNull(findElementByFxId(document, "Label", "achievement2Label"));
        assertNotNull(findElementByFxId(document, "Label", "achievement3Label"));
        assertNotNull(findElementByFxId(document, "Label", "achievement4Label"));
        assertTrue(css.contains(".achievement-unlocked"));
        assertTrue(css.contains(".achievement-locked"));
    }

    @Test
    void exportProgressButtonIsWiredToControllerHandler() throws Exception {
        Element exportButton = findButtonByText("Export Progress");
        Method handler = DashboardController.class.getDeclaredMethod("onExportProgress");

        assertNotNull(exportButton, "Dashboard should have an Export Progress button");
        assertEquals("#onExportProgress", exportButton.getAttribute("onAction"));
        assertEquals(void.class, handler.getReturnType());
    }

    @Test
    void reviewProgressButtonIsWiredToControllerHandler() throws Exception {
        Element reviewButton = findButtonByText("Review Progress");
        Method handler = DashboardController.class.getDeclaredMethod("onReviewProgress");

        assertNotNull(reviewButton, "Dashboard should have a Review Progress button");
        assertEquals("#onReviewProgress", reviewButton.getAttribute("onAction"));
        assertEquals(void.class, handler.getReturnType());
    }

    private static Element findButtonByText(String text) throws Exception {
        Document document = readFxml(DASHBOARD_FXML);
        NodeList buttons = document.getElementsByTagName("Button");

        for (int i = 0; i < buttons.getLength(); i++) {
            Element button = (Element) buttons.item(i);
            if (button.getAttribute("text").contains(text)) {
                return button;
            }
        }

        return null;
    }

    private static Element findElementByFxId(Document document, String tagName, String fxId) {
        NodeList elements = document.getElementsByTagName(tagName);

        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            if (fxId.equals(element.getAttribute("fx:id"))) {
                return element;
            }
        }

        return null;
    }

    private static Document readFxml(String path) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        try (InputStream stream = DashboardControllerTest.class.getResourceAsStream(path)) {
            assertNotNull(stream, "Missing test resource: " + path);
            return factory.newDocumentBuilder().parse(stream);
        }
    }

    private static String readResource(String path) throws Exception {
        try (InputStream stream = DashboardControllerTest.class.getResourceAsStream(path)) {
            assertNotNull(stream, "Missing test resource: " + path);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
