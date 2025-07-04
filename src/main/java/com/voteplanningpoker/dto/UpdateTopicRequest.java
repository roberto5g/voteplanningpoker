package com.voteplanningpoker.dto;

public record UpdateTopicRequest(
        String roomId,
        String topicTitle,
        String updatedBy
) {
}
