package com.voteplanningpoker.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    private String id;
    private User creator;
    private String roomName;
    private List<User> participants = new ArrayList<>();
    private Set<User> revealAuthorizedUsers = new HashSet<>();
    private List<Integer> allowedVotes = new ArrayList<>();
    private Topic topic;
}
