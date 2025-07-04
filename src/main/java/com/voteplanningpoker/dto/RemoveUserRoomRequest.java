package com.voteplanningpoker.dto;

public record RemoveUserRoomRequest(
        String roomId,
        String requestedBy,
        String userIdToRemove
) {
}
