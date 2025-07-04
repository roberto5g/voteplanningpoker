package com.voteplanningpoker.dto;

public record CreateTopicRequest(
        String roomId,
        String topicTitle,
        String createdBy
) {
}
