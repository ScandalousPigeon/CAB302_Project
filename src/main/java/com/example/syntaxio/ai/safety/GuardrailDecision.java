package com.example.syntaxio.ai.safety;

public record GuardrailDecision(boolean permitted, String reason, String safeResponse) {

    public static GuardrailDecision allowed() {
        return new GuardrailDecision(true, null, null);
    }

    public static GuardrailDecision blocked(String reason, String safeResponse) {
        return new GuardrailDecision(false, reason, safeResponse);
    }
}
