package com.voteplanningpoker.dto;

import java.util.List;

public record RoomConfigResponse(
        String roomName,
        String creatorName,
        List<Integer> allowedVotes
) {}
