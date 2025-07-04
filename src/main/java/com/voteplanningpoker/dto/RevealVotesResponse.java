package com.voteplanningpoker.dto;

import java.util.Map;

public record RevealVotesResponse(
        java.util.UUID roomId,
        Map<String, Integer> votes,
        double average,
        int suggested
) {
}
