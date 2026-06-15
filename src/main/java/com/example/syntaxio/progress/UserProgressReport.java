package com.example.syntaxio.progress;

import java.util.List;

public record UserProgressReport(
        List<String> attemptSummaryLines,
        List<String> submissionHistoryLines,
        List<String> hintLogLines
) {
}
