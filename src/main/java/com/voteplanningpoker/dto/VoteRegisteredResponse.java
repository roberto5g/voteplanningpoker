package com.voteplanningpoker.dto;

import java.util.Set;

public record VoteRegisteredResponse(
        String roomId,
        Set<String> usersWhoVoted
) {
}
