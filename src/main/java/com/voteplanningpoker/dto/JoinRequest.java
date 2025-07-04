package com.voteplanningpoker.dto;

import java.util.List;

public record JoinRequest(
        String roomId,
        String userName,
        List<Integer> allowedVotes) {
}
