package com.voteplanningpoker.dto;

import java.util.List;

public record CreateRoomRequest(
        String userName,
        String roomName,
        List<Integer> allowedVotes) {
}
