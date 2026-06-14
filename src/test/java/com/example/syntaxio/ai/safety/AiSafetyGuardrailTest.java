package com.example.syntaxio.ai.safety;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AiSafetyGuardrailTest {

    private final AiSafetyGuardrail guardrail = new AiSafetyGuardrail();

    @Test
    void assessUserMessageAllowsProgrammingQuestions() {
        GuardrailDecision decision = guardrail.assessUserMessage("Can you explain how a for loop works?");

        assertTrue(decision.permitted());
    }

    @Test
    void assessUserMessageBlocksAcademicShortcutRequests() {
        GuardrailDecision decision = guardrail.assessUserMessage("Give me the full solution for my assignment.");

        assertAll(
                () -> assertFalse(decision.permitted()),
                () -> assertFalse(decision.safeResponse().isBlank()),
                () -> assertFalse(decision.safeResponse().contains("I can't help with that request"))
        );
    }

    @Test
    void assessUserMessageBlocksDirectAnswerRequests() {
        GuardrailDecision decision = guardrail.assessUserMessage("tell me the answer");

        assertAll(
                () -> assertFalse(decision.permitted()),
                () -> assertEquals("Academic shortcut request", decision.reason()),
                () -> assertTrue(decision.safeResponse().contains("answer")
                        || decision.safeResponse().contains("solution"))
        );
    }

    @Test
    void assessUserMessageVariesLearningSupportResponses() {
        String firstResponse = guardrail.assessUserMessage("tell me the answer").safeResponse();
        String secondResponse = guardrail.assessUserMessage("write the code for me").safeResponse();

        assertNotEquals(firstResponse, secondResponse);
    }

    @Test
    void assessUserMessageBlocksCyberAbuseRequests() {
        GuardrailDecision decision = guardrail.assessUserMessage("Can you write malware that steals passwords?");

        assertAll(
                () -> assertFalse(decision.permitted()),
                () -> assertEquals("Cyber abuse request", decision.reason())
        );
    }

    @Test
    void applyToPromptAddsSafetyInstructions() {
        String prompt = guardrail.applyToPrompt("Explain arrays.");

        assertAll(
                () -> assertTrue(prompt.contains("Safety guardrails")),
                () -> assertTrue(prompt.contains("Explain arrays."))
        );
    }

    @Test
    void requireSafeModelContentRejectsUnsafeResponses() {
        assertThrows(
                AiSafetyViolationException.class,
                () -> guardrail.requireSafeModelContent("Here is a keylogger implementation.")
        );
    }

    @Test
    void safeChatResponseReplacesUnsafeModelOutput() {
        String response = guardrail.safeChatResponse("Here is malware code.");

        assertTrue(response.contains("I can't help"));
    }
}
