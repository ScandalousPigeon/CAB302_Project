package com.example.syntaxio.ai.chat;

import com.example.syntaxio.ai.client.LLMClient;
import com.example.syntaxio.ai.safety.AiSafetyGuardrail;
import com.example.syntaxio.ai.safety.GuardrailDecision;
import com.example.syntaxio.model.Challenge;

public class PuzzlePageAssistant {
    private final LLMClient llmClient;
    private final AiSafetyGuardrail guardrail;

    public PuzzlePageAssistant(LLMClient llmClient) {
        this(llmClient, new AiSafetyGuardrail());
    }

    PuzzlePageAssistant(LLMClient llmClient, AiSafetyGuardrail guardrail) {
        this.llmClient = llmClient;
        this.guardrail = guardrail;
    }

    public String reply(
            String userMessage,
            Challenge challenge,
            String currentCode,
            String latestTestOutput
    ) {
        GuardrailDecision decision = guardrail.assessUserMessage(userMessage);
        if (!decision.permitted()) {
            return decision.safeResponse();
        }

        String prompt = guardrail.applyToPrompt("""
                You are the AI assistant inside Syntaxio's coding challenge page.

                Help beginner programmers reason through the current puzzle.
                Keep the answer concise and practical.
                Ask one guiding question when useful.
                Give hints, explain concepts, point out likely mistakes, and suggest next steps.
                Do not provide a complete final solution or paste full working code.

                Current challenge title:
                %s

                Difficulty:
                %s

                Challenge description:
                %s

                Starter code:
                %s

                User's current code:
                %s

                Latest test output:
                %s

                User message:
                %s
                """.formatted(
                challenge == null ? "Unknown challenge" : challenge.getTitle(),
                challenge == null ? "Unknown" : challenge.getDifficulty(),
                challenge == null ? "" : challenge.getDescription(),
                challenge == null ? "" : challenge.getStarterCode(),
                currentCode == null ? "" : currentCode,
                latestTestOutput == null || latestTestOutput.isBlank() ? "No test output yet." : latestTestOutput,
                userMessage
        ));

        return guardrail.safeChatResponse(llmClient.generate(prompt));
    }
}
