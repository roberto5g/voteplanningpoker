package com.voteplanningpoker.dto;

public record VoteRequest(
        String roomId,
        String userName,
        int vote
) {
}
