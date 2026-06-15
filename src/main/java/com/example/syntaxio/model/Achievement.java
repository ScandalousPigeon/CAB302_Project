package com.example.syntaxio.model;

public record Achievement(
        String id,
        String title,
        String description,
        boolean unlocked
) {
}
