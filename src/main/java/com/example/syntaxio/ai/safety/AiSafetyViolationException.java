package com.example.syntaxio.ai.safety;

public class AiSafetyViolationException extends RuntimeException {

    public AiSafetyViolationException(String message) {
        super(message);
    }
}
