package com.example.syntaxio.ai.chat;

import com.example.syntaxio.ai.client.LLMClient;
import com.example.syntaxio.model.Challenge;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PuzzlePageAssistantTest {

    @Test
    void replyIncludesChallengeContextCodeAndTestOutput() {
        FakeLLMClient fakeLLM = new FakeLLMClient("Try tracking a running total.");
        PuzzlePageAssistant assistant = new PuzzlePageAssistant(fakeLLM);

        String response = assistant.reply(
                "Why is my loop wrong?",
                challenge(),
                "return 0;",
                "Expected 6, got 0"
        );

        assertAll(
                () -> assertEquals("Try tracking a running total.", response),
                () -> assertTrue(fakeLLM.lastPrompt.contains("Sum of Array")),
                () -> assertTrue(fakeLLM.lastPrompt.contains("Write a method")),
                () -> assertTrue(fakeLLM.lastPrompt.contains("return 0;")),
                () -> assertTrue(fakeLLM.lastPrompt.contains("Expected 6, got 0"))
        );
    }

    @Test
    void replyBlocksDirectAnswerRequestsWithoutCallingLLM() {
        FakeLLMClient fakeLLM = new FakeLLMClient("full answer");
        PuzzlePageAssistant assistant = new PuzzlePageAssistant(fakeLLM);

        String response = assistant.reply("tell me the answer", challenge(), "", "");

        assertAll(
                () -> assertEquals(0, fakeLLM.callCount),
                () -> assertTrue(response.contains("answer") || response.contains("solution"))
        );
    }

    @Test
    void replyPromptIncludesSafetyInstructions() {
        FakeLLMClient fakeLLM = new FakeLLMClient("hint");
        PuzzlePageAssistant assistant = new PuzzlePageAssistant(fakeLLM);

        assistant.reply("Give me a hint", challenge(), "", "");

        assertTrue(fakeLLM.lastPrompt.contains("Safety guardrails"));
    }

    private Challenge challenge() {
        return new Challenge(
                "ch-001",
                "Sum of Array",
                "Write a method that sums an array.",
                "public int sumArray(int[] numbers) { return 0; }",
                "EASY",
                List.of(),
                "return 1;"
        );
    }

    private static class FakeLLMClient implements LLMClient {
        private final String response;
        private String lastPrompt;
        private int callCount;

        FakeLLMClient(String response) {
            this.response = response;
        }

        @Override
        public String generate(String prompt) {
            this.lastPrompt = prompt;
            this.callCount++;
            return response;
        }
    }
}
