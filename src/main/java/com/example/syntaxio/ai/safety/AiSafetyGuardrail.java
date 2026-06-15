package com.example.syntaxio.ai.safety;

import java.util.Locale;

public class AiSafetyGuardrail {

    public static final String SAFETY_INSTRUCTIONS = """
            Safety guardrails:
            - Stay focused on beginner-friendly programming education.
            - Do not complete homework, exams, or assessments for the user.
            - Do not provide full final code or direct answers. Offer hints, explanations, debugging guidance, or learning steps instead.
            - Do not help with violence, self-harm, harassment, sexual content, malware, credential theft, or other harmful activity.
            - If a request violates these rules, refuse briefly and redirect to safe coding help.
            """;

    private static final String REFUSAL_MESSAGE = """
            I can't help with that request. I can still help with programming concepts, hints, debugging steps, or practice problems that support learning.
            """;

    private static final String[] LEARNING_SUPPORT_MESSAGES = {
            "Sorry, to help you learn code more effectively, I can't give you the answer directly. I can guide you step by step and explain the problem.",
            "I can't directly tell you the final answer, but I can walk through the idea with you step by step so you understand how to solve it.",
            "I won't give away the answer, because the goal is to help you learn. I can explain the problem, give hints, and help you build the solution one step at a time.",
            "I can't provide the completed solution, but I can help you reason through the approach and explain each part of the problem.",
            "I can't just hand over the answer, but I can help you learn it properly by breaking the problem down and guiding you through the next step."
    };

    public GuardrailDecision assessUserMessage(String message) {
        if (message == null || message.isBlank()) {
            return GuardrailDecision.blocked("Empty message", "Please ask a coding question so I can help.");
        }

        String normalized = normalize(message);

        if (containsAny(normalized, "suicide", "self harm", "kill myself", "hurt myself")) {
            return GuardrailDecision.blocked("Self-harm request", REFUSAL_MESSAGE);
        }

        if (containsAny(normalized, "build a bomb", "make a bomb", "poison", "weapon", "kill someone")) {
            return GuardrailDecision.blocked("Violence request", REFUSAL_MESSAGE);
        }

        if (containsAny(normalized, "malware", "ransomware", "keylogger", "phishing", "steal password", "ddos")) {
            return GuardrailDecision.blocked("Cyber abuse request", REFUSAL_MESSAGE);
        }

        if (containsAny(normalized, "porn", "sexual content", "explicit sexual")) {
            return GuardrailDecision.blocked("Sexual content request", REFUSAL_MESSAGE);
        }

        if (asksForAcademicShortcut(normalized)) {
            return GuardrailDecision.blocked("Academic shortcut request", learningSupportMessage(normalized));
        }

        return GuardrailDecision.allowed();
    }

    public String applyToPrompt(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new AiSafetyViolationException("Prompt cannot be empty.");
        }

        return SAFETY_INSTRUCTIONS + System.lineSeparator() + prompt;
    }

    public String safeChatResponse(String response) {
        if (response == null || response.isBlank()) {
            return "I couldn't generate a helpful response. Please try asking the question another way.";
        }

        return assessModelContent(response).permitted() ? response : REFUSAL_MESSAGE;
    }

    private GuardrailDecision assessModelContent(String response) {
        if (response == null || response.isBlank()) {
            return GuardrailDecision.blocked("Empty model response", REFUSAL_MESSAGE);
        }

        String normalized = normalize(response);
        if (containsAny(normalized, "malware", "ransomware", "keylogger", "phishing", "steal password")) {
            return GuardrailDecision.blocked("Unsafe model response", REFUSAL_MESSAGE);
        }

        if (containsAny(normalized, "build a bomb", "make a bomb", "kill someone")) {
            return GuardrailDecision.blocked("Unsafe model response", REFUSAL_MESSAGE);
        }

        return GuardrailDecision.allowed();
    }

    private static boolean asksForAcademicShortcut(String normalized) {
        return containsAny(normalized, "do my assignment", "finish my assignment", "complete my assignment",
                "write my homework", "do my homework", "take my exam", "take my quiz")
                || containsAny(normalized, "give me the full solution", "write the full solution",
                "solve this for me", "just give me the answer", "tell me the answer",
                "give me the answer", "show me the answer", "what is the answer",
                "give me the solution", "show me the solution", "write the solution",
                "write the code for me", "give me the code", "show me the code");
    }

    private static String learningSupportMessage(String seed) {
        int index = Math.floorMod(normalize(seed).hashCode(), LEARNING_SUPPORT_MESSAGES.length);
        return LEARNING_SUPPORT_MESSAGES[index];
    }

    private static boolean containsAny(String text, String... unsafeTerms) {
        for (String unsafeTerm : unsafeTerms) {
            if (text.contains(unsafeTerm)) {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String text) {
        return text.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }
}
