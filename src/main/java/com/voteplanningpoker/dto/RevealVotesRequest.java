package com.voteplanningpoker.dto;

public record RevealVotesRequest(
        String roomId,
        String topicId,
        String userName
) {
}
