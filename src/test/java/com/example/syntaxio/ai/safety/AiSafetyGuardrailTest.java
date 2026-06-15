package com.example.syntaxio.ai.safety;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiSafetyGuardrailTest {

    private final AiSafetyGuardrail guardrail = new AiSafetyGuardrail();

    @Test
    void assessUserMessageAllowsProgrammingQuestions() {
        GuardrailDecision decision = guardrail.assessUserMessage("Can you explain how a for loop works?");

        assertTrue(decision.permitted());
    }

    @Test
    void assessUserMessageBlocksDirectAnswerRequests() {
        GuardrailDecision decision = guardrail.assessUserMessage("tell me the answer");

        assertAll(
                () -> assertFalse(decision.permitted()),
                () -> assertFalse(decision.safeResponse().isBlank()),
                () -> assertTrue(decision.safeResponse().contains("answer")
                        || decision.safeResponse().contains("solution"))
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
}
