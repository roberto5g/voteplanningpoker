package com.voteplanningpoker.dto;

public record ResetVotesRequest(
        String roomId,
        String topicId,
        String userName
) {
}
