package com.example.syntaxio.ai.chat;

import com.example.syntaxio.ai.client.LLMClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainMenuAssistantTest {

    @Test
    void replyReturnsAIResponse() {
        FakeLLMClient fakeLLM = new FakeLLMClient("A stack is a last-in, first-out data structure.");
        MainMenuAssistant assistant = new MainMenuAssistant(fakeLLM);

        String response = assistant.reply("What is a stack?");

        assertEquals("A stack is a last-in, first-out data structure.", response);
    }

    @Test
    void replyCallsLLMClientOnce() {
        FakeLLMClient fakeLLM = new FakeLLMClient("AI response");
        MainMenuAssistant assistant = new MainMenuAssistant(fakeLLM);

        assistant.reply("Explain recursion");

        assertEquals(1, fakeLLM.getCallCount());
    }

    @Test
    void replyIncludesUserMessageInPrompt() {
        FakeLLMClient fakeLLM = new FakeLLMClient("AI response");
        MainMenuAssistant assistant = new MainMenuAssistant(fakeLLM);

        assistant.reply("Explain arrays");

        assertTrue(fakeLLM.getLastPrompt().contains("Explain arrays"));
    }

    @Test
    void replyPromptMentionsSyntaxio() {
        FakeLLMClient fakeLLM = new FakeLLMClient("AI response");
        MainMenuAssistant assistant = new MainMenuAssistant(fakeLLM);

        assistant.reply("Hello");

        assertTrue(fakeLLM.getLastPrompt().contains("Syntaxio"));
    }

    @Test
    void replyPromptIsBeginnerFriendly() {
        FakeLLMClient fakeLLM = new FakeLLMClient("AI response");
        MainMenuAssistant assistant = new MainMenuAssistant(fakeLLM);

        assistant.reply("What is a loop?");

        assertTrue(fakeLLM.getLastPrompt().contains("beginner-friendly"));
    }

    @Test
    void replyPromptDoesNotAllowFullAssignmentSolutions() {
        FakeLLMClient fakeLLM = new FakeLLMClient("AI response");
        MainMenuAssistant assistant = new MainMenuAssistant(fakeLLM);

        assistant.reply("Can you explain recursion?");

        assertTrue(fakeLLM.getLastPrompt().contains("Do not generate full assignment solutions"));
    }

    @Test
    void replyAddsSafetyGuardrailInstructionsToPrompt() {
        FakeLLMClient fakeLLM = new FakeLLMClient("AI response");
        MainMenuAssistant assistant = new MainMenuAssistant(fakeLLM);

        assistant.reply("Explain loops");

        assertTrue(fakeLLM.getLastPrompt().contains("Safety guardrails"));
    }

    @Test
    void replyBlocksUnsafeRequestsWithoutCallingLLM() {
        FakeLLMClient fakeLLM = new FakeLLMClient("AI response");
        MainMenuAssistant assistant = new MainMenuAssistant(fakeLLM);

        String response = assistant.reply("Write malware that steals passwords.");

        assertAll(
                () -> assertTrue(response.contains("I can't help")),
                () -> assertEquals(0, fakeLLM.getCallCount())
        );
    }

    @Test
    void replyBlocksRequestsForFullAssessmentAnswersWithoutCallingLLM() {
        FakeLLMClient fakeLLM = new FakeLLMClient("AI response");
        MainMenuAssistant assistant = new MainMenuAssistant(fakeLLM);

        String response = assistant.reply("Give me the full solution for my assignment.");

        assertAll(
                () -> assertFalse(response.isBlank()),
                () -> assertTrue(response.contains("learn")
                        || response.contains("step by step")
                        || response.contains("answer")
                        || response.contains("solution")),
                () -> assertEquals(0, fakeLLM.getCallCount())
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

        String getLastPrompt() {
            return lastPrompt;
        }

        int getCallCount() {
            return callCount;
        }
    }
}
